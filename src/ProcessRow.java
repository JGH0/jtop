/**
 * Represents a single process entry in the system.
 * <p>
 * Holds basic information about a process including its ID, name, executable path,
 * owner, CPU usage, and memory usage.
 * </p>
 */
public class ProcessRow {

	/** Process ID (PID) */
	public long pid;

	/** Name of the executable (e.g., "java") */
	public String name;

	/** Full path of the executable (e.g., "/usr/bin/java") */
	public String path;

	/** User or owner of the process */
	public String user;

	/** CPU usage as a percentage string (e.g., "12.5") */
	public String cpu;

	/** Memory usage as a percentage string (e.g., "8.3") */
	public String memory;

	/**
	 * Constructs a ProcessRow instance.
	 *
	 * @param pid the process ID
	 * @param name the process executable name
	 * @param path the full path to the process executable
	 * @param user the owner of the process
	 * @param cpu the CPU usage as a string
	 * @param memory the memory usage as a string
	 */
	public ProcessRow(long pid, String name, String path, String user, String cpu, String memory) {
		this.pid = pid;
		this.name = name;
		this.path = path;
		this.user = user;
		this.cpu = cpu;
		this.memory = memory;
	}
}