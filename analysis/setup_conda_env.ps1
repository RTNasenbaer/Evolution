# Evolution Simulation Analysis - Conda Environment Setup (PowerShell)
# Creates a local conda environment with all required dependencies

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Evolution Analysis - Conda Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if conda is installed
$condaExists = Get-Command conda -ErrorAction SilentlyContinue
if (-not $condaExists) {
    Write-Host "ERROR: Conda is not installed or not in PATH" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Miniconda or Anaconda from:"
    Write-Host "  https://docs.conda.io/en/latest/miniconda.html" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "After installation, restart PowerShell and run this script again."
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[1/5] Checking conda installation..." -ForegroundColor Green
conda --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Conda command failed" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

# Set environment name
$envName = "evolution-analysis"

Write-Host "[2/5] Creating conda environment '$envName'..." -ForegroundColor Green
conda create -n $envName python=3.11 -y
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to create conda environment" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

Write-Host "[3/5] Environment created successfully!" -ForegroundColor Green
Write-Host "Note: Activation will be handled by conda install commands" -ForegroundColor Yellow
Write-Host ""

Write-Host "[4/5] Installing required packages..." -ForegroundColor Green
Write-Host ""

Write-Host "  Installing core packages (pandas, numpy, matplotlib, seaborn)..." -ForegroundColor Cyan
conda install -n $envName -c conda-forge pandas numpy matplotlib seaborn -y
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to install core packages" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "  ✓ Core packages installed" -ForegroundColor Green
Write-Host ""

Write-Host "  Installing scientific packages (scipy, scikit-learn)..." -ForegroundColor Cyan
conda install -n $envName -c conda-forge scipy scikit-learn -y
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to install scientific packages" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "  ✓ Scientific packages installed" -ForegroundColor Green
Write-Host ""

Write-Host "  Installing optional packages (jupyter, notebook)..." -ForegroundColor Cyan
conda install -n $envName -c conda-forge jupyter notebook -y
if ($LASTEXITCODE -ne 0) {
    Write-Host "  WARNING: Failed to install Jupyter (optional)" -ForegroundColor Yellow
}
else {
    Write-Host "  ✓ Jupyter installed" -ForegroundColor Green
}
Write-Host ""

Write-Host "[5/5] Verifying installation..." -ForegroundColor Green
# Use conda run to execute in the environment
conda run -n $envName python -c "import pandas, numpy, matplotlib, seaborn, scipy, sklearn; print('✓ All required packages installed successfully!')"
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Package verification failed" -ForegroundColor Red
    Write-Host "Try activating the environment manually and testing imports" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host ""

# Get Python version
Write-Host "Python version in environment:" -ForegroundColor Cyan
conda run -n $envName python --version
Write-Host ""

Write-Host "========================================" -ForegroundColor Green
Write-Host "Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Environment name: $envName" -ForegroundColor Cyan
Write-Host ""
Write-Host "To use this environment:" -ForegroundColor Yellow
Write-Host "  1. Activate:  " -NoNewline -ForegroundColor White
Write-Host "conda activate $envName" -ForegroundColor Cyan
Write-Host "  2. Run scripts: " -NoNewline -ForegroundColor White
Write-Host "python analyze_evolution.py" -ForegroundColor Cyan
Write-Host "  3. Deactivate: " -NoNewline -ForegroundColor White
Write-Host "conda deactivate" -ForegroundColor Cyan
Write-Host ""
Write-Host "Optional: Launch Jupyter Notebook" -ForegroundColor Yellow
Write-Host "  jupyter notebook interactive_analysis.ipynb" -ForegroundColor Cyan
Write-Host ""
Write-Host "To remove this environment later:" -ForegroundColor Yellow
Write-Host "  conda env remove -n $envName" -ForegroundColor Cyan
Write-Host ""
Write-Host "TIP: You may need to restart your PowerShell terminal before activating." -ForegroundColor Magenta
Write-Host ""
Read-Host "Press Enter to exit"
