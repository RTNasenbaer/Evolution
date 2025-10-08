#!/bin/bash
# Evolution Simulation Analysis - Conda Environment Setup (Linux/Mac)
# Creates a local conda environment with all required dependencies

echo "========================================"
echo "Evolution Analysis - Conda Setup"
echo "========================================"
echo

# Check if conda is installed
if ! command -v conda &> /dev/null; then
    echo "ERROR: Conda is not installed or not in PATH"
    echo
    echo "Please install Miniconda or Anaconda from:"
    echo "  https://docs.conda.io/en/latest/miniconda.html"
    echo
    exit 1
fi

echo "[1/5] Checking conda installation..."
conda --version
echo

# Set environment name
ENV_NAME="evolution-analysis"

echo "[2/5] Creating conda environment '$ENV_NAME'..."
conda create -n $ENV_NAME python=3.11 -y
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to create conda environment"
    exit 1
fi
echo

echo "[3/5] Activating environment..."
# Initialize conda for bash if not already done
eval "$(conda shell.bash hook)"
conda activate $ENV_NAME
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to activate environment"
    exit 1
fi
echo

echo "[4/5] Installing required packages..."
echo
echo "Installing core packages..."
conda install -c conda-forge pandas numpy matplotlib seaborn -y
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to install core packages"
    exit 1
fi

echo
echo "Installing scientific packages..."
conda install -c conda-forge scipy scikit-learn -y
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to install scientific packages"
    exit 1
fi

echo
echo "Installing optional packages..."
conda install -c conda-forge jupyter notebook -y
if [ $? -ne 0 ]; then
    echo "WARNING: Failed to install Jupyter (optional)"
fi

echo
echo "[5/5] Verifying installation..."
python -c "import pandas, numpy, matplotlib, seaborn, scipy, sklearn; print('✓ All required packages installed successfully!')"
if [ $? -ne 0 ]; then
    echo "ERROR: Package verification failed"
    exit 1
fi
echo

echo "========================================"
echo "Setup Complete!"
echo "========================================"
echo
echo "Environment name: $ENV_NAME"
echo "Python version:"
python --version
echo
echo "To use this environment:"
echo "  1. Activate: conda activate $ENV_NAME"
echo "  2. Run scripts: python analyze_evolution.py"
echo "  3. Deactivate: conda deactivate"
echo
echo "Optional: Launch Jupyter Notebook"
echo "  jupyter notebook interactive_analysis.ipynb"
echo
echo "To remove this environment later:"
echo "  conda env remove -n $ENV_NAME"
echo
