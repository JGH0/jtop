import java.io.IOException;
import java.util.Comparator;

public class ProcessSorter {
	public static Comparator<ProcessHandle> getComparator(InfoType sortBy, boolean ascending) {
		return (a, b) -> {
			int cmp = 0;
			try {
				switch (sortBy) {
					case PID -> cmp = Long.compare(a.pid(), b.pid());
					case NAME -> cmp = safeCompare(PathInfo.getName(a.pid()), PathInfo.getName(b.pid()));
					case PATH -> cmp = safeCompare(PathInfo.getPath(a.pid()), PathInfo.getPath(b.pid()));
					case USER -> cmp = safeCompare(a.info().user().orElse(""), b.info().user().orElse(""));
					case CPU -> cmp = Double.compare(CpuInfo.getCpuPercent(a.pid()), CpuInfo.getCpuPercent(b.pid()));
					case MEMORY -> cmp = Double.compare(MemoryInfo.getMemoryPercent(a.pid()), MemoryInfo.getMemoryPercent(b.pid()));
					default -> cmp = 0;
				}
			} catch (Exception ignored) { }
			return ascending ? cmp : -cmp;
		};
	}

	private static int safeCompare(String a, String b) {
		if (a == null) a = "";
		if (b == null) b = "";
		return a.compareToIgnoreCase(b);
	}
}