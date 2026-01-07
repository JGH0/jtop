package jtop.system;

public enum OperatingSystem {
	LINUX,
	FREEBSD,
	MAC;

	public static OperatingSystem detect() {
		String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("linux")) return LINUX;
		if (os.contains("freebsd")) return FREEBSD;
		if (os.contains("mac")) return MAC;

		throw new UnsupportedOperationException("Unsupported OS: " + os);
	}
}
