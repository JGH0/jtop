import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for retrieving and interpreting a process's current state.
 * <p>
 * Reads the process status from <code>/proc/[pid]/stat</code> and maps
 * the one-letter state code to a human-readable description.
 * </p>
 * <p>
 * Typical Linux process states:
 * <ul>
 *     <li><b>R</b> - Running</li>
 *     <li><b>S</b> - Sleeping</li>
 *     <li><b>D</b> - Disk Sleep</li>
 *     <li><b>T</b> - Stopped</li>
 *     <li><b>Z</b> - Zombie</li>
 *     <li><b>X</b> - Dead</li>
 * </ul>
 * </p>
 */
public class ProcessState {

	/**
	 * Retrieves the current state of the specified process.
	 * <p>
	 * Reads <code>/proc/[pid]/stat</code> and extracts the third field,
	 * which represents the process state as a single-character code.
	 * </p>
	 *
	 * @param pid the process ID whose state should be retrieved
	 * @return a human-readable description of the process state, or <code>"?"</code> if unavailable
	 */
	public static String getState(long pid) {
		String path = "/proc/" + pid + "/stat";
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String[] parts = reader.readLine().split("\\s+");
			// Field 3 is the process state (R, S, D, T, Z, etc.)
			if (parts.length > 2) {
				return parseState(parts[2]);
			}
		} catch (IOException e) {
			return "?";
		}
		return "?";
	}

	/**
	 * Converts the short one-letter state code from <code>/proc/[pid]/stat</code>
	 * into a descriptive string.
	 *
	 * @param s the single-character state code (e.g. "R", "S", "Z")
	 * @return the full human-readable process state
	 */
	private static String parseState(String s) {
		switch (s) {
			case "R": return "Running";
			case "S": return "Sleeping";
			case "D": return "Disk Sleep";
			case "T": return "Stopped";
			case "Z": return "Zombie";
			case "X": return "Dead";
			default:  return s;
		}
	}
}