#!/bin/bash
echo "Cleaning build directory..."
find . -maxdepth 1 -name "*.class" -type f -delete
find entities -name "*.class" -type f -delete 2>/dev/null
find export -name "*.class" -type f -delete 2>/dev/null
find ui -name "*.class" -type f -delete 2>/dev/null
find world -name "*.class" -type f -delete 2>/dev/null
echo "Clean complete."
