import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class testfile{
	public static void main(String[] args) throws IOException{
		Screen screen = new DefaultTerminalFactory().createScreen();
		screen.startScreen();
		MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

		Table<String> table = new Table<>("PID", "Name", "User", "CPU %");
		table.setSelectAction(() ->{});

		Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
		panel.setLayoutData(LinearLayout.createLayoutData(LinearLayout.Alignment.Center));
		panel.addComponent(table);

		BasicWindow window = new BasicWindow();
		window.setHints(List.of(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));
		window.setComponent(panel);
		gui.addWindow(window);

		int numCores = Runtime.getRuntime().availableProcessors();
		Map<Long, Duration> lastCpuMap = new ConcurrentHashMap<>();

		new Thread(() ->{
			while (true){
				try{
					String selectedPid = null;
					if (!table.getTableModel().getRows().isEmpty() && table.getSelectedRow() >= 0){
						selectedPid = table.getTableModel().getRow(table.getSelectedRow()).get(0);
					}

					table.getTableModel().clear();

					ProcessHandle.allProcesses().forEach(ph ->{
						ProcessHandle.Info info = ph.info();
						String pid = String.valueOf(ph.pid());
						String command = info.command().orElse("Unknown");
						String name = command.substring(command.lastIndexOf("/") + 1);
						String user = info.user().orElse("Unknown");

						// CPU calculation
						Duration totalCpu = info.totalCpuDuration().orElse(Duration.ZERO);
						Duration lastCpu = lastCpuMap.getOrDefault(ph.pid(), Duration.ZERO);
						double cpuPercent = ((totalCpu.toMillis() - lastCpu.toMillis()) / 1000.0) / numCores * 100;
						if (cpuPercent < 0) cpuPercent = 0; // sometimes totalCpu decreases
						lastCpuMap.put(ph.pid(), totalCpu);

						table.getTableModel().addRow(pid, name, user, String.format("%.1f", cpuPercent));
					});

					// Restore selection by PID
					if (selectedPid != null){
						Optional<Integer> rowIndex = findRowByPid(table.getTableModel().getRows(), selectedPid);
						rowIndex.ifPresent(table::setSelectedRow);
					}

					gui.updateScreen();
					Thread.sleep(1000);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		}).start();

		gui.waitForWindowToClose(window);
		screen.stopScreen();
	}

	private static Optional<Integer> findRowByPid(List<List<String>> rows, String pid){
		for (int i = 0; i < rows.size(); i++){
			if (rows.get(i).get(0).equals(pid)){
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}
}
