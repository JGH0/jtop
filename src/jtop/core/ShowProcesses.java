package jtop.core;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.IPathInfo;
import jtop.config.Config;
import jtop.system.CpuInfo;
import jtop.system.MemoryInfo;
import jtop.system.PathInfo;
import jtop.terminal.TerminalSize;

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

		IPathInfo pathInfo = new PathInfo();       // instance for PathInfo
		ICpuInfo cpuInfo = new CpuInfo();          // instance for CpuInfo
		IMemoryInfo memoryInfo = new MemoryInfo();// instance for MemoryInfo

		for (ProcessHandle ph : processes) {
			try {
				rows.add(new ProcessRow(
					ph.pid(),
					safe(pathInfo.getName(ph.pid())),
					safe(pathInfo.getPath(ph.pid())),
					ph.info().user().orElse("Unknown"),
					String.valueOf(cpuInfo.getCpuPercent(ph.pid())),
					String.valueOf(memoryInfo.getMemoryPercent(ph.pid()))
				));
			} catch (IOException e) {
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

		if (cachedProcesses.isEmpty()){
			refreshProcesses();
		};

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
