package jtop.core;

import java.util.ArrayList;
import java.util.List;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.IPathInfo;
import jtop.config.Config;
import jtop.terminal.TerminalSize;
import jtop.system.Feature;
import jtop.system.SystemInfoFactory;

/**
 * Core class responsible for managing, sorting, and displaying running processes.
 * <p>
 * Implements {@link IRefreshable} so it can be refreshed periodically.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Fetch all running processes via {@link ProcessHandle}.</li>
 *     <li>Cache process information for efficient display.</li>
 *     <li>Sort processes based on user-selected criteria (CPU, memory, PID, etc.).</li>
 *     <li>Handle scrolling and pagination for terminal display.</li>
 *     <li>Coordinate with {@link ProcessTableRenderer} to render formatted output.</li>
 * </ul>
 */
public class ShowProcesses implements IRefreshable {
	private final List<InfoType> infoTypes;
	private final Config config = new Config();

	private InfoType sortBy = InfoType.CPU;
	private boolean sortAsc = config.getBoolean("table.sorting.ASC", true);

	private int scrollIndex = 0;
	private int pageSize;
	private int cellWidth;

	private List<ProcessRow> cachedProcesses = new ArrayList<>();

	/**
	 * Constructs a ShowProcesses instance with the specified columns to display.
	 *
	 * @param infos Varargs of {@link InfoType} representing columns to display (PID, NAME, CPU, MEMORY, etc.)
	 */
	public ShowProcesses(InfoType... infos) {
		infoTypes = List.of(infos);
	}

	/**
	 * Refreshes the cached list of process rows.
	 * Fetches all running processes, sorts them according to the current sort criteria,
	 * and builds {@link ProcessRow} objects containing CPU, memory, and other stats.
	 *
	 * @throws Exception if process information cannot be read
	 */
	public void refreshProcesses() throws Exception {
		List<ProcessHandle> processes = new ArrayList<>(ProcessHandle.allProcesses().toList());
		processes.sort(ProcessSorter.getComparator(sortBy, sortAsc));

		List<ProcessRow> rows = new ArrayList<>();

		// dynamically get feature implementations
		ICpuInfo cpuInfo = SystemInfoFactory.getFeature(Feature.CPU)
			.map(f -> (ICpuInfo) f)
			.orElse(null);

		IMemoryInfo memoryInfo = SystemInfoFactory.getFeature(Feature.MEMORY)
			.map(f -> (IMemoryInfo) f)
			.orElse(null);

		IPathInfo pathInfo = SystemInfoFactory.getFeature(Feature.PROCESS)
			.map(f -> (IPathInfo) f)
			.orElse(null);

		for (ProcessHandle ph : processes) {
			try {
				long pid = ph.pid();
				String name = pathInfo != null ? safe(pathInfo.getName(pid)) : "?";
				String path = pathInfo != null ? safe(pathInfo.getPath(pid)) : "?";
				String user = ph.info().user().orElse("Unknown");
				String cpuPercent = cpuInfo != null ? String.valueOf(safeCpu(cpuInfo, pid)) : "?";
				String memPercent = memoryInfo != null ? String.valueOf(safeMemory(memoryInfo, pid)) : "?";

				rows.add(new ProcessRow(pid, name, path, user, cpuPercent, memPercent));
			} catch (Exception e) {
				// ignore processes we cannot read
			}
		}

		cachedProcesses = rows;
	}

	/**
	 * Draws the process table to the terminal.
	 * Calculates terminal size, cell width, and page size.
	 * Automatically refreshes the process cache if empty.
	 *
	 * @throws Exception if rendering or process retrieval fails
	 */
	public void draw() throws Exception {
		TerminalSize terminalSize = new TerminalSize();
		this.pageSize = terminalSize.getRows() - ProcessTableRenderer.getHeaderAndFooterLength();
		this.cellWidth = terminalSize.getColumns() / infoTypes.size();

		if (cachedProcesses.isEmpty()) {
			refreshProcesses();
		}

		new ProcessTableRenderer(config, cellWidth, pageSize)
			.draw(cachedProcesses, infoTypes, sortBy, sortAsc, scrollIndex);
	}

	/**
	 * Scrolls the display up by one row.
	 */
	public void scrollUp() {
		if (scrollIndex > 0) scrollIndex--;
	}

	/**
	 * Scrolls the display down by one row.
	 */
	public void scrollDown() {
		if (scrollIndex + pageSize < cachedProcesses.size()) scrollIndex++;
	}

	/**
	 * Changes the sorting column based on a mouse click's character position.
	 * Toggles ascending/descending if the same column is clicked again.
	 *
	 * @param charPosition Horizontal character position of the click in the terminal
	 * @throws Exception if refreshing processes fails
	 */
	public void changeSortByClick(int charPosition) throws Exception {
		int columnIndex = charPosition / cellWidth;
		changeSort(columnIndex);
	}

	/**
	 * Changes the sorting column based on the index entered.
	 * Toggles ascending/descending if the same column is clicked again.
	 *
	 * @param columnIndex Horizontal character position of the click in the terminal
	 * @throws Exception if refreshing processes fails
	 */
	public void changeSort(int columnIndex) throws Exception {
		if (columnIndex >= 0 && columnIndex < infoTypes.size()) {
			InfoType newSort = infoTypes.get(columnIndex);
			sortAsc = (sortBy == newSort) ? !sortAsc : true;
			sortBy = newSort;
			refreshProcesses();
		}
	}

	/**
	 * Safely returns a non-null string.
	 *
	 * @param s Input string
	 * @return Original string if non-null, otherwise "?"
	 */
	private String safe(String s) {
		return s != null ? s : "?";
	}

	/**
	 * Safely retrieves CPU usage, returns 0.0 if unavailable.
	 */
	private static double safeCpu(ICpuInfo cpu, long pid) {
		try {
			return cpu.getCpuPercent(pid);
		} catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * Safely retrieves memory usage, returns 0.0 if unavailable.
	 */
	private static double safeMemory(IMemoryInfo mem, long pid) {
		try {
			return mem.getMemoryPercent(pid);
		} catch (Exception e) {
			return 0.0;
		}
	}

	/**
	 * Refreshes the process display and cached data.
	 * <p>
	 * Implements {@link IRefreshable#refresh()}, so it can be used with {@link RefreshThread}.
	 */
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