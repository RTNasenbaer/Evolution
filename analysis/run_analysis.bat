@echo off
REM Evolution Simulation Analysis - Quick Start Script for Windows
REM This script sets up the environment and runs the analysis

echo ========================================
echo Evolution Simulation Analysis
echo ========================================
echo.

REM Check if Python is installed
python --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Python is not installed or not in PATH
    echo Please install Python 3.8 or higher from python.org
    pause
    exit /b 1
)

echo [1/4] Checking Python installation...
python --version
echo.

REM Check if requirements are installed
echo [2/4] Checking dependencies...
python -c "import pandas, matplotlib, seaborn" >nul 2>&1
if errorlevel 1 (
    echo Dependencies not found. Installing...
    python -m pip install -r requirements.txt
    if errorlevel 1 (
        echo ERROR: Failed to install dependencies
        pause
        exit /b 1
    )
) else (
    echo Dependencies already installed.
)
echo.

REM Run main analysis
echo [3/4] Running main analysis...
python analyze_evolution.py ..
if errorlevel 1 (
    echo ERROR: Main analysis failed
    pause
    exit /b 1
)
echo.

REM Run batch comparison
echo [4/4] Running batch comparison...
python batch_comparison.py ..
if errorlevel 1 (
    echo ERROR: Batch comparison failed
    pause
    exit /b 1
)
echo.

echo ========================================
echo Analysis complete!
echo ========================================
echo.
echo Results saved to:
echo   - analysis_output\
echo   - batch_analysis_output\
echo.
echo Check the PNG files for visualizations.
echo Check the TXT files for detailed reports.
echo.
pause
