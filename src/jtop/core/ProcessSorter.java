package jtop.core;
import java.io.IOException;
import java.util.Comparator;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.IPathInfo;
import jtop.system.CpuInfo;
import jtop.system.MemoryInfo;
import jtop.system.PathInfo;

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
		// create instances of your interface implementations
		IPathInfo pathInfo = new PathInfo();
		ICpuInfo cpuInfo = new CpuInfo();
		IMemoryInfo memoryInfo = new MemoryInfo();

		return (a, b) -> {
			int cmp = 0;
			try {
				switch (sortBy) {
					case PID -> cmp = Long.compare(a.pid(), b.pid());
					case NAME -> cmp = safeCompare(pathInfo.getName(a.pid()), pathInfo.getName(b.pid()));
					case PATH -> cmp = safeCompare(pathInfo.getPath(a.pid()), pathInfo.getPath(b.pid()));
					case USER -> cmp = safeCompare(a.info().user().orElse(""), b.info().user().orElse(""));
					case CPU -> cmp = Double.compare(cpuInfo.getCpuPercent(a.pid()), cpuInfo.getCpuPercent(b.pid()));
					case MEMORY -> cmp = Double.compare(memoryInfo.getMemoryPercent(a.pid()), memoryInfo.getMemoryPercent(b.pid()));
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