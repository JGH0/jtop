import java.util.Map;
public class Header {

	private static final String RESET = "\033[0m";
	private static final String HEADER_BG = "\033[44m";  // blue background
	private static final String HEADER_FG = "\033[97m";  // bright white text

	// Draw header on a single line
	public static void draw() {
		try {
			double uptime = Uptime.getSystemUptime('h'); // hours
			String load = CpuInfo.getLoadAverage();
			double cpuUsage = CpuInfo.getCpuUsage(100); // short sample
			double memPercent = MemoryInfo.getMemoryUsage();
			double totalMem = MemoryInfo.getTotalMemoryBytes();
			double usedMem = totalMem * (memPercent / 100.0);

			Map<String, Double> temps = TemperatureInfo.getTemperatures();

			StringBuilder sb = new StringBuilder();
			sb.append(HEADER_BG).append(HEADER_FG);
			sb.append(String.format(" Uptime: %.1fh ", uptime));
			sb.append(String.format("| Load: %s ", load));
			sb.append(String.format("| CPU: %.1f%% ", cpuUsage));
			sb.append(String.format("| Mem: %.1f%% (%.1f/%.1f GB) ",
					memPercent, usedMem / 1e9, totalMem / 1e9));

			int count = 0;
			for (Map.Entry<String, Double> entry : temps.entrySet()) {
				sb.append(String.format("| %s: %.1f°C ", entry.getKey(), entry.getValue()));
				if (++count >= 3) break; // avoid overflowing
			}

			// truncate to terminal width
			int terminalWidth = TerminalSize.getColumns();
			String line = sb.toString();
			if (line.length() > terminalWidth) {
				line = line.substring(0, terminalWidth - 1);
			}

			sb = new StringBuilder(line).append(RESET);
			System.out.println(sb);

		} catch (Exception e) {
			System.out.println(HEADER_BG + HEADER_FG + " Header error: " + e.getMessage() + RESET);
		}
	}
	public static int getRowsCount() {
		return 1;// in future updates the header can be split into multiple lines
	}
}
