package jtop.Isystem;

/**
 * Interface for retrieving battery information.
 * <p>
 * Allows platform-specific implementations to provide battery status, charge,
 * voltage, energy, and power readings.
 */
public interface IBatteryInfo {

    /**
     * Gets the current battery charge percentage (0–100).
     *
     * @return battery percentage, or -1 if unavailable
     */
    int getBatteryPercentage();

    /**
     * Gets the current battery status (e.g., Charging, Discharging, Full).
     *
     * @return battery status string, or null if unavailable
     */
    String getBatteryStatus();

    /**
     * Gets the current battery voltage in volts.
     *
     * @return voltage in volts, or -1 if unavailable
     */
    double getVoltage();

    /**
     * Gets the current battery energy in watt-hours.
     *
     * @return energy in Wh, or -1 if unavailable
     */
    double getEnergy();

    /**
     * Gets the current power draw in watts.
     *
     * @return power in W, or -1 if unavailable
     */
    double getPower();

    /**
     * Returns whether the system has a battery.
     *
     * @return true if battery is present and readable, false otherwise
     */
    boolean hasBattery();
}