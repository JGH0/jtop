# jtop Project Report

## Overview

**jtop** is a terminal-based system monitoring tool written in Java. It provides a lightweight alternative to the `top` command, featuring a modular and extensible interface.

## Features Implemented

* CPU, memory, disk, and network usage monitoring
* Process list with scrollable interface
* Sorting by CPU, memory, PID, name, path, and other headers
* Temperature monitoring (via `/sys/class/hwmon` or `/sys/class/thermal`)
* Terminal-size adaptive display
* Keyboard navigation: `j/k` to scroll, `Enter` for page scroll, `q` or `Ctrl+C` to quit

## Class Structure

* `Main` — Entry point; sets up the terminal and refresh loop
* `ShowProcesses` — Collects and manages running process data
* `ProcessRow` — Represents a single process entry
* `ProcessTableRenderer` — Draws the process table in the terminal
* `ProcessSorter` — Provides comparators for sorting
* `MemoryInfo`, `CpuInfo`, `DiskInfo`, `NetworkInfo`, `TemperatureInfo`, `Uptime`, etc. — System metrics modules
* `Header` — Displays header information
* `InputHandler` — Reads and handles keyboard and mouse input
* `TerminalSize` — Detects terminal dimensions
* `RefreshThread` — Handles background refresh of process data
* `PathInfo` — Retrieves process path and name

## Design Considerations

* Modular architecture with clear separation of data collection, rendering, and input handling
* Uses Java 21+ features and the ProcessHandle API
* Layout adapts dynamically to terminal size to prevent overflow
* Graceful handling of missing or inaccessible process information
* Keyboard navigation inspired by tools like `less` and `top`

## Usage

Compile and run:

```bash
javac src/*.java
java src/Main.java
```

For system-wide installation, use the included `build.sh` and `install.sh` scripts.

**Keyboard shortcuts:**

* `j/k`: scroll up/down
* `Enter`: scroll one page
* `q` or `Ctrl+C`: quit

## Code Quality

* Clear separation of concerns
* Proper encapsulation of fields
* Use of constructors and method overloading where appropriate
* Aggregation/composition used in `ShowProcesses` and rendering classes
* Interfaces used via `Comparator` for sorting
* Inheritance applied through `Thread` extension in `RefreshThread`

## Notes

* JavaDoc documentation can be generated using `./generate_javadoc.sh`
* The project is in an **alpha stage** — some metrics may not work on all hardware
* Tested primarily on Linux systems with Intel and AMD CPUs

## TODOs / Known Issues

### Tab View
* Planned implementation of a tab system to allow grouping of process information

### Config
* Config file support is partially implemented — some fields (e.g., `table.header.content`) are not yet parsed or applied
* Configs are not yet accessible in system-wide installations

### Performance / Design
* On some terminals with custom themes, colors may display incorrectly
* Cursor animations can cause slight lag during refresh
* Table caching occasionally fails, causing unnecessary redraws and performance drops
* On low-performance machines, keyboard input may experience minor delay

## Author

* **Jürg Georg Hallenbarter**
* Version 1.0 — Date: 2025-10-24