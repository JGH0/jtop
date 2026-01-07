package jtop.system;

public enum Feature {
    CPU("CpuInfo"),
    MEMORY("MemoryInfo"),
    DISK("DiskInfo"),
    NETWORK("NetworkInfo"),
    TEMPERATURE("TemperatureInfo"),
    BATTERY("BatteryInfo"),
    UPTIME("Uptime"),
    PROCESS("PathInfo"); // or whatever is correct

    private final String implClassName;

    Feature(String implClassName) {
        this.implClassName = implClassName;
    }

    public String getImplementationClassName() {
        return implClassName;
    }
}