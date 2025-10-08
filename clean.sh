#!/bin/bash
echo "Cleaning build directory..."
if [ -d "build" ]; then
    rm -rf build
    echo "✓ Clean completed successfully"
else
    echo "✓ Build directory already clean"
fi
