package jtop.Isystem;

/**
 * Interface for retrieving CPU usage information in a cross-platform way.
 * <p>
 * Implementations can provide system-specific logic for Linux, macOS, Windows, or FreeBSD.
 */
public interface ICpuInfo {

    /**
     * Computes the CPU usage percentage of a specific process.
     *
     * @param pid the process ID
     * @return CPU usage as a percentage, or -1 if unavailable
     */
    double getCpuPercent(long pid);

    /**
     * Retrieves the system load average.
     *
     * @return the load average string, or null if unavailable
     */
    String getLoadAverage();

    /**
     * Computes the overall CPU usage percentage over a sample period.
     *
     * @param sampleMs the sample duration in milliseconds
     * @return the CPU usage as a percentage over the sample period, or -1 if unavailable
     */
    double getCpuUsage(long sampleMs);
}
