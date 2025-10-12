import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowProcesses {
	private final List<InfoType> infoTypes;
	private final Config config = new Config();

	private InfoType sortBy = InfoType.CPU;
	private boolean sortAsc = config.getBoolean("table.sorting.ASC", true);

	private int scrollIndex = 0;
	private int pageSize;
	private int cellWidth;

	private List<ProcessRow> cachedProcesses = new ArrayList<>();

	public ShowProcesses(InfoType... infos) {
		infoTypes = List.of(infos);
	}

	public void refreshProcesses() throws Exception {
		List<ProcessHandle> processes = new ArrayList<>(ProcessHandle.allProcesses().toList());
		processes.sort(ProcessSorter.getComparator(sortBy, sortAsc));

		List<ProcessRow> rows = new ArrayList<>();
		for (ProcessHandle ph : processes) {
			try {
				rows.add(new ProcessRow(
					ph.pid(),
					safe(PathInfo.getName(ph.pid())),
					safe(PathInfo.getPath(ph.pid())),
					ph.info().user().orElse("Unknown"),
					String.valueOf(CpuInfo.getCpuPercent(ph.pid())),
					String.valueOf(MemoryInfo.getMemoryPercent(ph.pid()))
				));
			} catch (IOException e) {
				// ignore
			}
		}
		cachedProcesses = rows;
	}

	public void draw() throws Exception {
		TerminalSize terminalSize = new TerminalSize();
		this.pageSize = terminalSize.getRows() - 3;
		this.cellWidth = terminalSize.getColumns() / infoTypes.size();

		if (cachedProcesses.isEmpty()) refreshProcesses();

		new ProcessTableRenderer(config, cellWidth, pageSize)
			.draw(cachedProcesses, infoTypes, sortBy, sortAsc, scrollIndex);
	}

	public void scrollUp() { if (scrollIndex > 0) scrollIndex--; }
	public void scrollDown() { if (scrollIndex + pageSize < cachedProcesses.size()) scrollIndex++; }

	public void changeSortByClick(int charPosition) throws Exception {
		int columnIndex = charPosition / cellWidth;
		if (columnIndex >= 0 && columnIndex < infoTypes.size()) {
			InfoType newSort = infoTypes.get(columnIndex);
			sortAsc = (sortBy == newSort) ? !sortAsc : true;
			sortBy = newSort;
			refreshProcesses();
		}
	}

	private String safe(String s) { return s != null ? s : "?"; }
}