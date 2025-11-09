#!/bin/bash
echo "Starte Zellulären Automaten..."

# Compile first if needed
if [ ! -f build/Hauptprogramm.class ]; then
    ./compile.sh
    if [ $? -ne 0 ]; then exit 1; fi
fi

# Run the program
java -cp build Hauptprogramm
