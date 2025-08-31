import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CpuInfo{
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

		return (100d * totalTime / uptime) / Runtime.getRuntime().availableProcessors();
	}
}
