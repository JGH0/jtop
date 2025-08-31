import java.util.ArrayList;
import java.util.List;

public class ShowProcesses{
	public enum InfoType{
		PID, NAME, PATH, USER, CPU, MEMORY, DISK_READ, DISK_WRITE, NETWORK
	}

	private final List<InfoType> infoTypes;  // preserves user order, allows duplicates
	private final int pageSize;			   // number of visible rows
	private int scrollIndex = 0;			  // top row index for scrolling
	private static final int DEFAULT_CELL_WIDTH = 25; // default column width
	private static final int SINGLE_COL_WIDTH = 10;   // width if only 1 column

	public ShowProcesses(int pageSize, InfoType... infos){
		this.pageSize = pageSize;
		infoTypes = new ArrayList<>();
		for (InfoType info : infos){
			infoTypes.add(info);
		}
	}

	/** Returns mutable sorted list of processes */
	private List<ProcessHandle> getProcesses(){
		List<ProcessHandle> processes = new ArrayList<>(ProcessHandle.allProcesses().toList());
		processes.sort((a, b) -> Long.compare(a.pid(), b.pid()));
		return processes;
	}

	/** Draws only the visible window starting from scrollIndex */
	public void draw(){
		List<ProcessHandle> processes = getProcesses();
		int total = processes.size();
		int end = Math.min(scrollIndex + pageSize, total);

		// Clear screen
		System.out.print("\033[H\033[2J");
		System.out.flush();

		// Print header
		List<String> headers = new ArrayList<>();
		for (InfoType type : infoTypes) headers.add(type.name());
		printRow(headers);

		// Print visible rows
		for (int i = scrollIndex; i < end; i++){
			printProcessRow(processes.get(i));
		}

		// Footer
		System.out.printf("-- Showing %d-%d of %d --\n", scrollIndex + 1, end, total);
		System.out.println("\rUse j/k to scroll, Enter to scroll entire row, 'q' or Ctrl+C to quit");
	}

	private void printProcessRow(ProcessHandle ph){
		try{
			ProcessHandle.Info info = ph.info();
			List<String> row = new ArrayList<>();

			for (InfoType type : infoTypes){
				switch (type){
					case PID: row.add(truncate(String.valueOf(ph.pid()))); break;
					case NAME: row.add(truncate(PathInfo.getName(ph.pid()))); break;
					case PATH: row.add(truncate(PathInfo.getPath(ph.pid()))); break;
					case USER: row.add(truncate(info.user().orElse("Unknown"))); break;
					case CPU: row.add(truncate(String.valueOf(CpuInfo.getCpuPercent(ph.pid())))); break;
					case MEMORY: row.add(truncate(String.valueOf(MemoryInfo.getMemoryKb(ph.pid())))); break;
					case DISK_READ: row.add("TODO_R"); break;
					case DISK_WRITE: row.add("TODO_W"); break;
					case NETWORK: row.add("TODO_NET"); break;
				}
			}
			printRow(row);
		} catch (Exception ignored){}
	}

	private void printRow(List<String> cells){
		StringBuilder sb = new StringBuilder();
		int width = (cells.size() == 1) ? SINGLE_COL_WIDTH : DEFAULT_CELL_WIDTH;

		for (String c : cells){
			sb.append(String.format("%-" + width + "s", truncate(c, width)));
		}

		// Move cursor to start of line and print row
		System.out.print("\r"); 
		System.out.println(sb.toString());
	}


	private String truncate(String str){
		int width = (infoTypes.size() == 1) ? SINGLE_COL_WIDTH : DEFAULT_CELL_WIDTH;
		return truncate(str, width);
	}

	private String truncate(String str, int width){
		if (str.length() > width - 1) return str.substring(0, width - 1);
		return str;
	}

	/** Scroll up one row */
	public void scrollUp(){
		if (scrollIndex > 0) scrollIndex--;
	}

	/** Scroll down one row */
	public void scrollDown(){
		List<ProcessHandle> processes = getProcesses();
		if (scrollIndex + pageSize < processes.size()) scrollIndex++;
	}
}