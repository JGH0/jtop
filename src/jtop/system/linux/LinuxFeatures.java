package jtop.system.linux;

import java.util.EnumSet;
import jtop.system.Feature;

public final class LinuxFeatures {

	public static final EnumSet<Feature> SUPPORTED = EnumSet.of(
		Feature.CPU,
		Feature.MEMORY,
		Feature.DISK,
		Feature.NETWORK,
		Feature.TEMPERATURE,
		Feature.BATTERY,
		Feature.UPTIME,
		Feature.PROCESS
	);

	private LinuxFeatures() {}
}