import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowProcesses {
	public enum InfoType {
		PID, NAME, PATH, USER, CPU, MEMORY, DISK_READ, DISK_WRITE, NETWORK
	}
	private final List<InfoType> infoTypes;	// preserves user order
	private int pageSize;					// number of visible rows
	private int scrollIndex = 0;			// top row index for scrolling
	private int cellWidth;					// column width

	Config config = new Config();
	private InfoType sortBy = InfoType.CPU;	// default sort
	private boolean sortAsc = config.getBoolean("table.sorting.ASC", true); // ascending or descending

	private final String keyBindings = config.getString("footer.text.keybindings", "Use j/k to scroll, Enter to scroll entire row, 'q' or Ctrl+C to quit");

	private final String tableColor = config.getString("table.color", "\033[40m" + "\033[37m");
	private final String headerColor = config.getString("header.color", "\033[47m" + "\033[30m");
	private final String footerColor = config.getString("footer.color", "\033[41m" + "\033[37m");
	private final String sortingArrowColor = "\033[31m";
	private final String clearStyling = "\033[0m";

	public ShowProcesses(InfoType... infos) {
		infoTypes = new ArrayList<>();
		for (InfoType info : infos) {
			infoTypes.add(info);
		}
	}

	// Cache of already prepared rows
	private List<ProcessRow> cachedProcesses = new ArrayList<>();

	static class ProcessRow {
		long pid;
		String name;
		String path;
		String user;
		String cpu;
		String memory;
	}

	/** Build the cache of processes (expensive calls only once per refresh). */
	public void refreshProcesses() {
		List<ProcessHandle> processes = new ArrayList<>(ProcessHandle.allProcesses().toList());

		// Sort based on current sort setting
		processes.sort((a, b) -> {
			int cmp = 0;
			try {
				switch (sortBy) {
					case PID: cmp = Long.compare(a.pid(), b.pid()); break;
					case NAME: cmp = safeCompare(PathInfo.getName(a.pid()), PathInfo.getName(b.pid())); break;
					case PATH: cmp = safeCompare(PathInfo.getPath(a.pid()), PathInfo.getPath(b.pid())); break;
					case USER: cmp = safeCompare(a.info().user().orElse(""), b.info().user().orElse("")); break;
					case CPU: cmp = Double.compare(CpuInfo.getCpuPercent(a.pid()), CpuInfo.getCpuPercent(b.pid())); break;
					case MEMORY: cmp = Double.compare(MemoryInfo.getMemoryPercent(a.pid()), MemoryInfo.getMemoryPercent(b.pid())); break;
					default: cmp = 0;
				}
			} catch (Exception e) {
				cmp = 0;
			}
			return sortAsc ? cmp : -cmp;
		});

		// Convert to cached ProcessRow
		List<ProcessRow> rows = new ArrayList<>();
		for (ProcessHandle ph : processes) {
			ProcessRow row = new ProcessRow();
			row.pid = ph.pid();

			try { row.name = truncate(PathInfo.getName(ph.pid())); } catch (Exception e) { row.name = "?"; }
			try { row.path = truncate(PathInfo.getPath(ph.pid())); } catch (Exception e) { row.path = "?"; }
			row.user = ph.info().user().orElse("Unknown");

			try { row.cpu = String.valueOf(CpuInfo.getCpuPercent(ph.pid())); } catch (Exception e) { row.cpu = "?"; }
			try { row.memory = String.valueOf(MemoryInfo.getMemoryPercent(ph.pid())); } catch (IOException e) { row.memory = "?"; }

			rows.add(row);
		}

		this.cachedProcesses = rows;
	}

	private int safeCompare(String a, String b) {
		if (a == null) a = "";
		if (b == null) b = "";
		return a.compareToIgnoreCase(b);
	}

	/** Draws only the visible window starting from scrollIndex */
	public void draw() {
		TerminalSize terminalSize = new TerminalSize();
		// Set page size (-2 for header and footer)
		int headerAndFooterRows = terminalSize.getRows()
				- (2 + ((keyBindings.length() + terminalSize.getColumns() - 1) / terminalSize.getColumns()));
		this.pageSize = headerAndFooterRows;
		// Set column width
		this.cellWidth = terminalSize.getColumns() / infoTypes.size();

		if (cachedProcesses.isEmpty()) {
			refreshProcesses();
		}

		int total = cachedProcesses.size();
		int end = Math.min(scrollIndex + pageSize, total);

		// Clear screen
		System.out.print("\033[H\033[2J");
		System.out.flush();

		// Print header
		List<String> headers = new ArrayList<>();
		for (InfoType type : infoTypes) {
			String name = type.name();
			if (type == InfoType.CPU || type == InfoType.MEMORY) {
				name += " %";
			}
			if (type == sortBy) {
				// Add sorting arrow depending on sortAsc
				String sortingArrow = sortAsc ? " ^" : " v";
				name += sortingArrow; // todo: sortingArrowColor + sortingArrow + headerColor;
			}
			headers.add(name);
		}
		printRow(headerColor, headers);

		// Print visible rows
		for (int i = scrollIndex; i < end; i++) {
			printProcessRow(cachedProcesses.get(i));
		}

		// Footer
		String spaces = " ".repeat(Math.max(0, (terminalSize.getColumns() - 25) / 2));
		System.out.printf("\r" + spaces + footerColor + "-- Showing %d-%d of %d --" + clearStyling + "\n",
				scrollIndex + 1, end, total);
		System.out.print("\r" + keyBindings);
	}

	private void printProcessRow(ProcessRow row) {
		List<String> cells = new ArrayList<>();
		for (InfoType type : infoTypes) {
			switch (type) {
				case PID: cells.add(String.valueOf(row.pid)); break;
				case NAME: cells.add(row.name); break;
				case PATH: cells.add(row.path); break;
				case USER: cells.add(row.user); break;
				case CPU: cells.add(row.cpu); break;
				case MEMORY: cells.add(row.memory); break;
				case DISK_READ: cells.add("TODO_R"); break;
				case DISK_WRITE: cells.add("TODO_W"); break;
				case NETWORK: cells.add("TODO_NET"); break;
				default: cells.add("TODO"); break;
			}
		}
		printRow("", cells);
	}

	private void printRow(String color, List<String> cells) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String c : cells) {
			stringBuilder.append(String.format("%-" + cellWidth + "s", truncate(c, cellWidth)));
		}
		System.out.print("\r" + tableColor);
		System.out.println(color + stringBuilder + clearStyling);
	}

	private String truncate(String str) {
		return truncate(str, cellWidth);
	}

	private String truncate(String str, int width) {
		if (str == null) return "";
		if (str.length() > width - 1) return str.substring(0, width - 1);
		return str;
	}

	/** Scroll up one row */
	public void scrollUp() {
		if (scrollIndex > 0) scrollIndex--;
	}

	/** Scroll down one row */
	public void scrollDown() {
		if (scrollIndex + pageSize < cachedProcesses.size()) scrollIndex++;
	}

	public void changeSortByClick(int charPosition) {
		int columnIndex = charPosition / cellWidth;
		if (columnIndex >= 0 && columnIndex < infoTypes.size()) {
			InfoType newSort = infoTypes.get(columnIndex);
			if (sortBy == newSort) {
				sortAsc = !sortAsc;
			} else {
				sortBy = newSort;
				sortAsc = true;
			}
			refreshProcesses(); // resort cache immediately
		}
	}
}