import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TerminalSize {
    public static int[] getTerminalSize() {//get terminal size in rows and columns via "stty size" command
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
        } catch (Exception e) {}
        return new int[]{24, 80}; // default fallback
	}

	public static int getRows() {
        return getTerminalSize()[0];
    }

    public static int getColumns() {
        return getTerminalSize()[1];
    }
}
