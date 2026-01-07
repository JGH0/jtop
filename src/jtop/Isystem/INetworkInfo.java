package jtop.Isystem;

import java.io.IOException;
import java.util.Map;

/**
 * Provides methods to collect network usage statistics.
 * <p>
 * Reads from the Linux file <code>/proc/net/dev</code> to retrieve
 * the number of bytes received (RX) and transmitted (TX) per network interface.
 * </p>
 */
public interface INetworkInfo {

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
	Map<String, long[]> getNetworkUsage() throws IOException;
}