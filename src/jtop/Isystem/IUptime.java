package jtop.Isystem;

/**
 * Interface for retrieving system uptime information.
 * <p>
 * Provides the uptime in various units such as seconds, minutes, hours, or days.
 */
public interface IUptime {

    /**
     * Gets the system uptime in the specified format.
     *
     * @param timeFormat a character indicating the desired unit:
     *                   's' for seconds,
     *                   'm' for minutes,
     *                   'h' for hours,
     *                   'd' for days.
     * @return the system uptime in the requested unit.
     * @throws Exception if reading /proc/uptime fails or if the timeFormat is invalid.
     */
    double getSystemUptime(char timeFormat) throws Exception;
}
