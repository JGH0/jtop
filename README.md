# jtop - A Java-based System Monitoring Tool

\=============================================

Note: This project is not related to the Python-based jtop for NVIDIA Jetson devices ([https://rnext.it/jetson\_stats/](https://rnext.it/jetson_stats/))

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java Version](https://img.shields.io/badge/Java-21%2B-orange.svg)](https://www.java.com/en/)
[![Platform](https://img.shields.io/badge/Platform-Linux-brightgreen.svg)](https://www.linux.org/)
[![Status](https://img.shields.io/badge/Status-Alpha-red.svg)](https://en.wikipedia.org/wiki/Software_release_life_cycle#Alpha)

## Overview

jtop is a lightweight, terminal-based system monitoring tool written in Java. It aims to replicate the basic functionality of the `top` command, providing a modular interface for easy system monitoring.

## Features

* Resource-efficient design
* No external build tools required
* Modular and tab-based interface
* Supports Java 21+

## Target Platform

* Linux (primary target)
* Other platforms (e.g. macOS, freeBSD) may be supported in the future, pending compatibility testing and development.

## Getting Started

### Prerequisites

* Java 21+ installed on your system
* Linux platform

### Building and Running Locally

1. Clone the repository:

   ```bash
   git clone https://github.com/JGH0/jtop.git
   cd jtop
   ```

2. Compile the code:

   ```bash
   javac src/*.java
   ```

3. Run the application:

   ```bash
   java src/Main.java
   ```

### System-wide Installation

To install jtop globally so that it can be run from any terminal:

1. Clone the repository (if not done already):

   ```bash
   git clone https://github.com/JGH0/jtop.git
   cd jtop
   ```

2. Make the build and install scripts executable:

   ```bash
   chmod +x build.sh install.sh
   ```

3. Build the project using the provided build script:

   ```bash
   ./build.sh
   ```

4. Install globally (requires root privileges):

   ```bash
   sudo ./install.sh
   ```

5. Run jtop from anywhere:

   ```bash
   jtop
   ```

**Notes:**

* The `build.sh` script compiles all Java source files and creates an executable `jtop.jar`.
* The `install.sh` script copies `jtop.jar` to `/usr/local/lib/jtop` and installs a wrapper script in `/usr/local/bin` for easy execution.

### Usage

jtop provides a simple and intuitive interface for system monitoring. Use the following keys to navigate:

* `j`/`k`: Scroll up/down
* `Enter`: Scroll entire row
* `q` or `Ctrl+C`: Quit

## Contributing (starting 24.10 since this is a graded school project)

We welcome contributions from the community! To contribute:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes with proper testing.
4. Submit a pull request detailing your modifications.

### Developer Documentation

Developer documentation is generated using JavaDoc. To generate and view the documentation:

```bash
chmod +x generate_javadoc.sh
./generate_javadoc.sh
```

This will create a `docs/` folder with HTML documentation you can view in your browser.

## License

jtop is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Author

* Jürg Georg Hallenbarter

## Note

jtop is currently in **Alpha** stage, which means it is still a work-in-progress and may contain bugs or incomplete features. Use at your own risk!
