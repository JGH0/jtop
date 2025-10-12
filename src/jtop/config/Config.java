package jtop.config;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Represents configuration options for the application.
 * <p>
 * Handles reading, parsing, and providing configuration values
 * such as refresh intervals, color modes, sorting options, and lists of items.
 * Supports inline comments and ANSI escape sequences in configuration values.
 * </p>
 */
public class Config {

	/** Stores all loaded configuration properties. */
	private final Properties properties = new Properties();

	/**
	 * Constructs a {@code Config} instance using the default configuration file.
	 * The default path is {@code config/default.conf}.
	 * <p>
	 * If the file cannot be loaded, an error message is printed to {@code stderr}.
	 * </p>
	 */
	public Config() {
		String filePath = "config/default.conf";
		try (FileReader reader = new FileReader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			System.err.println("Could not load config file: " + filePath);
			e.printStackTrace();
		}
	}

	/**
	 * Constructs a {@code Config} instance using a custom configuration file path.
	 *
	 * @param filePath the path to the configuration file
	 */
	public Config(String filePath) {
		try (FileReader reader = new FileReader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			System.err.println("Could not load config file: " + filePath);
			e.printStackTrace();
		}
	}

	/**
	 * Cleans and normalizes a raw configuration value.
	 * <p>
	 * Cleaning includes:
	 * </p>
	 * <ul>
	 *	 <li>Removing inline comments starting with '#'</li>
	 *	 <li>Removing quotes and '+' signs</li>
	 *	 <li>Replacing escape sequences (033 or \033) with ANSI escape character</li>
	 *	 <li>Trimming whitespace and spaces immediately after escape codes</li>
	 * </ul>
	 *
	 * @param value the raw configuration string
	 * @return the cleaned value, or {@code null} if the input was {@code null}
	 */
	private String cleanValue(String value) {
		if (value == null) return null;

		int commentIndex = value.indexOf('#');
		if (commentIndex != -1) {
			value = value.substring(0, commentIndex);
		}

		value = value.replace("\"", "").replace("+", "");
		value = value.replaceAll("\\\\?033", "\u001B");
		value = value.replaceAll("(\u001B\\[[0-9;]*m)\\s+", "$1");
		return value.strip();
	}

	/**
	 * Retrieves a configuration value as a string.
	 *
	 * @param key the configuration key
	 * @param defaultValue the value to return if the key is missing or empty
	 * @return the string value associated with the key, or {@code defaultValue} if not found
	 */
	public String getString(String key, String defaultValue) {
		String value = cleanValue(properties.getProperty(key, defaultValue));
		return (value != null) ? value : defaultValue;
	}

	/**
	 * Retrieves a configuration value as an integer.
	 *
	 * @param key the configuration key
	 * @param defaultValue the value to return if the key is missing, invalid, or unparsable
	 * @return the integer value associated with the key, or {@code defaultValue} if not found or invalid
	 */
	public int getInt(String key, int defaultValue) {
		String value = cleanValue(properties.getProperty(key));
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				System.err.println("Invalid int for key " + key + ": " + value);
			}
		}
		return defaultValue;
	}

	/**
	 * Retrieves a configuration value as a boolean.
	 *
	 * @param key the configuration key
	 * @param defaultValue the value to return if the key is missing
	 * @return the boolean value associated with the key, or {@code defaultValue} if not found
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		String value = cleanValue(properties.getProperty(key));
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}

	/**
	 * Retrieves a configuration value as a list of strings.
	 *
	 * @param key the configuration key
	 * @param separator the string used to split the value into multiple elements
	 * @param defaultValue the value to return if the key is missing
	 * @return a list of trimmed string values, or {@code defaultValue} if not found
	 */
	public List<String> getList(String key, String separator, List<String> defaultValue) {
		String value = cleanValue(properties.getProperty(key));
		if (value != null) {
			String[] parts = value.split(separator);
			for (int i = 0; i < parts.length; i++) parts[i] = parts[i].strip();
			return Arrays.asList(parts);
		}
		return defaultValue;
	}
}