package jtop.system.linux;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import jtop.Isystem.ITemperatureInfo;

/**
 * Provides system temperature readings from hardware sensors.
 * <p>
 * Temperature sources:
 * <ul>
 *	 <li>Primary: /sys/class/hwmon</li>
 *	 <li>Fallback: /sys/class/thermal</li>
 * </ul>
 * Each temperature is returned in degrees Celsius.
 */
public class TemperatureInfo implements ITemperatureInfo {

	/**
	 * Retrieves a map of all detected temperatures on the system.
	 *
	 * @return Map where the key is a sensor name (e.g., "coretemp:Core 0")
	 *		 and the value is the temperature in °C.
	 * @throws IOException if the sensor directories cannot be read
	 */
	@Override
	public Map<String, Double> getTemperatures() throws IOException {
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

	private String readTrimmed(Path path, String fallback) {
		try {
			return Files.exists(path) ? Files.readString(path).trim() : fallback;
		} catch (IOException e) {
			return fallback;
		}
	}

	private double readTempMilliC(Path path) {
		try {
			String str = Files.readString(path).trim();
			return Double.parseDouble(str) / 1000.0;
		} catch (IOException | NumberFormatException e) {
			return Double.NaN;
		}
	}
}