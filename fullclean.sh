#!/bin/bash
echo "Performing full clean..."
find . -name "*.class" -type f -delete
find . -name "*.csv" -type f -delete
find . -name "*.jar" -type f -delete
echo "Full clean complete."
