import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessState {
	public static String getState(long pid) {
		String path = "/proc/" + pid + "/stat";
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String[] parts = reader.readLine().split("\\s+");
			// field 3 is the process state (R, S, D, T, Z, etc.)
			if (parts.length > 2) {
				return parseState(parts[2]);
			}
		} catch (IOException e) {
			return "?";
		}
		return "?";
	}

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