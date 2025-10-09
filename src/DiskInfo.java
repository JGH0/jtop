import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DiskInfo{
	static Map<String, long[]> getDiskStats() throws IOException {
		Map<String, long[]> map = new LinkedHashMap<>();
		try (BufferedReader br = Files.newBufferedReader(Path.of("/proc/diskstats"))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] parts = line.trim().split("\\s+");
				if (parts.length < 14) continue;
				String device = parts[2];
				long reads = Long.parseLong(parts[3]);
				long writes = Long.parseLong(parts[7]);
				map.put(device, new long[]{reads, writes});
			}
		}
		return map;
	}
}