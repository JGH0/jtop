package jtop.core;

import java.util.Comparator;
import java.util.Optional;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.IPathInfo;
import jtop.system.Feature;
import jtop.system.SystemInfoFactory;

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
		// create interface instances from factory
		Optional<IPathInfo> pathOpt = SystemInfoFactory.getFeature(Feature.PROCESS);
		Optional<ICpuInfo> cpuOpt = SystemInfoFactory.getFeature(Feature.CPU);
		Optional<IMemoryInfo> memOpt = SystemInfoFactory.getFeature(Feature.MEMORY);

		return (a, b) -> {
			int cmp = 0;
			try {
				switch (sortBy) {
					case PID -> cmp = Long.compare(a.pid(), b.pid());
					case NAME -> cmp = safeCompare(
							pathOpt.map(p -> p.getName(a.pid())).orElse(""),
							pathOpt.map(p -> p.getName(b.pid())).orElse("")
					);
					case PATH -> cmp = safeCompare(
							pathOpt.map(p -> p.getPath(a.pid())).orElse(""),
							pathOpt.map(p -> p.getPath(b.pid())).orElse("")
					);
					case USER -> cmp = safeCompare(
							a.info().user().orElse(""),
							b.info().user().orElse("")
					);
					case CPU -> cmp = Double.compare(
						cpuOpt.map(c -> safeCpu(c, a.pid())).orElse(0.0),
						cpuOpt.map(c -> safeCpu(c, b.pid())).orElse(0.0)
					);
					case MEMORY -> cmp = Double.compare(
						memOpt.map(m -> safeMemory(m, a.pid())).orElse(0.0),
						memOpt.map(m -> safeMemory(m, b.pid())).orElse(0.0)
					);

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

	private static double safeMemory(IMemoryInfo mem, long pid) {
		try {
			return mem.getMemoryPercent(pid);
		} catch (Exception e) {
			return 0.0;
		}
	}

	private static double safeCpu(ICpuInfo cpu, long pid) {
		try {
			return cpu.getCpuPercent(pid);
		} catch (Exception e) {
			return 0.0;
		}
	}
}