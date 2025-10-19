package jtop.system;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

/**
 * Utility class for retrieving battery information on Linux systems.
 * <p>
 * Automatically detects the battery directory under
 * <code>/sys/class/power_supply/</code> (e.g. BAT0, BAT1)
 * and exposes percentage, status, voltage, and energy readings.
 */
public class BatteryInfo {

	private static final Path POWER_SUPPLY_PATH = Path.of("/sys/class/power_supply");
	private static Path batteryPath = null;

	static {
		try {
			batteryPath = detectBatteryPath();
		} catch (IOException e) {
			// Will be handled gracefully later
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
	 * Reads a value from a given file path inside the battery directory.
	 *
	 * @param filename the name of the file to read
	 * @return the file's trimmed string contents
	 * @throws IOException if the file cannot be read
	 */
	private static String readBatteryFile(String filename) throws IOException {
		if (batteryPath == null) batteryPath = detectBatteryPath();
		Path file = batteryPath.resolve(filename);
		if (!Files.exists(file)) {
			throw new IOException("Battery attribute not found: " + file);
		}
		return Files.readString(file).trim();
	}

	/**
	 * Gets the current battery charge percentage (0–100).
	 */
	public static int getBatteryPercentage() throws IOException {
		return Integer.parseInt(readBatteryFile("capacity"));
	}

	/**
	 * Gets the current battery status (e.g. Charging, Discharging, Full).
	 */
	public static String getBatteryStatus() throws IOException {
		return readBatteryFile("status");
	}

	/**
	 * Gets the current voltage in volts (if available).
	 *
	 * @return voltage in volts, or -1 if unavailable
	 */
	public static double getVoltage() {
		try {
			String content = readBatteryFile("voltage_now");
			// value is usually in microvolts
			return Double.parseDouble(content) / 1_000_000.0;
		} catch (IOException | NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Gets the current energy in watt-hours (if available).
	 *
	 * @return energy in Wh, or -1 if unavailable
	 */
	public static double getEnergy() {
		try {
			String content = readBatteryFile("energy_now");
			// value is usually in microwatt-hours
			return Double.parseDouble(content) / 1_000_000.0;
		} catch (IOException | NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Gets the current power draw in watts (if available).
	 *
	 * @return power in W, or -1 if unavailable
	 */
	public static double getPower() {
		try {
			String content = readBatteryFile("power_now");
			// value is usually in microwatts
			return Double.parseDouble(content) / 1_000_000.0;
		} catch (IOException | NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Checks whether the system has a readable battery directory.
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