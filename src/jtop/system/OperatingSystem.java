package jtop.system;

/**
 * Enum representing supported operating systems.
 * <p>
 * Provides a method to detect the current operating system at runtime.
 * </p>
 */
public enum OperatingSystem {
	/** Linux operating system. */
	LINUX,
	/** FreeBSD operating system. */
	FREEBSD,
	/** macOS operating system. */
	MAC;

	/**
	 * Detects the current operating system.
	 * <p>
	 * Uses the system property {@code os.name} to determine the OS.
	 * Returns the corresponding {@link OperatingSystem} enum value.
	 * </p>
	 *
	 * @return the detected {@link OperatingSystem}
	 * @throws UnsupportedOperationException if the OS is not supported
	 */
	public static OperatingSystem detect() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("linux")) return LINUX;
		if (os.contains("freebsd")) return FREEBSD;
		if (os.contains("mac")) return MAC;

		throw new UnsupportedOperationException("Unsupported OS: " + os);
	}
}