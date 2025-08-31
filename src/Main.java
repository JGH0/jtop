import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main{
public static void main(String[] args) throws IOException, InterruptedException{
	int pageSize = 15; // rows visible at a time
	ShowProcesses sp = new ShowProcesses(
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
		sp.draw();//initial draw

		AtomicBoolean refresh = new AtomicBoolean(true);

		Thread refreshThread = new Thread(() ->{
			while (true){
				try{
					Thread.sleep(2000);
				} catch (InterruptedException e){
					Thread.currentThread().interrupt();
				}
				if (refresh.get()){
					sp.draw();
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
								sp.scrollUp();
									break;
								case 66: // Down
									sp.scrollDown();
									break;
							}
							sp.draw();
							refresh.set(true);
						}
						break;
					case 106: // 'j'
						sp.scrollDown();
						sp.draw();
						refresh.set(true);
						break;
					case 107: // 'k'
						sp.scrollUp();
						sp.draw();
						refresh.set(true);
						break;
					case 13: // Enter
						for (int i = 0; i < pageSize; i++){
							sp.scrollDown();
						}
						sp.draw();
						refresh.set(true);
						break;
					default:
						char ch = (char) c;
						if (ch == 'q' || c == 3){ // 'q' or Ctrl+C
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
