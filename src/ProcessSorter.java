import java.io.IOException;
import java.util.Comparator;

/**
 * Provides sorting utilities for processes.
 * <p>
 * Generates comparators to sort {@link ProcessHandle} instances based on
 * PID, name, path, user, CPU usage, or memory usage. Supports ascending
 * and descending order.
 * </p>
 */
public class ProcessSorter {

	/**
	 * Returns a comparator for processes based on the specified sort type.
	 *
	 * @param sortBy the {@link InfoType} to sort by (PID, NAME, CPU, MEMORY, etc.)
	 * @param ascending true for ascending order, false for descending
	 * @return a {@link Comparator} for {@link ProcessHandle}
	 */
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

	/**
	 * Compares two strings in a case-insensitive manner, treating null as empty.
	 *
	 * @param a first string
	 * @param b second string
	 * @return comparison result
	 */
	private static int safeCompare(String a, String b) {
		if (a == null) a = "";
		if (b == null) b = "";
		return a.compareToIgnoreCase(b);
	}
}