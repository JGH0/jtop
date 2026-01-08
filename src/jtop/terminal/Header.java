package jtop.terminal;

import java.util.Map;

import jtop.system.linux.SystemSampler;

public class Header {

	private static final String RESET = "\033[0m";
	private static final String HEADER_BG = "\033[44m";
	private static final String HEADER_FG = "\033[97m";

	// draw header using cached SystemSampler values
	public static void draw(SystemSampler sampler, double uptime, String load) {
		try {
			double cpuUsage = sampler.getCpu();
			double memPercent = sampler.getMem();
			double totalMem = sampler.getTotalMemoryBytes(); // you can cache this too
			double usedMem = totalMem * (memPercent / 100.0);

			StringBuilder sb = new StringBuilder();
			sb.append(HEADER_BG).append(HEADER_FG);
			sb.append(String.format(" Uptime: %.1fh ", uptime));
			sb.append(String.format("| Load: %s ", load));
			sb.append(String.format("| CPU: %.1f%% ", cpuUsage));
			sb.append(String.format("| Mem: %.1f%% (%.1f/%.1f GB) ",
					memPercent, usedMem / 1e9, totalMem / 1e9));

			Map<String, Double> temps = sampler.getTemps();
			if (temps != null) {
				int count = 0;
				for (Map.Entry<String, Double> entry : temps.entrySet()) {
					sb.append(String.format("| %s: %.1f°C ", entry.getKey(), entry.getValue()));
					if (++count >= 3) break;
				}
			}

			int terminalWidth = TerminalSize.getColumns();
			String line = sb.length() > terminalWidth ? sb.substring(0, terminalWidth - 1) : sb.toString();
			System.out.println(line + RESET);

		} catch (Exception e) {
			System.out.println(HEADER_BG + HEADER_FG + " Header error: " + e.getMessage() + RESET);
		}
	}

	public static int getRowsCount() {
		return 1;
	}
}