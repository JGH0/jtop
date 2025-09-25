import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main{
	public static void main(String[] args) throws IOException, InterruptedException{
		TerminalSize terminalSize = new TerminalSize();
		int pageSize = terminalSize.getRows() -4; // rows visible at a time
		Config config = new Config();
		ShowProcesses showProcesses = new ShowProcesses(
				pageSize,
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
								int arrow = System.in.read();
								switch (arrow){
									case 65: // Up
										showProcesses.scrollUp();
										break;
									case 66: // Down
										showProcesses.scrollDown();
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
		}
	}
}