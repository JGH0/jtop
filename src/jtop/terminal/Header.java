package jtop.terminal;
import java.util.Map;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.ITemperatureInfo;
import jtop.Isystem.IUptime;
import jtop.system.CpuInfo;
import jtop.system.MemoryInfo;
import jtop.system.TemperatureInfo;
import jtop.system.Uptime;

/**
 * Handles rendering of the terminal interface header.
 * <p>
 * Displays key system information such as uptime, CPU usage, memory usage,
 * load average, and temperatures of available sensors.
 * The header is displayed on a single line with ANSI color formatting.
 * </p>
 */
public class Header {

	/** ANSI reset code to restore default terminal formatting. */
	private static final String RESET = "\033[0m";

	/** ANSI escape code for header background color (blue). */
	private static final String HEADER_BG = "\033[44m";

	/** ANSI escape code for header foreground color (bright white). */
	private static final String HEADER_FG = "\033[97m";

	/**
	 * Draws the header line on the terminal.
	 * <p>
	 * Displays:
	 * </p>
	 * <ul>
	 *	 <li>System uptime in hours</li>
	 *	 <li>Load average from /proc/loadavg</li>
	 *	 <li>CPU usage percentage</li>
	 *	 <li>Memory usage percentage and absolute values (GB)</li>
	 *	 <li>Temperatures of available sensors (up to 3 to avoid overflow)</li>
	 * </ul>
	 * <p>
	 * Automatically truncates the header to the terminal width.
	 * Any errors during data retrieval are caught and displayed as a message.
	 * </p>
	 */
	public static void draw() {
		// create interface instances
		IUptime uptimeInfo = new Uptime();
		ICpuInfo cpuInfo = new CpuInfo();
		IMemoryInfo memoryInfo = new MemoryInfo();
		ITemperatureInfo tempInfo = new TemperatureInfo();

		try {
			double uptime = uptimeInfo.getSystemUptime('h'); // hours
			String load = cpuInfo.getLoadAverage();
			double cpuUsage = cpuInfo.getCpuUsage(100); // short sample
			double memPercent = memoryInfo.getMemoryUsage();
			double totalMem = memoryInfo.getTotalMemoryBytes();
			double usedMem = totalMem * (memPercent / 100.0);

			Map<String, Double> temps = tempInfo.getTemperatures();

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

	/**
	 * Returns the number of terminal rows occupied by the header.
	 * Currently returns 1, but this may change if the header is split into multiple lines.
	 *
	 * @return number of rows occupied by the header
	 */
	public static int getRowsCount() {
		return 1;//in future there may be multi line header
	}
}