package jtop.system;

import java.util.Optional;
import jtop.Isystem.IPathInfo;

/**
 * Provides utilities to retrieve process path information.
 * <p>
 * Uses {@link ProcessHandle} to fetch details about a running process,
 * including its command (full path) and executable name.
 * </p>
 */
public class PathInfo implements IPathInfo {

	/**
	 * Returns the name of the executable for the given process ID.
	 * <p>
	 * For example, if the command is "/usr/bin/java", this will return "java".
	 * </p>
	 *
	 * @param pid the process ID
	 * @return the executable name, or "Unknown" if the process does not exist
	 */
	@Override
	public String getName(long pid) {
		Optional<ProcessHandle> ph = ProcessHandle.of(pid);
		if (ph.isPresent()) {
			ProcessHandle.Info info = ph.get().info();
			String command = info.command().orElse("Unknown");
			return command.substring(command.lastIndexOf("/") + 1);
		}
		return "Unknown";
	}

	/**
	 * Returns the full command path of the executable for the given process ID.
	 * <p>
	 * For example, "/usr/bin/java".
	 * </p>
	 *
	 * @param pid the process ID
	 * @return the full command path, or "Unknown" if the process does not exist
	 */
	@Override
	public String getPath(long pid) {
		Optional<ProcessHandle> ph = ProcessHandle.of(pid);
		if (ph.isPresent()) {
			ProcessHandle.Info info = ph.get().info();
			return info.command().orElse("Unknown");
		}
		return "Unknown";
	}
}