import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for retrieving system uptime information.
 * <p>
 * Reads the uptime from <code>/proc/uptime</code> and returns it
 * in various units such as seconds, minutes, hours, or days.
 */
public class Uptime {

	/**
	 * Gets the system uptime in the specified format.
	 *
	 * @param timeFormat a character indicating the desired unit:
	 *				   's' for seconds,
	 *				   'm' for minutes,
	 *				   'h' for hours,
	 *				   'd' for days.
	 * @return the system uptime in the requested unit.
	 * @throws Exception if reading <code>/proc/uptime</code> fails
	 *				   or if the timeFormat is invalid.
	 */
	public static double getSystemUptime(char timeFormat) throws Exception {
		String content = Files.readString(Path.of("/proc/uptime"));
		double seconds = Double.parseDouble(content.split(" ")[0]);

		return switch (timeFormat) {
			case 's' -> seconds;
			case 'm' -> seconds / 60;
			case 'h' -> seconds / 3600;
			case 'd' -> seconds / 86400;
			default -> throw new IllegalArgumentException("Invalid time format: " + timeFormat);
		};
	}
}