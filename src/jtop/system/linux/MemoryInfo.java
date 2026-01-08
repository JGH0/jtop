package jtop.system.linux;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jtop.Isystem.IMemoryInfo;

/**
 * Provides methods to gather memory usage statistics.
 * <p>
 * Reads Linux /proc files to determine:
 * </p>
 * <ul>
 *	 <li>Total and available system memory</li>
 *	 <li>Memory usage percentage by the system</li>
 *	 <li>Memory usage percentage of a specific process (by PID)</li>
 * </ul>
 *
 * <p>
 * Performance notes:
 * <ul>
 *	 <li>/proc/meminfo is cached for a short time window</li>
 *	 <li>No regex usage</li>
 *	 <li>No temporary Maps or Lists</li>
 * </ul>
 * </p>
 */
public class MemoryInfo implements IMemoryInfo {

	/** Typical memory page size on Linux in bytes. */
	private static final long PAGE_SIZE = 4096;

	/** Cache validity in milliseconds. */
	private static final long MEMINFO_CACHE_MS = 500;

	private static long lastRead;

	private static long memTotalKb;
	private static long memAvailableKb;
	private static long memFreeKb;
	private static long buffersKb;
	private static long cachedKb;
	private static long sReclaimableKb;
	private static long shmemKb;

	/**
	 * Returns the memory usage percentage of a process.
	 *
	 * @param pid the process ID
	 * @return memory usage percentage of the process
	 * @throws IOException if the /proc file cannot be read or is malformed
	 */
	@Override
	public double getMemoryPercent(long pid) throws IOException {
		readMemInfoCached();

		Path statmPath = Path.of("/proc", String.valueOf(pid), "statm");
		if (!Files.exists(statmPath)) {
			throw new IOException("Process with PID " + pid + " does not exist");
		}

		String statm = Files.readString(statmPath).trim();
		int space = statm.indexOf(' ');
		if (space < 0) {
			throw new IOException("Unexpected format in /proc/" + pid + "/statm");
		}

		long rssPages = Long.parseLong(statm.substring(space + 1).trim().split(" ")[0]);
		long processKb = (rssPages * PAGE_SIZE) / 1024;

		double percent = (processKb / (double) memTotalKb) * 100.0;
		return round(percent, 3);
	}

	/**
	 * Returns the overall memory usage percentage of the system.
	 *
	 * @return memory usage percentage of the system
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	@Override
	public double getMemoryUsage() throws IOException {
		readMemInfoCached();

		long free = memFreeKb
				+ buffersKb
				+ cachedKb
				+ sReclaimableKb
				- shmemKb;

		double usedPercent = 100.0 * (memTotalKb - free) / memTotalKb;
		return round(usedPercent, 2);
	}

	/**
	 * Returns total system memory in bytes.
	 *
	 * @return total memory in bytes
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	@Override
	public long getTotalMemoryBytes() throws IOException {
		readMemInfoCached();
		return memTotalKb * 1024;
	}

	/**
	 * Returns used memory in bytes (total minus available memory).
	 *
	 * @return used memory in bytes
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	@Override
	public long getAvailableMemoryBytes() throws IOException {
		readMemInfoCached();
		return (memTotalKb - memAvailableKb) * 1024;
	}

	/**
	 * Reads /proc/meminfo and caches values for a short time window.
	 */
	private static void readMemInfoCached() throws IOException {
		long now = System.currentTimeMillis();
		if (now - lastRead < MEMINFO_CACHE_MS) {
			return;
		}

		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/meminfo"))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("MemTotal:")) {
					memTotalKb = parseKb(line);
				} else if (line.startsWith("MemAvailable:")) {
					memAvailableKb = parseKb(line);
				} else if (line.startsWith("MemFree:")) {
					memFreeKb = parseKb(line);
				} else if (line.startsWith("Buffers:")) {
					buffersKb = parseKb(line);
				} else if (line.startsWith("Cached:")) {
					cachedKb = parseKb(line);
				} else if (line.startsWith("SReclaimable:")) {
					sReclaimableKb = parseKb(line);
				} else if (line.startsWith("Shmem:")) {
					shmemKb = parseKb(line);
				}
			}
		}

		lastRead = now;
	}

	/**
	 * Parses a line of /proc/meminfo and returns the value in kB.
	 */
	private static long parseKb(String line) {
		int i = line.indexOf(':') + 1;
		while (line.charAt(i) == ' ') {
			i++;
		}

		long val = 0;
		while (i < line.length() && Character.isDigit(line.charAt(i))) {
			val = val * 10 + (line.charAt(i++) - '0');
		}
		return val;
	}

	/**
	 * Rounds a double value to the given number of decimal places.
	 *
	 * @return returns the value to the desired length
	 */
	private static double round(double val, int decimals) {
		double factor = Math.pow(10, decimals);
		return Math.round(val * factor) / factor;
	}
}