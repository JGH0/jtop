import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MemoryInfo{
	public static double getMemoryPercent(long pid) throws IOException {
		int decimals = 3;

		// Read process memory usage (Resident Set Size)
		String statm = Files.readString(Paths.get("/proc/" + pid + "/statm"));
		String[] parts = statm.split("\\s+");
		long rssPages = Long.parseLong(parts[1]);
		long pageSize = 4096; // usually 4KB
		long processKb = (rssPages * pageSize) / 1024;

		// Read total system memory
		String memInfo = Files.readString(Paths.get("/proc/meminfo"));
		String firstLine = memInfo.split("\n")[0]; // "MemTotal:  16349572 kB"
		long totalKb = Long.parseLong(firstLine.replaceAll("\\D+", ""));

		// Calculate percentage
		double percent = (processKb / (double) totalKb) * 100.0;

		// Round to given decimal places
		double factor = Math.pow(10, decimals);
		return Math.round(percent * factor) / factor;
	}
}