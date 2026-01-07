package jtop.system.linux;

import java.util.EnumSet;
import jtop.system.Feature;

/**
 * Defines the set of system features supported on Linux.
 * <p>
 * Each operating system has its own feature class (e.g., {@link jtop.system.freebsd.FreeBsdFeatures})
 * that lists which features are implemented and available.
 * </p>
 */
public final class LinuxFeatures {

    /**
     * The set of features currently supported on Linux.
     * <p>
     * This is used by {@link jtop.system.FeatureResolver} to determine at runtime
     * which features can be instantiated via {@link jtop.system.SystemInfoFactory}.
     * </p>
     */
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

    /** Private constructor to prevent instantiation of this utility class. */
    private LinuxFeatures() {}
}