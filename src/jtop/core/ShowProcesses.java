package jtop.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.IPathInfo;
import jtop.Isystem.IUptime;
import jtop.Isystem.ITemperatureInfo;
import jtop.config.Config;
import jtop.terminal.TerminalSize;
import jtop.system.Feature;
import jtop.system.SystemInfoFactory;
import jtop.system.linux.SystemSampler;

/**
 * Core class responsible for managing, sorting, and displaying running processes.
 */
public class ShowProcesses implements IRefreshable {
	private final List<InfoType> infoTypes;
	private final Config config = new Config();

	private InfoType sortBy = InfoType.CPU;
	private boolean sortAsc = config.getBoolean("table.sorting.ASC", false);

	private int scrollIndex = 0;
	private int pageSize;
	private int cellWidth;

	private List<ProcessRow> cachedProcesses = new ArrayList<>();

	// system sampler for cached CPU, memory, temps
	private final SystemSampler sampler = new SystemSampler();

	/**
	 * Constructs a ShowProcesses instance with the specified columns to display.
	 */
	public ShowProcesses(InfoType... infos) {
		infoTypes = List.of(infos);
	}

	/**
	 * Refreshes the cached list of process rows and system sampler.
	 */
	public void refreshProcesses() throws Exception {
		// Fetch system features
		IUptime uptimeInfo = SystemInfoFactory.getFeature(Feature.UPTIME).map(f -> (IUptime) f).orElse(null);
		ICpuInfo cpuInfo = SystemInfoFactory.getFeature(Feature.CPU).map(f -> (ICpuInfo) f).orElse(null);
		IMemoryInfo memoryInfo = SystemInfoFactory.getFeature(Feature.MEMORY).map(f -> (IMemoryInfo) f).orElse(null);
		ITemperatureInfo tempInfo = SystemInfoFactory.getFeature(Feature.TEMPERATURE).map(f -> (ITemperatureInfo) f).orElse(null);
		IPathInfo pathInfo = SystemInfoFactory.getFeature(Feature.PROCESS).map(f -> (IPathInfo) f).orElse(null);

		if (pathInfo instanceof jtop.system.linux.PathInfo pi) {
			pi.clearCache();
		}

		// Update system sampler
		sampler.refresh(cpuInfo, memoryInfo, tempInfo);

		// cache memory usage per process
		Map<Long, Double> memCache = memoryInfo != null ? new HashMap<>() : null;

		// fetch all processes and sort
		List<ProcessHandle> processes = new ArrayList<>(ProcessHandle.allProcesses().toList());
		processes.sort(ProcessSorter.getComparator(sortBy, sortAsc));

		List<ProcessRow> rows = new ArrayList<>(processes.size());

		for (ProcessHandle ph : processes) {
			long pid = ph.pid();
			try {
				String name = pathInfo != null ? safe(pathInfo.getName(pid)) : "?";
				String path = pathInfo != null ? safe(pathInfo.getPath(pid)) : "?";
				String user = ph.info().user().orElse("Unknown");

				String cpuPercent = cpuInfo != null ? String.valueOf(safeCpu(cpuInfo, pid)) : "?";

				String memPercent;
				if (memoryInfo != null) {
					Double cached = memCache.get(pid);
					if (cached == null) {
						double val = safeMemory(memoryInfo, pid);
						memCache.put(pid, val);
						memPercent = String.valueOf(val);
					} else {
						memPercent = String.valueOf(cached);
					}
				} else {
					memPercent = "?";
				}

				rows.add(new ProcessRow(pid, name, path, user, cpuPercent, memPercent));
			} catch (Exception ignored) {}
		}

		cachedProcesses = rows;
	}

	/**
	 * Draws the process table to the terminal using cached system sampler.
	 */
	public void draw() throws Exception {
		TerminalSize terminalSize = new TerminalSize();
		this.pageSize = terminalSize.getRows() - ProcessTableRenderer.getHeaderAndFooterLength();
		this.cellWidth = terminalSize.getColumns() / infoTypes.size();

		if (cachedProcesses.isEmpty()) {
			refreshProcesses();
		}

		double uptime = 0.0;
		String load = "?";

		try {
			IUptime uptimeInfo = SystemInfoFactory.getFeature(Feature.UPTIME).map(f -> (IUptime) f).orElse(null);
			ICpuInfo cpuInfo = SystemInfoFactory.getFeature(Feature.CPU).map(f -> (ICpuInfo) f).orElse(null);
			if (uptimeInfo != null) uptime = uptimeInfo.getSystemUptime('h');
			if (cpuInfo != null) load = cpuInfo.getLoadAverage();
		} catch (Exception ignored) {}

		new ProcessTableRenderer(config, cellWidth, pageSize, sampler)
				.draw(cachedProcesses, infoTypes, sortBy, sortAsc, scrollIndex, uptime, load);
	}

	public void scrollUp() { if (scrollIndex > 0) scrollIndex--; }

	public void scrollDown() {
		if (scrollIndex + pageSize < cachedProcesses.size()) scrollIndex++;
	}

	public void changeSortByClick(int charPosition) throws Exception {
		int columnIndex = charPosition / cellWidth;
		changeSort(columnIndex);
	}

	public void changeSort(int columnIndex) throws Exception {
		if (columnIndex >= 0 && columnIndex < infoTypes.size()) {
			InfoType newSort = infoTypes.get(columnIndex);
			sortAsc = (sortBy == newSort) ? !sortAsc : true;
			sortBy = newSort;
			refreshProcesses();
		}
	}

	private String safe(String s) { return s != null ? s : "?"; }

	private static double safeCpu(ICpuInfo cpu, long pid) {
		try { return cpu.getCpuPercent(pid); } catch (Exception e) { return 0.0; }
	}

	private static double safeMemory(IMemoryInfo mem, long pid) {
		try { return mem.getMemoryPercent(pid); } catch (Exception e) { return 0.0; }
	}

	@Override
	public void refresh() {
		try {
			refreshProcesses();
			draw();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}