import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Utility class to detect the current terminal window size.
 * <p>
 * Provides methods to retrieve the number of rows and columns,
 * allowing output to dynamically adjust to fit the screen.
 */
public class TerminalSize {

	/**
	 * Retrieves the terminal size by executing the "stty size" command.
	 *
	 * @return an array of two integers: {rows, columns}.
	 *		 Defaults to {24, 80} if the size cannot be determined.
	 */
	public static int[] getTerminalSize() {
		try {
			Process process = new ProcessBuilder("sh", "-c", "stty size < /dev/tty").start();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line = reader.readLine();
				if (line != null) {
					String[] parts = line.trim().split("\\s+");
					if (parts.length == 2) {
						return new int[]{
							Integer.parseInt(parts[0]),
							Integer.parseInt(parts[1])
						};
					}
				}
			}
		} catch (Exception e) {
			// Ignore and fallback
		}
		return new int[]{24, 80}; // default fallback
	}

	/**
	 * Retrieves the number of terminal rows.
	 *
	 * @return the number of rows in the current terminal, or 24 if unknown
	 */
	public static int getRows() {
		return getTerminalSize()[0];
	}

	/**
	 * Retrieves the number of terminal columns.
	 *
	 * @return the number of columns in the current terminal, or 80 if unknown
	 */
	public static int getColumns() {
		return getTerminalSize()[1];
	}
}