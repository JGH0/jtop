import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MemoryInfo {

	private static final long PAGE_SIZE = 4096; // bytes, typical on Linux

	// --- Get memory percent used by a specific process (by PID) ---
	public static double getMemoryPercent(long pid) throws IOException {
		Path statmPath = Path.of("/proc", String.valueOf(pid), "statm");
		if (!Files.exists(statmPath)) {
			throw new IOException("Process with PID " + pid + " does not exist");
		}

		String statm = Files.readString(statmPath).trim();
		String[] parts = statm.split("\\s+");
		if (parts.length < 2) {
			throw new IOException("Unexpected format in /proc/" + pid + "/statm");
		}

		long rssPages = Long.parseLong(parts[1]);
		long processKb = (rssPages * PAGE_SIZE) / 1024;

		long totalKb = getTotalMemoryKb();
		double percent = (processKb / (double) totalKb) * 100.0;

		return round(percent, 3);
	}

	// --- Get total memory usage percentage of the system ---
	public static double getMemoryUsage() throws IOException {
		Map<String, Long> mem = readMemInfo();

		long total = mem.getOrDefault("MemTotal", 1L);
		// Linux counts these as reclaimable or cached
		long free = mem.getOrDefault("MemFree", 0L)
			+ mem.getOrDefault("Buffers", 0L)
			+ mem.getOrDefault("Cached", 0L)
			+ mem.getOrDefault("SReclaimable", 0L)
			- mem.getOrDefault("Shmem", 0L);

		double usedPercent = 100.0 * (total - free) / total;
		return round(usedPercent, 2);
	}

	// --- Helper: parse /proc/meminfo ---
	private static Map<String, Long> readMemInfo() throws IOException {
		Map<String, Long> mem = new HashMap<>();
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/meminfo"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.split(":");
				if (parts.length >= 2) {
					String key = parts[0].trim();
					String val = parts[1].replaceAll("\\D+", ""); // extract digits only
					if (!val.isEmpty()) {
						mem.put(key, Long.parseLong(val));
					}
				}
			}
		}
		return mem;
	}

	// --- Helper: get MemTotal from /proc/meminfo ---
	private static long getTotalMemoryKb() throws IOException {
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/meminfo"))) {
			String line = br.readLine();
			if (line != null && line.startsWith("MemTotal")) {
				return Long.parseLong(line.replaceAll("\\D+", ""));
			}
		}
		throw new IOException("Cannot read total memory from /proc/meminfo");
	}

	// --- Helper: round to n decimals ---
	private static double round(double val, int decimals) {
		double factor = Math.pow(10, decimals);
		return Math.round(val * factor) / factor;
	}

	public static long getTotalMemoryBytes() throws IOException {
		for (String line : Files.readAllLines(Path.of("/proc/meminfo"))) {
			if (line.startsWith("MemTotal:")) {
				return Long.parseLong(line.replaceAll("\\D+", "")) * 1024; // kB → bytes
			}
		}
		return 0;
	}

	public static long getAvailableMemoryBytes() throws IOException {
		long total = 0, available = 0;
		for (String line : Files.readAllLines(Path.of("/proc/meminfo"))) {
			if (line.startsWith("MemTotal:"))
				total = Long.parseLong(line.replaceAll("\\D+", "")) * 1024;
			else if (line.startsWith("MemAvailable:"))
				available = Long.parseLong(line.replaceAll("\\D+", "")) * 1024;
		}
		return total - available; // used memory in bytes
	}
}