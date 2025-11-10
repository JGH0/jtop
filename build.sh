#!/usr/bin/env bash
# --- Build script for jtop ---
# This script compiles Java sources into a JAR.
# If JDK is missing, it prompts the user to install it using the detected package manager.

set -e

JTOP_NAME="jtop"
SRC_DIR="src"
BIN_DIR="bin"
JAR_FILE="${JTOP_NAME}.jar"
MAIN_CLASS="Main"

# --- Detect package manager ---
detect_package_manager() {
    if command -v pacman >/dev/null 2>&1; then
        echo "pacman"
    elif command -v apt >/dev/null 2>&1; then
        echo "apt"
    elif command -v dnf >/dev/null 2>&1; then
        echo "dnf"
    elif command -v yum >/dev/null 2>&1; then
        echo "yum"
    elif command -v zypper >/dev/null 2>&1; then
        echo "zypper"
    elif command -v brew >/dev/null 2>&1; then
        echo "brew"
    else
        echo ""
    fi
}

# --- Generate JDK install command ---
jdk_install_command() {
    local pm="$1"
    case "$pm" in
        pacman) echo "pacman -Sy --noconfirm jdk-openjdk" ;;
        apt) echo "apt update && apt install -y openjdk-20-jdk || apt install -y default-jdk" ;;
        dnf) echo "dnf install -y java-20-openjdk-devel || dnf install -y java-latest-openjdk-devel" ;;
        yum) echo "yum install -y java-20-openjdk-devel || yum install -y java-latest-openjdk-devel" ;;
        zypper) echo "zypper install -y java-20-openjdk-devel || zypper install -y java-latest-openjdk-devel" ;;
        brew) echo "brew install openjdk" ;;
        *) echo "" ;;
    esac
}

# --- Check for javac ---
if ! command -v javac >/dev/null 2>&1; then
    echo "Java Development Kit (JDK) not found."
    PM=$(detect_package_manager)

    if [[ -z "$PM" ]]; then
        echo "Please install the latest JDK manually and rerun this script."
        exit 1
    fi

    CMD=$(jdk_install_command "$PM")
    if [[ $EUID -ne 0 && "$PM" != "brew" ]]; then
        echo "Please run this script as root to install the JDK, or install it manually."
        exit 1
    fi

    # Prompt the user
    read -rp "Do you want to run the following command to install the JDK? [$CMD] (y/n): " ANSWER
    case "$ANSWER" in
        y|Y)
            echo "Installing JDK..."
            eval "$CMD"
            echo "JDK installed successfully."
            ;;
        *)
            echo "JDK installation cancelled. Please install manually and rerun."
            exit 1
            ;;
    esac
fi

# --- Build process ---
echo "Compiling Java sources..."
mkdir -p "$BIN_DIR"
javac "$SRC_DIR"/*.java -d "$BIN_DIR"

echo "Creating JAR file..."
jar cfe "$JAR_FILE" "$MAIN_CLASS" -C "$BIN_DIR" .

echo "Build completed successfully: ${JAR_FILE}"
