package jtop.system;

import java.util.EnumSet;

import jtop.system.linux.LinuxFeatures;
import jtop.system.freebsd.FreeBsdFeatures;
import jtop.system.mac.MacFeatures;

public final class FeatureResolver {

	private FeatureResolver() {}

	public static EnumSet<Feature> supported(OperatingSystem os) {
		return switch (os) {
			case LINUX -> LinuxFeatures.SUPPORTED;
			case FREEBSD -> FreeBsdFeatures.SUPPORTED;
			case MAC -> MacFeatures.SUPPORTED;
		};
	}
}