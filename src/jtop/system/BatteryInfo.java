package jtop.system;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

/**
 * Utility class for retrieving battery information on Linux systems.
 * <p>
 * Automatically detects the battery directory under
 * <code>/sys/class/power_supply/</code> (e.g. BAT0, BAT1) and reads
 * the current charge percentage and status.
 */
public class BatteryInfo {

	private static final Path POWER_SUPPLY_PATH = Path.of("/sys/class/power_supply");
	private static Path batteryPath = null;

	static {
		try {
			batteryPath = detectBatteryPath();
		} catch (IOException e) {
			// Ignore: handled later when attempting to read values
		}
	}

	/**
	 * Detects the first available battery path under /sys/class/power_supply/.
	 *
	 * @return Path to the detected battery directory
	 * @throws IOException if no battery directory is found
	 */
	private static Path detectBatteryPath() throws IOException {
		if (!Files.isDirectory(POWER_SUPPLY_PATH)) {
			throw new IOException("Power supply path not found: " + POWER_SUPPLY_PATH);
		}

		try (var stream = Files.list(POWER_SUPPLY_PATH)) {
			Optional<Path> battery = stream
				.filter(p -> p.getFileName().toString().startsWith("BAT"))
				.findFirst();

			if (battery.isPresent()) {
				return battery.get();
			} else {
				throw new IOException("No battery directory found in " + POWER_SUPPLY_PATH);
			}
		}
	}

	/**
	 * Gets the current battery charge percentage.
	 *
	 * @return the battery percentage (0–100)
	 * @throws IOException if the battery information cannot be read
	 */
	public static int getBatteryPercentage() throws IOException {
		if (batteryPath == null) batteryPath = detectBatteryPath();

		Path capacityPath = batteryPath.resolve("capacity");
		String content = Files.readString(capacityPath).trim();
		return Integer.parseInt(content);
	}

	/**
	 * Gets the current battery status (e.g. Charging, Discharging, Full).
	 *
	 * @return the battery status string
	 * @throws IOException if the status cannot be read
	 */
	public static String getBatteryStatus() throws IOException {
		if (batteryPath == null) batteryPath = detectBatteryPath();

		Path statusPath = batteryPath.resolve("status");
		return Files.readString(statusPath).trim();
	}

	/**
	 * Checks whether the system has a readable battery directory.
	 *
	 * @return true if a battery was detected and is readable
	 */
	public static boolean hasBattery() {
		try {
			if (batteryPath == null) batteryPath = detectBatteryPath();
			return Files.exists(batteryPath);
		} catch (IOException e) {
			return false;
		}
	}
}