import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class NetworkInfo{
	public static Map<String, long[]> getNetworkUsage() throws IOException {
		Map<String, long[]> map = new LinkedHashMap<>();
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/net/dev"))) {
			br.lines().skip(2).forEach(line -> {
				String[] parts = line.split(":");
				if (parts.length < 2) return;
				String iface = parts[0].trim();
				String[] nums = parts[1].trim().split("\\s+");
				long rx = Long.parseLong(nums[0]);
				long tx = Long.parseLong(nums[8]);
				map.put(iface, new long[]{rx, tx});
			});
		}
		return map;
	}
}