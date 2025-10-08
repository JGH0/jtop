#!/usr/bin/env bash
# Install script for system-wide usage

# Ensure running as root
if [[ $EUID -ne 0 ]]; then
	echo "Please run as root to install globally."
	exit 1
fi

# Create installation directories
mkdir -p /usr/local/lib/jtop
cp jtop.jar /usr/local/lib/jtop/
cp jtop.sh /usr/local/bin/jtop
chmod +x /usr/local/bin/jtop

echo "jtop installed successfully! You can now run 'jtop' from anywhere."
