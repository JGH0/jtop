import java.lang.classfile.instruction.SwitchCase;
import java.nio.file.Files;
import java.nio.file.Path;

public class Uptime {
	public static double getSystemUptime(char timeFormat) throws Exception {
		String content = Files.readString(Path.of("/proc/uptime"));
		double seconds = Double.parseDouble(content.split(" ")[0]);
		switch (timeFormat) {
			case 's':
				return seconds;
			case 'm':
				return seconds / 60;
			case 'h':
				return seconds / 3600;
			case 'd':
				return seconds / 86400;
			default:
				throw new IllegalArgumentException("Invalid time format");
		}
	}
}