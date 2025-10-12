package jtop.system;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Provides CPU usage information and statistics for the system and individual processes.
 * <p>
 * Reads data from the <code>/proc</code> filesystem:
 * </p>
 * <ul>
 *	 <li><code>/proc/[pid]/stat</code> for per-process CPU usage</li>
 *	 <li><code>/proc/stat</code> for overall CPU usage</li>
 *	 <li><code>/proc/loadavg</code> for system load average</li>
 * </ul>
 */
public class CpuInfo {

	/** Number of decimal places to round CPU percentage values. */
	private final static int DECIMALS = 3;

	/**
	 * Computes the CPU usage percentage of a specific process.
	 *
	 * @param pid the process ID
	 * @return CPU usage as a percentage, rounded to {@link #DECIMALS} decimal places
	 * @throws Exception if reading the process stat file fails
	 */
	public static double getCpuPercent(long pid) throws Exception {
		String stat = Files.readString(Paths.get("/proc/" + pid + "/stat"));
		String[] parts = stat.split("\\s+");

		long utime = Long.parseLong(parts[13]);
		long stime = Long.parseLong(parts[14]);
		long totalTime = utime + stime;

		double uptimeSeconds = Uptime.getSystemUptime('s');

		double percent = (100d * totalTime / uptimeSeconds) / Runtime.getRuntime().availableProcessors();

		// Round to specified decimal places
		double factor = Math.pow(10, DECIMALS);
		return Math.round(percent * factor) / factor;
	}

	/**
	 * Retrieves the system load average as reported by <code>/proc/loadavg</code>.
	 *
	 * @return the load average string, trimmed
	 * @throws IOException if reading <code>/proc/loadavg</code> fails
	 */
	public static String getLoadAverage() throws IOException {
		return Files.readString(Path.of("/proc/loadavg")).trim();
	}

	/**
	 * Computes the overall CPU usage percentage over a sample period.
	 *
	 * @param sampleMs the sample duration in milliseconds
	 * @return the CPU usage as a percentage over the sample period
	 * @throws IOException if reading <code>/proc/stat</code> fails
	 * @throws InterruptedException if the sleep between samples is interrupted
	 */
	public static double getCpuUsage(long sampleMs) throws IOException, InterruptedException {
		long[] first = readCpuStat();
		Thread.sleep(sampleMs);
		long[] second = readCpuStat();

		long idle1 = first[3];
		long idle2 = second[3];
		long total1 = Arrays.stream(first).sum();
		long total2 = Arrays.stream(second).sum();

		long totalDelta = total2 - total1;
		long idleDelta = idle2 - idle1;
		return 100.0 * (totalDelta - idleDelta) / totalDelta;
	}

	/**
	 * Reads the system-wide CPU statistics from <code>/proc/stat</code>.
	 *
	 * @return an array of CPU time values (user, nice, system, idle, etc.)
	 * @throws IOException if reading <code>/proc/stat</code> fails
	 */
	static long[] readCpuStat() throws IOException {
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/stat"))) {
			String[] parts = br.readLine().trim().split("\\s+");
			long[] vals = new long[parts.length - 1];
			for (int i = 1; i < parts.length; i++)
				vals[i - 1] = Long.parseLong(parts[i]);
			return vals;
		}
	}
}