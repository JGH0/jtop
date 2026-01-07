package jtop.Isystem;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for providing system temperature information.
 */
public interface ITemperatureInfo {

	/**
	 * Retrieves a map of all detected temperatures on the system.
	 *
	 * @return Map where the key is a sensor name
	 *		 and the value is the temperature in °C.
	 * @throws IOException if the sensor directories cannot be read
	 */
	Map<String, Double> getTemperatures() throws IOException;
}