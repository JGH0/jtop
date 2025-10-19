#!/usr/bin/env bash
# --- Build script for jtop ---
# This script compiles Java sources into a JAR.
# If JDK is missing, it prompts the user to install it using the detected package manager.

set -e

JTOP_NAME="jtop"
SRC_DIR="src"
BIN_DIR="bin"
JAR_FILE="${JTOP_NAME}.jar"
MAIN_CLASS="${JTOP_NAME}.Main"

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
	elif command -v apk >/dev/null 2>&1; then
		echo "apk"
	elif command -v emerge >/dev/null 2>&1; then
		echo "emerge"

	else
		echo ""
	fi
}

# --- Generate JDK install command ---
jdk_install_command() {
	local pm="$1"
	case "$pm" in
		pacman) echo "pacman -Sy --noconfirm jdk-openjdk" ;;
		apt) echo "apt update && apt install -y openjdk-21-jdk || apt install -y default-jdk" ;;
		dnf) echo "dnf install -y java-21-openjdk-devel || dnf install -y java-latest-openjdk-devel" ;;
		yum) echo "yum install -y java-21-openjdk-devel || yum install -y java-latest-openjdk-devel" ;;
		zypper) echo "zypper install -y java-21-openjdk-devel || zypper install -y java-latest-openjdk-devel" ;;
		brew) echo "brew install openjdk" ;;
		apk) echo "apk add openjdk21" ;;
		emerge) echo "emerge dev-java/openjdk-bin" ;;
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
echo "Cleaning previous build..."
rm -rf "$BIN_DIR" "$JAR_FILE"
mkdir -p "$BIN_DIR"

echo "Compiling Java sources..."
# The -d flag preserves the package directory structure
find "$SRC_DIR" -name "*.java" > sources.txt
javac -d "$BIN_DIR" @sources.txt
rm sources.txt

echo "Creating JAR file..."
jar cfe "$JAR_FILE" "$MAIN_CLASS" -C "$BIN_DIR" .

echo "Build completed successfully: ${JAR_FILE}"