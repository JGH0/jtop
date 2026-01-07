package jtop.system.mac;

import java.util.EnumSet;
import jtop.system.Feature;

/**
 * Defines the set of system features supported on macOS.
 * <p>
 * Each operating system has its own feature class (e.g., {@link jtop.system.linux.LinuxFeatures})
 * that lists which features are implemented and available.
 * </p>
 */
public final class MacFeatures {

    /**
     * The set of features currently supported on macOS.
     * <p>
     * This is used by {@link jtop.system.FeatureResolver} to determine at runtime
     * which features can be instantiated via {@link jtop.system.SystemInfoFactory}.
     * </p>
     */
    public static final EnumSet<Feature> SUPPORTED = EnumSet.of(
        Feature.PROCESS
    );

    /** Private constructor to prevent instantiation of this utility class. */
    private MacFeatures() {}
}