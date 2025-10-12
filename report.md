# jtop Project Report

## Overview

jtop is a terminal-based system monitoring tool written in Java. It provides a lightweight alternative to the `top` command with modular and tab-based interface.

## Features Implemented

* CPU, memory, disk, and network usage monitoring
* Process list with scrollable interface
* Sorting by CPU, memory, PID, name, path, and user
* Temperature monitoring (via `/sys/class/hwmon` or `/sys/class/thermal`)
* Terminal-size adaptive display
* Keyboard navigation: `j/k` to scroll, `Enter` for page scroll, `q/Ctrl+C` to quit

## Class Structure

* `Main` - Entry point; sets up terminal and refresh loop
* `ShowProcesses` - Collects and manages running process data
* `ProcessRow` - Represents a single process entry
* `ProcessTableRenderer` - Draws the process table in terminal
* `ProcessSorter` - Provides comparators for sorting
* `MemoryInfo`, `CpuInfo`, `DiskInfo`, `NetworkInfo`, `TemperatureInfo`, `Uptime` - System metrics
* `Header` - Displays header information
* `InputHandler` - Reads and handles keyboard/mouse input
* `TerminalSize` - Detects terminal dimensions
* `RefreshThread` - Background refresh of process data
* `PathInfo` - Retrieves process path and name

## Design Considerations

* Modular design with clear separation of data collection, rendering, and input handling.
* Uses Java 21+ features and ProcessHandle API.
* Terminal size adaptive layout to prevent overflow.
* Safe handling of missing or inaccessible process info.
* Keyboard navigation inspired by `less` and `top`.

## Usage

Compile and run:

```bash
javac src/*.java
java src/Main.java
```

For system-wide installation, use the included `build.sh` and `install.sh` scripts.

Keyboard shortcuts:

* `j/k`: scroll up/down
* `Enter`: scroll entire page
* `q` or `Ctrl+C`: quit

## Code Quality

* Clear separation of concerns
* Proper encapsulation with private/public fields
* Use of constructors and method overloading where appropriate
* Aggregation/composition used in `ShowProcesses` and renderer
* Interface usage: `Comparator` for sorting
* Inheritance via extending `Thread` for `RefreshThread`

## Notes

* JavaDoc documentation is available. Can be generated using `./generate_javadoc.sh` or precompiled in `/doc/index.html`.
* The project is in Alpha stage; some metrics may not work on all hardware.
* Tested primarily on Linux with Intel and AMD CPUs.

## Author

* Jürg Georg Hallenbarter
* Version 1.0, Date: 2025-10-12

