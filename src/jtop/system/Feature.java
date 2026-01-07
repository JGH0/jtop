package jtop.system;

/**
 * Represents a system feature that can be dynamically implemented for different operating systems.
 * <p>
 * Each feature stores the name of its default implementation class, which is used
 * by {@link SystemInfoFactory} to instantiate the appropriate OS-specific implementation.
 * </p>
 */
public enum Feature {
    CPU("CpuInfo"),
    MEMORY("MemoryInfo"),
    DISK("DiskInfo"),
    NETWORK("NetworkInfo"),
    TEMPERATURE("TemperatureInfo"),
    BATTERY("BatteryInfo"),
    UPTIME("Uptime"),
    PROCESS("PathInfo");

    /** Name of the implementation class for this feature. */
    private final String implClassName;

    /**
     * Constructs a feature with its associated implementation class name.
     *
     * @param implClassName the default class name implementing this feature
     */
    Feature(String implClassName) {
        this.implClassName = implClassName;
    }

    /**
     * Returns the implementation class name associated with this feature.
     *
     * @return the class name of the implementation
     */
    public String getImplementationClassName() {
        return implClassName;
    }
}