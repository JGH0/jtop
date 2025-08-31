import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DiskInfo{
	public static long getReadBytes(long pid) throws IOException{
		return parseIo(pid, "read_bytes:");
	}

	public static long getWriteBytes(long pid) throws IOException{
		return parseIo(pid, "write_bytes:");
	}

	private static long parseIo(long pid, String key) throws IOException{
		List<String> lines = Files.readAllLines(Paths.get("/proc/" + pid + "/io"));
		for (String line : lines){
			if (line.startsWith(key)){
				return Long.parseLong(line.split("\\s+")[1]);
			}
		}
		return 0;
	}
}
