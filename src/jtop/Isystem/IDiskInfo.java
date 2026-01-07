package jtop.Isystem;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for providing information about disk usage and I/O statistics for mounted devices.
 * <p>
 * Implementations should retrieve disk statistics and provide a map of device names to their
 * read/write counts.
 * </p>
 */
public interface IDiskInfo {

    /**
     * Retrieves disk I/O statistics for all block devices.
     * <p>
     * Each entry in the returned map contains the device name as the key,
     * and an array of two long values as the value:
     * </p>
     * <ul>
     *     <li>index 0 - number of reads completed</li>
     *     <li>index 1 - number of writes completed</li>
     * </ul>
     *
     * @return a {@link Map} where the key is the device name and the value is a
     *         long array containing [reads, writes]
     * @throws IOException if disk statistics cannot be read
     */
    Map<String, long[]> getDiskStats() throws IOException;
}
