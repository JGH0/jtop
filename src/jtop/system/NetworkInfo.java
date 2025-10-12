package jtop.system;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides methods to collect network usage statistics.
 * <p>
 * Reads from the Linux file <code>/proc/net/dev</code> to retrieve
 * the number of bytes received (RX) and transmitted (TX) per network interface.
 * </p>
 */
public class NetworkInfo {

	/**
	 * Retrieves the current network usage for all network interfaces.
	 *
	 * @return a map where the key is the interface name (e.g., "eth0") and
	 *		 the value is a long array of size 2:
	 *		 <ul>
	 *			 <li>index 0: bytes received (RX)</li>
	 *			 <li>index 1: bytes transmitted (TX)</li>
	 *		 </ul>
	 * @throws IOException if /proc/net/dev cannot be read
	 */
	public static Map<String, long[]> getNetworkUsage() throws IOException {
		Map<String, long[]> map = new LinkedHashMap<>();
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/net/dev"))) {
			// Skip header lines
			br.lines().skip(2).forEach(line -> {
				String[] parts = line.split(":");
				if (parts.length < 2) return;

				String iface = parts[0].trim();
				String[] nums = parts[1].trim().split("\\s+");
				long rx = Long.parseLong(nums[0]);
				long tx = Long.parseLong(nums[8]);

				map.put(iface, new long[]{rx, tx});
			});
		}
		return map;
	}
}