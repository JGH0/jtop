import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
	private final Properties properties = new Properties();

	public Config() {
		String filePath = "config/default.conf";
		try (FileReader reader = new FileReader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			System.err.println("Could not load config file: " + filePath);
			e.printStackTrace();
		}
	}

	public Config(String filePath) {
		try (FileReader reader = new FileReader(filePath)) {
			properties.load(reader);
		} catch (IOException e) {
			System.err.println("Could not load config file: " + filePath);
			e.printStackTrace();
		}
	}

	private String cleanValue(String value) {
		if (value == null) return null;

		// Remove everything after '#' (inline comment)
		int commentIndex = value.indexOf('#');
		if (commentIndex != -1) {
			value = value.substring(0, commentIndex);
		}

		// Remove quotes and '+' signs
		value = value.replace("\"", "").replace("+", "");

		// Replace all 033 or \033 with actual escape character
		value = value.replaceAll("\\\\?033", "\u001B");

		// Remove spaces **immediately after escape codes** only
		value = value.replaceAll("(\u001B\\[[0-9;]*m)\\s+", "$1");

		// Trim leading/trailing whitespace from the entire value
		value = value.strip();

		return value;
	}

	public String getString(String key, String defaultValue) {
		String value = cleanValue(properties.getProperty(key, defaultValue));
		return (value != null) ? value : defaultValue;
	}

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

	public boolean getBoolean(String key, boolean defaultValue) {
		String value = cleanValue(properties.getProperty(key));
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}

	public List<String> getList(String key, String separator, List<String> defaultValue) {
		String value = cleanValue(properties.getProperty(key));
		if (value != null) {
			String[] parts = value.split(separator);
			for (int i = 0; i < parts.length; i++) parts[i] = parts[i].strip(); // trim each element
			return Arrays.asList(parts);
		}
		return defaultValue;
	}
}
