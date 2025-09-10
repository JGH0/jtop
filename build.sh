#!/usr/bin/env bash
# Build script for jtop

mkdir -p bin
javac src/*.java -d bin
jar cfe jtop.jar Main -C bin .
echo "Build completed: jtop.jar"