#!/bin/bash
echo "Kompiliere Zellulären Automaten..."

# Create build directory
mkdir -p build

# Compile all Java files
javac -d build -encoding UTF-8 src/*.java

if [ $? -eq 0 ]; then
    echo "✓ Kompilierung erfolgreich"
else
    echo "✗ Kompilierung fehlgeschlagen"
    exit 1
fi
