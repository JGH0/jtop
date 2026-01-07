package jtop.system;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import jtop.Isystem.IBatteryInfo;

/**
 * Utility class for retrieving battery information on Linux systems.
 * <p>
 * Automatically detects the battery directory under
 * <code>/sys/class/power_supply/</code> (e.g. BAT0, BAT1)
 * and exposes percentage, status, voltage, energy, and power readings.
 * <p>
 * Implements {@link IBatteryInfo} so it can be used in a cross-platform interface-based design.
 */
public class BatteryInfo implements IBatteryInfo {

    /** Path to the power supply directory on Linux */
    private static final Path POWER_SUPPLY_PATH = Path.of("/sys/class/power_supply");

    /** Path to the detected battery directory (e.g., BAT0) */
    private Path batteryPath;

    /**
     * Constructs a BatteryInfo instance and tries to detect the battery path.
     */
    public BatteryInfo() {
        batteryPath = detectBatteryPath();
    }

    /**
     * Detects the first available battery path under /sys/class/power_supply/.
     *
     * @return Path to the battery directory, or null if not found
     */
    private Path detectBatteryPath() {
        if (!Files.isDirectory(POWER_SUPPLY_PATH)) return null;

        try (var stream = Files.list(POWER_SUPPLY_PATH)) {
            Optional<Path> battery = stream
                    .filter(p -> p.getFileName().toString().startsWith("BAT"))
                    .findFirst();
            return battery.orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads a value from a given file path inside the battery directory.
     *
     * @param filename the name of the file to read
     * @return the file's trimmed string contents, or null if unavailable
     */
    private String readBatteryFile(String filename) {
        if (batteryPath == null) return null;

        Path file = batteryPath.resolve(filename);
        if (!Files.exists(file)) return null;

        try {
            return Files.readString(file).trim();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Gets the current battery charge percentage (0–100).
     *
     * @return battery percentage, or -1 if unavailable
     */
    @Override
    public int getBatteryPercentage() {
        String content = readBatteryFile("capacity");
        if (content == null) return -1;

        try {
            return Integer.parseInt(content);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the current battery status (e.g. Charging, Discharging, Full).
     *
     * @return status string, or "Unknown" if unavailable
     */
    @Override
    public String getBatteryStatus() {
        String status = readBatteryFile("status");
        return status != null ? status : "Unknown";
    }

    /**
     * Gets the current voltage in volts (if available).
     *
     * @return voltage in volts, or -1 if unavailable
     */
    @Override
    public double getVoltage() {
        String content = readBatteryFile("voltage_now");
        if (content == null) return -1;

        try {
            // value is usually in microvolts
            return Double.parseDouble(content) / 1_000_000.0;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the current energy in watt-hours (if available).
     *
     * @return energy in Wh, or -1 if unavailable
     */
    @Override
    public double getEnergy() {
        String content = readBatteryFile("energy_now");
        if (content == null) return -1;

        try {
            // value is usually in microwatt-hours
            return Double.parseDouble(content) / 1_000_000.0;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets the current power draw in watts (if available).
     *
     * @return power in W, or -1 if unavailable
     */
    @Override
    public double getPower() {
        String content = readBatteryFile("power_now");
        if (content == null) return -1;

        try {
            // value is usually in microwatts
            return Double.parseDouble(content) / 1_000_000.0;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Checks whether the system has a readable battery directory.
     *
     * @return true if battery is present and readable, false otherwise
     */
    @Override
    public boolean hasBattery() {
        return batteryPath != null && Files.exists(batteryPath);
    }
}
