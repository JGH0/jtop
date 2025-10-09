import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CpuInfo{
	private final static int DECIMALS = 3;
	public static double getCpuPercent(long pid) throws Exception{
		String stat = Files.readString(Paths.get("/proc/" + pid + "/stat"));
		String[] parts = stat.split("\\s+");

		long utime = Long.parseLong(parts[13]);
		long stime = Long.parseLong(parts[14]);
		long totalTime = utime + stime;

		double UptimeSeconds = Uptime.getSystemUptime('s');

		double percent = (100d * totalTime / UptimeSeconds) / Runtime.getRuntime().availableProcessors();

		// Round to specified decimal places
		double factor = Math.pow(10, DECIMALS);
		return Math.round(percent * factor) / factor;
	}

	public static String getLoadAverage() throws IOException {
		return Files.readString(Path.of("/proc/loadavg")).trim();
	}
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