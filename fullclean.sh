#!/bin/bash
echo "Performing full clean..."
rm -rf build 2>/dev/null
rm -f *.jar 2>/dev/null
echo "Moving stray CSV files to data folder..."
mkdir -p data
find . -maxdepth 1 -name "*.csv" -type f -exec mv {} data/ \; 2>/dev/null
echo "✓ Full clean completed successfully"
