import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MemoryInfo{
	public static long getMemoryKb(long pid) throws IOException{
		// /proc/[pid]/statm → RSS pages (field 2)
		String statm = Files.readString(Paths.get("/proc/" + pid + "/statm"));
		String[] parts = statm.split("\\s+");
		long rssPages = Long.parseLong(parts[1]);
		long pageSize = 4096; // usually 4KB
		return (rssPages * pageSize) / 1024;
	}
}
