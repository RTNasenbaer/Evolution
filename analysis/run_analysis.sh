#!/bin/bash
# Evolution Simulation Analysis - Quick Start Script for Linux/Mac
# This script sets up the environment and runs the analysis

# Suppress Qt Wayland warnings (use X11 backend)
export QT_QPA_PLATFORM=xcb

echo "========================================"
echo "Evolution Simulation Analysis"
echo "========================================"
echo

# Check if Python is installed
if ! command -v python3 &> /dev/null; then
    echo "ERROR: Python 3 is not installed"
    echo "Please install Python 3.8 or higher"
    exit 1
fi

echo "[1/4] Checking Python installation..."
python3 --version
echo

# Check if requirements are installed
echo "[2/4] Checking dependencies..."
if ! python3 -c "import pandas, matplotlib, seaborn" &> /dev/null; then
    echo "Dependencies not found. Installing..."
    python3 -m pip install -r requirements.txt
    if [ $? -ne 0 ]; then
        echo "ERROR: Failed to install dependencies"
        exit 1
    fi
else
    echo "Dependencies already installed."
fi
echo

# Run main analysis
echo "[3/4] Running main analysis..."
python3 analyze_evolution.py ..
if [ $? -ne 0 ]; then
    echo "ERROR: Main analysis failed"
    exit 1
fi
echo

# Run batch comparison
echo "[4/4] Running batch comparison..."
python3 batch_comparison.py ..
if [ $? -ne 0 ]; then
    echo "ERROR: Batch comparison failed"
    exit 1
fi
echo

echo "========================================"
echo "Analysis complete!"
echo "========================================"
echo
echo "Results saved to:"
echo "  - analysis_output/"
echo "  - batch_analysis_output/"
echo
echo "Check the PNG files for visualizations."
echo "Check the TXT files for detailed reports."
echo
