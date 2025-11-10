import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CpuInfo{
	private final static int DECIMALS = 3;
	private static long getSystemUptimeSeconds() throws IOException{
		String uptimeStr = Files.readString(Paths.get("/proc/uptime"));
		return (long) Double.parseDouble(uptimeStr.split("\\s+")[0]);
	}
	public static double getCpuPercent(long pid) throws IOException{
		String stat = Files.readString(Paths.get("/proc/" + pid + "/stat"));
		String[] parts = stat.split("\\s+");

		long utime = Long.parseLong(parts[13]);
		long stime = Long.parseLong(parts[14]);
		long totalTime = utime + stime;

		long uptime = getSystemUptimeSeconds();

		double percent = (100d * totalTime / uptime) / Runtime.getRuntime().availableProcessors();

		// Round to specified decimal places
		double factor = Math.pow(10, DECIMALS);
		return Math.round(percent * factor) / factor;
	}
}
