# Evolution Simulation - Data Analysis Scripts

Python scripts for analyzing Evolution simulation data.

---

## � Complete Documentation

**See [docs/ANALYSIS.md](../docs/ANALYSIS.md) for full documentation** including:

- Setup instructions (Conda, pip, venv)
- Detailed script descriptions
- Advanced usage examples
- Troubleshooting guide

---

## ⚡ Quick Start

### 1. Setup Environment

**Windows:**

```powershell
.\setup_conda_env.ps1
conda activate evolution-analysis
```

**Linux/Mac:**

```bash
chmod +x setup_conda_env.sh
./setup_conda_env.sh
conda activate evolution-analysis
```

### 2. Run Analysis

```bash
python analyze_evolution.py        # Main analysis
python batch_comparison.py         # Seed comparison
python statistical_analysis.py     # Advanced stats
```

---

## 📊 Available Scripts

| Script                       | Purpose                           | Output Files |
| ---------------------------- | --------------------------------- | ------------ |
| `analyze_evolution.py`       | Comprehensive single-run analysis | 6 files      |
| `batch_comparison.py`        | Multi-seed performance comparison | 4 files      |
| `statistical_analysis.py`    | Hypothesis testing & modeling     | 3 files      |
| `interactive_analysis.ipynb` | Jupyter notebook for exploration  | Interactive  |

---

## 📁 Required Data

Scripts auto-detect CSV files from simulation exports:

- `entity_details_*.csv` - Per-entity temporal data
- `biome_details_*.csv` - Biome statistics
- `batch_results_*.csv` - Multi-seed results

---

## 📦 Dependencies

```bash
pip install -r requirements.txt
```

Installs: pandas, numpy, matplotlib, seaborn, scipy, scikit-learn

---

## 📚 Documentation Structure

- **[docs/ANALYSIS.md](../docs/ANALYSIS.md)** - Complete analysis guide
- **[docs/GETTING_STARTED.md](../docs/GETTING_STARTED.md)** - Run simulation
- **[docs/DATA_ANALYSIS.md](../docs/DATA_ANALYSIS.md)** - Data formats
  - Biome distribution
  - Speed trait distribution

### Batch Comparison (`batch_comparison.py`)

Creates 3 visualizations + best seeds report:

1. **Seed Performance Comparison**

   - Success scores for each seed
   - Population dynamics (final vs max)
   - Simulation duration distribution
   - Extinction vs survival rates

2. **Trait Success Patterns**

   - Violin plots showing trait distributions by outcome
   - Compares extinct vs thriving populations
   - Identifies winning trait combinations

3. **Biome Impact on Success**
   - How initial biome distribution affects outcomes
   - Box plots comparing successful vs failed runs
   - Environmental factor analysis

## 📁 Expected Data Files

The scripts auto-detect the most recent CSV files:

- `entity_details_*.csv` - Per-entity, per-step detailed data
- `biome_details_*.csv` - Per-step biome-level aggregated data
- `*batch_results*.csv` - Summary statistics per simulation run

## 📈 Data Sufficiency Analysis

### ✅ Currently Available Data

The CSV exports contain **comprehensive data** for trait-survival analysis:

#### Entity Details CSV

- **Temporal**: Step number
- **Spatial**: X, Y coordinates, BiomeType, HasFood
- **Identity**: EntityID
- **Vitals**: Energy, Age
- **Traits**: Speed, Mass, EnergyEfficiency, SightRange, MetabolismRate, ReproductionThreshold, MaxLifespan

#### Biome Details CSV

- **Temporal**: Step number
- **Environmental**: BiomeType, TotalTiles, TilesWithFood
- **Population**: EntityCount, EntityIDs
- **Aggregates**: AvgEntityEnergy, AvgEntityAge

#### Batch Results CSV

- **Outcomes**: FinalEntityCount, MaxEntityCount, StepsRun
- **Performance**: ExecutionTime(ms)
- **Average Traits**: AvgSpeed, AvgMass, AvgEnergyEfficiency, AvgSightRange, AvgMetabolismRate, AvgReproductionThreshold
- **Biome Counts**: BiomeCount_GRASS, BiomeCount_MOUNTAIN, etc.

### 🔍 Additional Data That Would Enhance Analysis

While current data is sufficient for comprehensive analysis, these additions would provide deeper insights:

#### 1. **Reproduction Events** (HIGH VALUE)

```
Timestamp, ParentID, ChildID, InheritedTraits, Mutations, BiomeType
```

- Track genetic lineages
- Analyze mutation success rates
- Identify successful breeding strategies

#### 2. **Death Events** (HIGH VALUE)

```
Timestamp, EntityID, CauseOfDeath, Age, Energy, X, Y, BiomeType
```

- Understand failure modes (starvation, old age, stuck in unfavorable biome)
- Identify dangerous biomes/situations
- Calculate survival probability distributions

#### 3. **Movement History** (MEDIUM VALUE)

```
Step, EntityID, FromX, FromY, ToX, ToY, BiomeFrom, BiomeTo, EnergySpent
```

- Analyze migration patterns
- Understand biome transition strategies
- Calculate movement efficiency

#### 4. **Food Consumption Events** (MEDIUM VALUE)

```
Timestamp, EntityID, FoodValue, Energy Before, Energy After, BiomeType
```

- Track feeding efficiency
- Understand food competition
- Analyze energy economics

#### 5. **Reproduction Attempts** (MEDIUM VALUE)

```
Timestamp, EntityID, Success(bool), Energy, ReproductionThreshold, Reason
```

- Track failed reproduction attempts
- Understand reproduction barriers
- Optimize reproduction thresholds

#### 6. **Entity Interaction Events** (LOW VALUE)

```
Timestamp, Entity1ID, Entity2ID, InteractionType, Distance
```

- Track competition/cooperation (if implemented)
- Analyze crowding effects
- Study territorial behavior

### 💡 Recommendations

**For immediate analysis**: Current data is **100% sufficient** for comprehensive trait-survival analysis under different spatial conditions.

**For enhanced insights**: Consider adding death events and reproduction tracking to the CSVExporter.java to understand _why_ organisms fail or succeed, not just _that_ they do.

**Implementation suggestion**: Add to CSVExporter.java:

```java
public void exportLifecycleEvents(String filename, List<Event> events)
public void exportDeathLog(String filename, Map<String, DeathEvent> deaths)
public void exportReproductionLog(String filename, List<ReproductionEvent> reproductions)
```

## 🎨 Visualization Features

All visualizations include:

- ✨ High-resolution output (300 DPI)
- 🎨 Beautiful color schemes
- 📊 Clear labels and legends
- 📈 Statistical annotations
- 🔍 Multiple perspectives on the same data

## 🛠️ Customization

Both scripts support command-line arguments:

```bash
# Custom data directory and output location
python analyze_evolution.py /path/to/csv/files /path/to/output

# Same for batch comparison
python batch_comparison.py /path/to/csv/files /path/to/output
```

## 📝 Output Files

### Main Analysis Output

```
analysis_output/
├── 00_analysis_summary.txt          # Text summary of all data
├── 01_trait_survival.png            # Trait-survival correlations
├── 02_biome_performance.png         # Biome analysis
├── 03_trait_evolution.png           # Temporal dynamics
├── 04_trait_correlations.png        # Trait interaction matrix
└── 05_spatial_distribution.png      # Grid-based spatial analysis
```

### Batch Comparison Output

```
batch_analysis_output/
├── 00_best_seeds_report.txt         # Top performing seeds
├── 01_seed_performance.png          # Seed comparison
├── 02_trait_success_patterns.png    # Winning trait combinations
└── 03_biome_impact.png              # Environmental factors
```

## 🔬 Scientific Use

These scripts generate **publication-quality** visualizations suitable for:

- Academic papers on evolutionary algorithms
- Cellular automata research
- Artificial life studies
- Complex systems analysis
- Education and presentations

## ⚠️ Known Limitations

1. **Extinction Analysis**: Current batch data shows 100% extinction rate. Scripts handle this gracefully but note that trait averages will be 0.00 for extinct populations.

2. **Memory Usage**: Large CSV files (>1M rows) may require significant RAM. Consider sampling or chunked processing for very large datasets.

3. **Missing Data**: Scripts skip analyses when required data is unavailable rather than crashing.

## 🤝 Contributing

To add new visualizations:

1. Add a new method to the `EvolutionAnalyzer` or `BatchComparator` class
2. Follow the existing pattern: load data → process → visualize → save
3. Add the method call to `run_complete_analysis()`

## 📄 License

These analysis scripts are part of the Evolution Simulation project.
