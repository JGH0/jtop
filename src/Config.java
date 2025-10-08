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

	public String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public int getInt(String key, int defaultValue) {
		String value = properties.getProperty(key);
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
		String value = properties.getProperty(key);
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}

	public List<String> getList(String key, String separator, List<String> defaultValue) {
		String value = properties.getProperty(key);
		if (value != null) {
			return Arrays.asList(value.split(separator));
		}
		return defaultValue;
	}
}
