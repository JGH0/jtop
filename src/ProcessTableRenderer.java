import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for rendering the process table in the terminal.
 * <p>
 * This class handles:
 * <ul>
 *     <li>Color formatting for header, footer, and table rows.</li>
 *     <li>Column alignment based on terminal width and cell size.</li>
 *     <li>Displaying keybindings and scrolling status.</li>
 * </ul>
 * </p>
 */
public class ProcessTableRenderer {
	private final String tableColor;
	private final String headerColor;
	private final String footerColor;
	private final String clearStyling;
	private final String sortingArrowColor;
	private final String keyBindings;

	private final int cellWidth;
	private final int pageSize;

	/**
	 * Initializes the table renderer with configuration and layout settings.
	 *
	 * @param config configuration object containing color settings and footer text
	 * @param cellWidth width of each column in characters
	 * @param pageSize number of rows visible at a time
	 */
	public ProcessTableRenderer(Config config, int cellWidth, int pageSize) {
		this.tableColor = config.getString("table.color", "\033[40m\033[37m");
		this.headerColor = config.getString("header.color", "\033[47m\033[30m");
		this.footerColor = config.getString("footer.color", "\033[41m\033[37m");
		this.clearStyling = "\033[0m";
		this.sortingArrowColor = "\033[31m";
		this.keyBindings = config.getString("footer.text.keybindings", "Use j/k to scroll, Enter to scroll entire row, 'q' or Ctrl+C to quit");
		this.cellWidth = cellWidth;
		this.pageSize = pageSize;
	}

	/**
	 * Draws the process table on the terminal.
	 *
	 * @param processes the list of processes to display
	 * @param infoTypes the columns to show (PID, NAME, CPU, etc.)
	 * @param sortBy the column currently used for sorting
	 * @param sortAsc true if sorting ascending, false if descending
	 * @param scrollIndex starting index for visible rows
	 */
	public void draw(List<ProcessRow> processes, List<InfoType> infoTypes, InfoType sortBy, boolean sortAsc, int scrollIndex) {
		TerminalSize terminalSize = new TerminalSize();
		int total = processes.size();
		int end = Math.min(scrollIndex + pageSize, total);

		// Clear screen
		System.out.print("\033[H\033[2J");
		System.out.flush();

		printHeader(infoTypes, sortBy, sortAsc);
		for (int i = scrollIndex; i < end; i++) {
			printProcessRow(processes.get(i), infoTypes);
		}

		String spaces = " ".repeat(Math.max(0, (terminalSize.getColumns() - 25) / 2));
		System.out.printf("\r%s%s-- Showing %d-%d of %d --%s\n",
				spaces, footerColor, scrollIndex + 1, end, total, clearStyling);
		System.out.print("\r" + keyBindings);
	}

	/**
	 * Prints the table header with sorting indicators.
	 */
	private void printHeader(List<InfoType> infoTypes, InfoType sortBy, boolean sortAsc) {
		List<String> headers = new ArrayList<>();
		for (InfoType type : infoTypes) {
			String name = type.name();
			if (type == InfoType.CPU || type == InfoType.MEMORY) name += " %";
			if (type == sortBy) name += sortAsc ? " ^" : " v";
			headers.add(name);
		}
		printRow(headerColor, headers);
	}

	/**
	 * Prints a single row of process data.
	 */
	private void printProcessRow(ProcessRow row, List<InfoType> infoTypes) {
		List<String> cells = new ArrayList<>();
		for (InfoType type : infoTypes) {
			switch (type) {
				case PID -> cells.add(String.valueOf(row.pid));
				case NAME -> cells.add(row.name);
				case PATH -> cells.add(row.path);
				case USER -> cells.add(row.user);
				case CPU -> cells.add(row.cpu);
				case MEMORY -> cells.add(row.memory);
				case DISK_READ -> cells.add("TODO_R");
				case DISK_WRITE -> cells.add("TODO_W");
				case NETWORK -> cells.add("TODO_NET");
				default -> cells.add("?");
			}
		}
		printRow("", cells);
	}

	/**
	 * Prints a row with the given color and cells.
	 */
	private void printRow(String color, List<String> cells) {
		StringBuilder sb = new StringBuilder();
		for (String c : cells) {
			sb.append(String.format("%-" + cellWidth + "s", truncate(c, cellWidth)));
		}
		System.out.println("\r" + tableColor + color + sb + clearStyling);
	}

	/**
	 * Truncates a string to the given width.
	 */
	private String truncate(String s, int width) {
		if (s == null) return "";
		if (s.length() > width - 1) return s.substring(0, width - 1);
		return s;
	}
}