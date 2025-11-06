import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main{
	public static void main(String[] args) throws IOException, InterruptedException{
		TerminalSize terminalSize = new TerminalSize();
		int pageSize = terminalSize.getRows() -3; // rows visible at a time
		Config config = new Config();
		ShowProcesses showProcesses = new ShowProcesses(
				ShowProcesses.InfoType.PID,
				ShowProcesses.InfoType.NAME,
				ShowProcesses.InfoType.USER,
				ShowProcesses.InfoType.CPU,
				ShowProcesses.InfoType.MEMORY
				//ShowProcesses.InfoType.DISK_READ,
				//ShowProcesses.InfoType.DISK_WRITE
		);

		// Enable raw input (no Enter buffering)
		new ProcessBuilder("sh", "-c", "stty raw -echo </dev/tty").inheritIO().start().waitFor();
		// enable mouse reporting
		System.out.print("\u001B[?1000h");
		System.out.flush();
		try{
			showProcesses.draw();//initial draw

			AtomicBoolean refresh = new AtomicBoolean(true);

			Thread refreshThread = new Thread(() ->{
				while (true){
					try{
						Thread.sleep(2000);
					} catch (InterruptedException e){
						Thread.currentThread().interrupt();
					}
					if (refresh.get()){
						showProcesses.refreshProcesses();
						showProcesses.draw();
					}
				}
			});
			refreshThread.setDaemon(true);
			refreshThread.start();

			while (true){
				int c;
				while ((c = System.in.read()) != -1){
					switch (c){
						case 27: // ESC sequence
							if (System.in.read() == 91){ // '['
								int next = System.in.read();
								switch (next){
									case 65: // Arrow Up
										showProcesses.scrollUp();
										break;
									case 66: // Arrow Down
										showProcesses.scrollDown();
										break;
									case 77: // 'M' → mouse event
										int cb = System.in.read() - 32; // mouse button
										int cx = System.in.read() - 32; // X (column, 1-based)
										int cy = System.in.read() - 32; // Y (row, 1-based)

										// Left click is cb == 0
										if ((cb) == 0 && cy == 1) { // header row (row 1)
											showProcesses.changeSortByClick(cx - 1); // convert to 0-based column char index
										}
										break;
								}
								showProcesses.draw();
								refresh.set(true);
							}
							break;
						case 106: // 'j'
							showProcesses.scrollDown();
							showProcesses.draw();
							refresh.set(true);
							break;
						case 107: // 'k'
							showProcesses.scrollUp();
							showProcesses.draw();
							refresh.set(true);
							break;
						case 13: // Enter
							for (int i = 0; i < pageSize; i++){
								showProcesses.scrollDown();
							}
							showProcesses.draw();
							refresh.set(true);
							break;
						default:
							if (c == 113 || c == 3){ // 'q' or Ctrl+C
								return;
							}
							break;
					}
				}
			}
		} catch (IOException e){
			// Ctrl+C was pressed
			return;
		} finally{
			// restore normal terminal
			new ProcessBuilder("sh", "-c", "stty sane </dev/tty").inheritIO().start().waitFor();
			// disable mouse reporting
			System.out.print("\u001B[?1000l");
			System.out.flush();

		}
	}
}