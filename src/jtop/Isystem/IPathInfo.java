package jtop.Isystem;

/**
 * Provides utilities to retrieve process path information.
 * <p>
 * Implementations may use OS-specific mechanisms to fetch details about
 * a running process, including its command (full path) and executable name.
 * </p>
 */
public interface IPathInfo {

	/**
	 * Returns the name of the executable for the given process ID.
	 * <p>
	 * For example, if the command is "/usr/bin/java", this should return "java".
	 * </p>
	 *
	 * @param pid the process ID
	 * @return the executable name, or "Unknown" if the process does not exist
	 */
	String getName(long pid);

	/**
	 * Returns the full command path of the executable for the given process ID.
	 * <p>
	 * For example, "/usr/bin/java".
	 * </p>
	 *
	 * @param pid the process ID
	 * @return the full command path, or "Unknown" if the process does not exist
	 */
	String getPath(long pid);
}
