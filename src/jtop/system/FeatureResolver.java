package jtop.system;

import java.util.EnumSet;

import jtop.system.linux.LinuxFeatures;
import jtop.system.freebsd.FreeBsdFeatures;
import jtop.system.mac.MacFeatures;

/**
 * Resolves which system features are supported on a given operating system.
 * <p>
 * Provides a centralized way to query the supported {@link Feature}s for
 * Linux, FreeBSD, and macOS without hardcoding OS-specific logic elsewhere.
 * </p>
 */
public final class FeatureResolver {

	/**
	 * Private constructor to prevent instantiation.
	 */
	private FeatureResolver() {}

	/**
	 * Returns the set of features supported by the given operating system.
	 *
	 * @param os the {@link OperatingSystem} to query
	 * @return an {@link EnumSet} of {@link Feature} representing supported features
	 */
	public static EnumSet<Feature> supported(OperatingSystem os) {
		return switch (os) {
			case LINUX -> LinuxFeatures.SUPPORTED;
			case FREEBSD -> FreeBsdFeatures.SUPPORTED;
			case MAC -> MacFeatures.SUPPORTED;
		};
	}
}