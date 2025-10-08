# Data Analysis Guide

Complete guide for analyzing Evolution simulation data with Python.

---

## 📦 Quick Start

### 1. Install Dependencies

**Windows (PowerShell):**

```powershell
cd analysis
.\setup_conda_env.ps1
conda activate evolution-analysis
```

**Linux/Mac:**

```bash
cd analysis
chmod +x setup_conda_env.sh
./setup_conda_env.sh
conda activate evolution-analysis
```

**Troubleshooting Windows Setup:**
If you get an "execution policy" error:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### 2. Run Analysis

```bash
# Comprehensive analysis of latest simulation
python analyze_evolution.py

# Compare multiple simulations
python batch_comparison.py

# Advanced statistical tests
python statistical_analysis.py

# Interactive exploration
jupyter notebook interactive_analysis.ipynb
```

---

## 📊 Analysis Scripts

### 1. Main Analysis (`analyze_evolution.py`)

**Purpose:** Comprehensive single-run analysis

**Output:** 6 visualizations + statistical summary

- `01_trait_survival.png` - Trait-survival correlations
- `02_biome_performance.png` - Environmental impacts
- `03_trait_evolution.png` - Temporal dynamics
- `04_trait_correlations.png` - Trait interaction matrix
- `05_spatial_distribution.png` - Grid-based heatmaps
- `00_analysis_summary.txt` - Statistical report

**Usage:**

```bash
# Auto-detect latest data
python analyze_evolution.py

# Specify custom paths
python analyze_evolution.py /path/to/csv/files /path/to/output
```

### 2. Batch Comparison (`batch_comparison.py`)

**Purpose:** Multi-seed performance comparison

**Output:** 4 comparative visualizations + rankings

- `01_seed_performance.png` - Seed rankings by survival
- `02_trait_success_patterns.png` - Winning trait combinations
- `03_biome_impact.png` - Environmental factors
- `00_best_seeds_report.txt` - Top performers report

**Usage:**

```bash
# Requires batch_results_*.csv files
python batch_comparison.py
```

### 3. Statistical Analysis (`statistical_analysis.py`)

**Purpose:** Advanced hypothesis testing and modeling

**Output:** Statistical reports + feature importance

- `01_statistical_tests.txt` - Pearson, Spearman, t-tests
- `02_feature_importance.png` - Random Forest analysis
- `03_biome_statistics.txt` - ANOVA results

**Usage:**

```bash
python statistical_analysis.py
```

### 4. Interactive Notebook (`interactive_analysis.ipynb`)

**Purpose:** Exploratory data analysis with Jupyter

**Features:**

- Custom visualizations
- Live data exploration
- Modifiable analysis code
- Export custom figures

**Usage:**

```bash
pip install jupyter notebook
jupyter notebook interactive_analysis.ipynb
```

---

## 📁 Data Requirements

The analysis scripts expect CSV files from Evolution simulation exports:

### Required Files

1. **entity*details*\*.csv** - Per-entity, per-step data (15 columns)

   - Step, EntityID, X, Y, Energy, Age, Speed, Mass, EnergyEfficiency,
     ReproductionThreshold, SightRange, MetabolismRate, MaxLifespan,
     BiomeType, HasFood

2. **biome*details*\*.csv** - Per-biome, per-step aggregates (5 columns)

   - Step, BiomeType, TotalTiles, TilesWithFood, EntityCount

3. **batch*results*\*.csv** - Per-simulation summaries (18 columns)
   - Seed, FinalEntityCount, MaxEntityCount, StepsRun, ExecutionTime(ms),
     EntityID, X, Y, Energy, Age, Speed, Mass, EnergyEfficiency,
     ReproductionThreshold, SightRange, MetabolismRate, MaxLifespan, BiomeType

### Auto-Detection

All scripts automatically find the most recent CSV files in the `data/` directory or project root.

### Manual Override

```python
python analyze_evolution.py /custom/path/to/csvs /custom/output/path
```

---

## 🔧 Setup Methods

### Method 1: Conda (Recommended)

**Pros:** Isolated environment, handles all dependencies, cross-platform
**Cons:** Requires Conda/Miniconda installation

```bash
cd analysis
.\setup_conda_env.ps1  # Windows
./setup_conda_env.sh    # Linux/Mac
```

**Conda Commands:**

```bash
conda activate evolution-analysis    # Start working
conda deactivate                     # Stop working
conda env list                       # List environments
conda env remove -n evolution-analysis  # Uninstall
```

### Method 2: pip (Simple)

**Pros:** Quick, no extra software needed
**Cons:** System-wide installation, potential conflicts

```bash
cd analysis
pip install -r requirements.txt
```

### Method 3: venv (Isolated pip)

**Pros:** Isolated without Conda, standard Python tool
**Cons:** Manual activation required

```bash
cd analysis
python -m venv venv
venv\Scripts\activate    # Windows
source venv/bin/activate # Linux/Mac
pip install -r requirements.txt
```

---

## 📦 Dependencies

All scripts require:

- **pandas** - Data manipulation
- **numpy** - Numerical computing
- **matplotlib** - Plotting
- **seaborn** - Statistical visualization
- **scipy** - Statistical tests
- **scikit-learn** - Machine learning (feature importance)

Installed automatically with any setup method.

---

## 🎨 Visualization Features

All generated figures include:

- ✅ High resolution (300 DPI) for publication
- ✅ Professional color schemes (seaborn palettes)
- ✅ Clear labels and legends
- ✅ Statistical annotations
- ✅ Grid lines for readability
- ✅ Consistent styling across all plots

---

## 📈 Analysis Workflows

### Workflow 1: Single Simulation Analysis

```bash
# 1. Run simulation (export CSVs)
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" MainApp

# 2. Analyze data
cd analysis
conda activate evolution-analysis
python analyze_evolution.py

# 3. Review outputs
ls analysis_output/
```

### Workflow 2: Multi-Seed Comparison

```bash
# 1. Run batch simulation
java -cp "build;lib/gson-2.10.1.jar" BatchSimulation

# 2. Compare seeds
cd analysis
python batch_comparison.py

# 3. Review rankings
cat batch_analysis_output/00_best_seeds_report.txt
```

### Workflow 3: Statistical Deep-Dive

```bash
# 1. Run comprehensive analysis
python analyze_evolution.py
python batch_comparison.py
python statistical_analysis.py

# 2. Review all outputs
ls *_output/

# 3. Interactive exploration
jupyter notebook interactive_analysis.ipynb
```

---

## 🔬 Advanced Usage

### Custom Aggregations

```python
import pandas as pd

# Load raw entity data
df = pd.read_csv('batch_results_terminal_*.csv')

# Calculate per-seed statistics
seed_stats = df.groupby('Seed').agg({
    'Age': ['mean', 'std', 'min', 'max'],
    'Energy': ['mean', 'std'],
    'Speed': ['mean', 'median'],
    'Mass': ['mean', 'std']
})

# Find best performing seeds
best = seed_stats.nlargest(10, ('Age', 'mean'))
```

### Trait Distribution Analysis

```python
import matplotlib.pyplot as plt
import seaborn as sns

# Load data
df = pd.read_csv('entity_details_*.csv')

# Plot trait distributions by biome
for trait in ['Speed', 'Mass', 'EnergyEfficiency']:
    plt.figure(figsize=(10, 6))
    sns.violinplot(data=df, x='BiomeType', y=trait)
    plt.title(f'{trait} Distribution by Biome')
    plt.xticks(rotation=45)
    plt.tight_layout()
    plt.savefig(f'{trait}_by_biome.png', dpi=300)
```

### Survival Prediction

```python
from sklearn.ensemble import RandomForestClassifier

# Prepare data
df['Survived'] = (df['Age'] > df['Age'].median()).astype(int)
features = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 'MetabolismRate']

X = df[features]
y = df['Survived']

# Train model
model = RandomForestClassifier(n_estimators=100)
model.fit(X, y)

# Feature importance
importance = pd.DataFrame({
    'Feature': features,
    'Importance': model.feature_importances_
}).sort_values('Importance', ascending=False)

print(importance)
```

---

## ⚠️ Troubleshooting

### "No data files found"

→ Ensure CSV files exist in `data/` or project root
→ Run a simulation with CSV export enabled first

### "Module not found" error

→ Activate conda environment: `conda activate evolution-analysis`
→ Or install dependencies: `pip install -r requirements.txt`

### "All simulations extinct" warning

→ Normal with current balance settings
→ Scripts handle this gracefully with informative messages

### Windows PowerShell issues

→ Set execution policy: `Set-ExecutionPolicy RemoteSigned -Scope CurrentUser`
→ Use PowerShell (not Command Prompt)
→ Run as Administrator if permission errors occur

### Large file memory errors

→ Large CSV files (>1M rows) may require sampling
→ Use chunked reading: `pd.read_csv(..., chunksize=10000)`
→ Or increase available RAM

---

## 📚 See Also

- **GETTING_STARTED.md** - Running the simulation
- **SEED_SYSTEM.md** - Understanding world seeds
- **ARCHITECTURE.md** - Project technical overview

---

## 🎓 Scientific Applications

This analysis suite supports research in:

- **Evolutionary Biology** - Trait-survival relationships
- **Artificial Life** - Population dynamics and emergence
- **Complex Systems** - Self-organization and adaptation
- **Spatial Ecology** - Habitat preferences and spatial patterns
- **Genetic Algorithms** - Parameter optimization
- **Energy Economics** - Resource management strategies

---

**Ready to analyze? Install dependencies and run your first analysis!**
