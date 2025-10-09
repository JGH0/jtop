import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TemperatureInfo {

	public static Map<String, Double> getTemperatures() throws IOException {
		Map<String, Double> temps = new LinkedHashMap<>();

		// --- Primary source: /sys/class/hwmon ---
		Path hwmonBase = Path.of("/sys/class/hwmon");
		if (Files.isDirectory(hwmonBase)) {
			try (DirectoryStream<Path> hwmons = Files.newDirectoryStream(hwmonBase)) {
				for (Path hwmon : hwmons) {
					String name = readTrimmed(hwmon.resolve("name"), "hwmon");

					try (DirectoryStream<Path> files = Files.newDirectoryStream(hwmon, "temp*_input")) {
						for (Path tempFile : files) {
							String base = tempFile.getFileName().toString().replace("_input", "");
							String label = readTrimmed(hwmon.resolve(base + "_label"), base);
							double value = readTempMilliC(tempFile);
							temps.put(name + ":" + label, value);
						}
					} catch (IOException ignored) {
						// Ignore unreadable hwmon entries
					}
				}
			}
		}

		// --- Fallback: /sys/class/thermal ---
		if (temps.isEmpty()) {
			Path thermalBase = Path.of("/sys/class/thermal");
			if (Files.isDirectory(thermalBase)) {
				try (DirectoryStream<Path> zones = Files.newDirectoryStream(thermalBase, "thermal_zone*")) {
					for (Path zone : zones) {
						Path typeFile = zone.resolve("type");
						Path tempFile = zone.resolve("temp");
						if (Files.exists(typeFile) && Files.exists(tempFile)) {
							String type = readTrimmed(typeFile, "zone");
							double temp = readTempMilliC(tempFile);
							temps.put(type, temp);
						}
					}
				}
			}
		}

		return temps;
	}

	// --- Helper to read and trim text from file, with fallback default ---
	private static String readTrimmed(Path path, String fallback) {
		try {
			return Files.exists(path) ? Files.readString(path).trim() : fallback;
		} catch (IOException e) {
			return fallback;
		}
	}

	// --- Helper to read temperature in °C (converts from millidegree) ---
	private static double readTempMilliC(Path path) {
		try {
			String str = Files.readString(path).trim();
			return Double.parseDouble(str) / 1000.0;
		} catch (IOException | NumberFormatException e) {
			return Double.NaN;
		}
	}
}
