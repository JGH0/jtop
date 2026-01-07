package jtop.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import jtop.Isystem.ICpuInfo;

/**
 * Provides CPU usage information and statistics for the system and individual processes.
 * <p>
 * Reads data from the <code>/proc</code> filesystem on Linux:
 * </p>
 * <ul>
 *     <li><code>/proc/[pid]/stat</code> for per-process CPU usage</li>
 *     <li><code>/proc/stat</code> for overall CPU usage</li>
 *     <li><code>/proc/loadavg</code> for system load average</li>
 * </ul>
 */
public class CpuInfo implements ICpuInfo {

    /** Number of decimal places to round CPU percentage values. */
    private static final int DECIMALS = 3;

    /**
     * Computes the CPU usage percentage of a specific process.
     *
     * @param pid the process ID
     * @return CPU usage as a percentage, or -1 if unavailable
     */
    @Override
    public double getCpuPercent(long pid) {
        try {
            String stat = Files.readString(Paths.get("/proc/" + pid + "/stat"));
            String[] parts = stat.split("\\s+");

            long utime = Long.parseLong(parts[13]);
            long stime = Long.parseLong(parts[14]);
            long totalTime = utime + stime;

            double uptimeSeconds = new Uptime().getSystemUptime('s');

            double percent = (100d * totalTime / uptimeSeconds) / Runtime.getRuntime().availableProcessors();

            double factor = Math.pow(10, DECIMALS);
            return Math.round(percent * factor) / factor;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Retrieves the system load average as reported by <code>/proc/loadavg</code>.
     *
     * @return the load average string, or null if unavailable
     */
    @Override
    public String getLoadAverage() {
        try {
            return Files.readString(Path.of("/proc/loadavg")).trim();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Computes the overall CPU usage percentage over a sample period.
     *
     * @param sampleMs the sample duration in milliseconds
     * @return the CPU usage as a percentage over the sample period, or -1 if unavailable
     */
    @Override
    public double getCpuUsage(long sampleMs) {
        try {
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
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Reads the system-wide CPU statistics from <code>/proc/stat</code>.
     *
     * @return an array of CPU time values (user, nice, system, idle, etc.), or null if unavailable
     */
    private long[] readCpuStat() {
        try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/stat"))) {
            String[] parts = br.readLine().trim().split("\\s+");
            long[] vals = new long[parts.length - 1];
            for (int i = 1; i < parts.length; i++) {
                vals[i - 1] = Long.parseLong(parts[i]);
            }
            return vals;
        } catch (IOException e) {
            return null;
        }
    }
}
