package jtop.system.linux;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import jtop.Isystem.IPathInfo;

/**
 * Provides utilities to retrieve process path information.
 * <p>
 * Uses {@link ProcessHandle} to fetch details about a running process,
 * including its command (full path) and executable name.
 * </p>
 *
 * <p>
 * Performance notes:
 * <ul>
 *	 <li>Results are cached per PID</li>
 *	 <li>ProcessHandle is queried only once per PID</li>
 * </ul>
 * </p>
 */
public class PathInfo implements IPathInfo {

	private static final String UNKNOWN = "Unknown";

	/** Cache full command path per PID */
	private final Map<Long, String> pathCache = new HashMap<>();

	/** Cache executable name per PID */
	private final Map<Long, String> nameCache = new HashMap<>();

	/**
	 * Returns the name of the executable for the given process ID.
	 *
	 * @param pid the process ID
	 * @return the executable name, or "Unknown" if the process does not exist
	 */
	@Override
	public String getName(long pid) {
		String cached = nameCache.get(pid);
		if (cached != null) {
			return cached;
		}

		String path = getPath(pid);
		if (UNKNOWN.equals(path)) {
			return UNKNOWN;
		}

		int idx = path.lastIndexOf('/');
		String name = idx >= 0 ? path.substring(idx + 1) : path;

		nameCache.put(pid, name);
		return name;
	}

	/**
	 * Returns the full command path of the executable for the given process ID.
	 *
	 * @param pid the process ID
	 * @return the full command path, or "Unknown" if the process does not exist
	 */
	@Override
	public String getPath(long pid) {
		String cached = pathCache.get(pid);
		if (cached != null) {
			return cached;
		}

		Optional<ProcessHandle> ph = ProcessHandle.of(pid);
		if (ph.isEmpty()) {
			return UNKNOWN;
		}

		String path = ph.get().info().command().orElse(UNKNOWN);
		pathCache.put(pid, path);

		return path;
	}

	/**
	 * Clears cached entries.
	 * Should be called periodically to remove dead PIDs.
	 */
	public void clearCache() {
		pathCache.clear();
		nameCache.clear();
	}
}
