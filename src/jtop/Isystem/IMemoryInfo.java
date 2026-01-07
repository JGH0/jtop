package jtop.Isystem;

import java.io.IOException;

/**
 * Provides methods to gather memory usage statistics.
 * <p>
 * Implementations typically read Linux <code>/proc</code> files to determine:
 * </p>
 * <ul>
 *	 <li>Total and available system memory</li>
 *	 <li>Memory usage percentage by the system</li>
 *	 <li>Memory usage percentage of a specific process (by PID)</li>
 * </ul>
 */
public interface IMemoryInfo {

	/**
	 * Returns the memory usage percentage of a process.
	 *
	 * @param pid the process ID
	 * @return memory usage percentage of the process
	 * @throws IOException if the /proc file cannot be read or is malformed
	 */
	double getMemoryPercent(long pid) throws IOException;

	/**
	 * Returns the overall memory usage percentage of the system.
	 *
	 * @return memory usage percentage of the system
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	double getMemoryUsage() throws IOException;

	/**
	 * Returns total system memory in bytes.
	 *
	 * @return total memory in bytes
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	long getTotalMemoryBytes() throws IOException;

	/**
	 * Returns used memory in bytes (total minus available memory).
	 *
	 * @return used memory in bytes
	 * @throws IOException if /proc/meminfo cannot be read
	 */
	long getAvailableMemoryBytes() throws IOException;
}