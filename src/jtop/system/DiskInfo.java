package jtop.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import jtop.Isystem.IDiskInfo;

/**
 * Provides information about disk usage and I/O statistics for mounted devices.
 * <p>
 * Reads data from <code>/proc/diskstats</code> and parses the number of
 * reads and writes for each block device. The results can be used to
 * monitor disk I/O activity or calculate total/used storage if combined
 * with filesystem information.
 * </p>
 */
public class DiskInfo implements IDiskInfo {

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
     * @throws IOException if reading <code>/proc/diskstats</code> fails
     */
    @Override
    public Map<String, long[]> getDiskStats() throws IOException {
        Map<String, long[]> map = new LinkedHashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/diskstats"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length < 14) continue; // Skip incomplete lines
                String device = parts[2];
                long reads = Long.parseLong(parts[3]);
                long writes = Long.parseLong(parts[7]);
                map.put(device, new long[]{reads, writes});
            }
        }
        return map;
    }
}