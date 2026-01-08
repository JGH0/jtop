package jtop.system.linux;

import jtop.Isystem.ICpuInfo;
import jtop.Isystem.IMemoryInfo;
import jtop.Isystem.ITemperatureInfo;

import java.io.IOException;
import java.util.Map;

/**
 * Caches CPU, memory, and temperature readings to avoid repeated blocking IO.
 */
public class SystemSampler {

	private double lastCpuUsage;
	private double lastMemPercent;
	private Map<String, Double> lastTemps;
	private double totalMemoryBytes = 0;

	/**
	 * Initialize total memory once.
	 */
	public void initTotalMemory(IMemoryInfo mem) {
		try {
			totalMemoryBytes = mem.getTotalMemoryBytes();
		} catch (IOException e) {
			totalMemoryBytes = 0;
			System.err.println("Failed to read total memory: " + e.getMessage());
		}
	}

	/**
	 * Refreshes cached CPU, memory, and temperature info.
	 * If total memory was not initialized, it will attempt to cache it now.
	 */
	public void refresh(ICpuInfo cpu, IMemoryInfo mem, ITemperatureInfo temps) {
		try {
			lastCpuUsage = cpu.getCpuUsage(100); // CPU usage snapshot
		} catch (Exception e) {
			lastCpuUsage = 0;
		}

		try {
			lastMemPercent = mem.getMemoryUsage();

			// Ensure total memory is cached
			if (totalMemoryBytes == 0) {
				totalMemoryBytes = mem.getTotalMemoryBytes();
			}
		} catch (Exception e) {
			lastMemPercent = 0;
			if (totalMemoryBytes == 0) {
				totalMemoryBytes = 0;
			}
		}

		try {
			lastTemps = temps != null ? temps.getTemperatures() : Map.of();
		} catch (Exception e) {
			lastTemps = Map.of();
		}
	}

	public double getCpu() { return lastCpuUsage; }
	public double getMem() { return lastMemPercent; }
	public Map<String, Double> getTemps() { return lastTemps; }
	public double getTotalMemoryBytes() { return totalMemoryBytes; }
}