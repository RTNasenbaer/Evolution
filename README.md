# Evolution Simulation

A Java-based evolution and ecosystem simulation that models entities living and evolving in a procedurally generated world with different biomes. The simulation tracks entity behavior, reproduction, survival, and population dynamics across various environmental conditions.

## 🌍 Overview

The Evolution simulation creates a 100x100 grid world populated with different biomes (Grassland, Forest, Water, Desert, Mountain, Snow) where entities can move, eat, reproduce, and die based on their energy levels and environmental conditions. The simulation supports both terminal-based and JavaFX graphical interfaces with real-time data visualization.

## 📚 Documentation

### Essential Guides

- **[Getting Started](docs/GETTING_STARTED.md)** - Installation, compilation, and first steps
- **[Cross-Platform Setup](docs/CROSS_PLATFORM.md)** - Windows, macOS, and Linux instructions
- **[User Interface Guide](docs/UI_GUIDE.md)** - Terminal, GUI, and World Builder interfaces

### Advanced Topics

- **[Data Analysis](docs/ANALYSIS.md)** - Python scripts, visualization, and statistics
- **[Seed System](docs/SEED_SYSTEM.md)** - World generation and reproducibility
- **[Architecture](docs/ARCHITECTURE.md)** - Technical implementation details
- **[Balance Adjustments](docs/BALANCE_ADJUSTMENTS.md)** - Simulation tuning reference

## ✨ Key Features

### Three Interfaces

- **Terminal Mode** - Interactive command-line interface with ANSI colors and Unicode symbols
- **JavaFX GUI** - Modern graphical interface with real-time charts and zoom/pan controls
- **World Builder** - Visual biome editor for creating custom worlds

### Simulation Features

- **Genetic Traits** - Entities have speed, mass, energy efficiency, sight range, and more
- **Energy System** - Physics-based energy consumption and food-seeking behavior
- **Reproduction** - Asexual reproduction with trait mutation
- **Multiple Biomes** - Grassland, Forest, Water, Desert, Mountain, Snow with distinct properties
- **World Seeds** - Reproducible worlds with import/export capability

### Data & Analysis

- **CSV Export** - Population tracking and entity-level data export
- **Batch Simulation** - Automated multi-run experiments
- **Real-time Charts** - Population dynamics visualization
- **Statistics** - Comprehensive entity and biome statistics

## 📁 Project Structure

```
Evolution/
├── src/                    # Java source code
│   ├── Main.java          # Terminal interface entry point
│   ├── MainApp.java       # GUI interface entry point
│   ├── WorldBuilder.java  # World Builder entry point
│   ├── BatchSimulation.java  # Batch simulation engine
│   ├── entities/          # Entity logic and traits
│   ├── world/             # World, tiles, biomes, and seeds
│   ├── ui/                # GUI components and displays
│   └── export/            # CSV export utilities
├── build/                  # Compiled .class files (auto-generated)
├── data/                   # CSV exports and simulation data
├── docs/                   # Documentation
├── analysis/               # Python analysis scripts
├── lib/                    # External libraries (JavaFX, Gson)
├── examples/               # Example seed files
└── [build scripts]         # compile.bat/sh, clean.bat/sh, etc.
```

**Key Points:**

- Source code in `src/` with proper package structure
- Compiled files automatically go to `build/` directory
- All CSV exports save to `data/` directory
- Clean separation between source, build artifacts, and data

## 🚀 Quick Start

### Prerequisites

- Java 11 or higher
- JavaFX library (included in `lib/javafx/` - **platform-specific, see [Cross-Platform Guide](docs/CROSS_PLATFORM.md)**)
- Gson library (included in `lib/gson-2.10.1.jar`)

### Platform Support

✅ **Windows** | ✅ **macOS** | ✅ **Linux**

This simulation is fully cross-platform compatible. See the [Cross-Platform Setup Guide](docs/CROSS_PLATFORM.md) for platform-specific instructions.

### Running the Simulation

**Using Universal Shell Script** (macOS/Linux):

```bash
chmod +x run.sh  # First time only
./run.sh         # Interactive menu
# Or direct: ./run.sh gui
```

**Using Batch Files** (Windows):

```cmd
compile.bat      # Compile first
# Then run with VS Code tasks or java commands
```

**Using VS Code Tasks** (All Platforms):

- Press `Ctrl+Shift+B` and select:
  - **🚀 Run Evolution Simulation** - JavaFX GUI mode
  - **⚡ Run Terminal Simulation** - Terminal mode
  - **🌍 Run World Builder** - World design tool

**Command Line (Windows)**:

```cmd
# Compile first
.\compile.bat

# Terminal mode
java -cp "build;lib/gson-2.10.1.jar" Main

# GUI mode
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media MainApp

# World Builder
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media WorldBuilder
```

**Command Line (macOS/Linux)**:

```bash
# Compile first
./compile.sh

# Terminal mode
java -cp "build:lib/gson-2.10.1.jar" Main

# GUI mode
java -cp ".:lib/javafx/lib/*:lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media MainApp

# World Builder
java -cp ".:lib/javafx/lib/*:lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media WorldBuilder
```

For detailed installation steps, platform-specific setup, and troubleshooting, see:

- **[Cross-Platform Setup Guide](docs/CROSS_PLATFORM.md)** - Windows, macOS, Linux instructions
- **[Getting Started Guide](docs/GETTING_STARTED.md)** - Basic usage and first steps

## 📖 Usage Examples

### Terminal Mode

```
> /run 100        # Run 100 simulation steps
> /auto 1000      # Auto mode with continuous display
> /spawn 50 50    # Spawn entity at (50, 50)
> /export         # Toggle CSV data export
> /stats          # Show detailed statistics
```

### GUI Mode

- Click "Run Step" for single-step execution
- Enter number and click "Run N Steps" for multiple steps
- Click entities in world view for inspection
- Use batch simulation for automated experiments

### World Builder

- Select biome brush from palette
- Click and drag to paint biomes
- Click "Export Seed" to save world
- Click "Import Seed" to load saved world

For complete command reference and interface guides, see **[UI Guide](docs/UI_GUIDE.md)**.

## � Data Analysis

The simulation provides comprehensive CSV export capabilities through the centralized `CSVExporter` utility:

**Export Types:**

- **Biome Counts**: Population per biome at each step
- **Entity Details**: Per-entity traits, position, energy, and environment data
- **Biome Details**: Ecosystem-level statistics with food availability and entity distribution

```csv
Step,EntityID,X,Y,Energy,Age,Speed,Mass,BiomeType,HasFood
0,E001,45,67,100.0,0,1.2,80.5,GRASS,true
1,E001,46,67,95.3,1,1.2,80.5,GRASS,false
...
```

**Available in all modes**: Terminal (`/export details`), GUI (Export buttons), and Batch simulation.

For analysis techniques and experiment designs, see **[Data Analysis Guide](docs/DATA_ANALYSIS.md)**.

1. **Birth**: Entities spawn with 10 energy units
2. **Movement**: Intelligent food-seeking behavior within detection range
3. **Feeding**: Consume biome-specific food for energy gain
4. **Reproduction**: Asexual reproduction at 50+ energy (creates offspring at adjacent tile)
5. **Death**: Occurs when energy drops below 1.0

### Environmental Factors

- **Biome Diversity**: Different food availability and energy yields
- **Food Regeneration**: Prevents resource depletion
- **Movement Costs**: Physics-based energy consumption
- **Population Dynamics**: Natural selection based on biome adaptation

### Evolutionary Pressures

- **Resource Competition**: Limited food sources create selection pressure
- **Biome Adaptation**: Different biomes favor different survival strategies
- **Energy Management**: Balancing movement, feeding, and reproduction
- **Population Control**: Death and reproduction maintain ecological balance

## 🔧 Technical Details

### Performance Optimizations

- **Thread Safety**: `CopyOnWriteArrayList` for concurrent access
- **Efficient Rendering**: Block combining for large world visualization
- **Asynchronous Processing**: Separate threads for UI and simulation
- **Memory Management**: Automatic entity cleanup and garbage collection

### Extensibility

- **Modular Design**: Easy to add new biomes, entity types, or behaviors
- **Configurable Parameters**: Adjustable world size, energy systems, and reproduction thresholds
- **Multiple Interfaces**: Both terminal and GUI support for different use cases

## 📈 Analysis & Visualization

The simulation includes comprehensive **Python analysis scripts** for beautiful, publication-quality visualizations:

### Quick Start

```bash
cd analysis
pip install -r requirements.txt
python analyze_evolution.py        # Main analysis
python batch_comparison.py         # Multi-seed comparison
python statistical_analysis.py     # Advanced statistics
```

### Available Analyses

**Main Analysis** (`analyze_evolution.py`):

- 🎯 Trait-survival correlations with regression analysis
- 🌍 Biome performance comparison
- ⏱️ Temporal trait evolution
- 🔗 Trait correlation matrix
- 🗺️ Spatial distribution heatmaps

**Batch Comparison** (`batch_comparison.py`):

- 🌱 Seed performance ranking
- 🧬 Successful trait pattern identification
- 🌎 Biome impact on survival
- 📊 Extinction vs survival rates

**Statistical Analysis** (`statistical_analysis.py`):

- 📈 Hypothesis testing (Pearson, Spearman, ANOVA)
- 🌲 Random Forest feature importance
- 📉 Effect size calculations
- 🔬 Multi-biome statistical comparison

### Research Applications

The simulation enables research into:

- Population dynamics and carrying capacity
- Evolutionary adaptation to environmental pressures
- Resource distribution effects on survival
- Spatial ecology and habitat preferences
- Energy-based ecosystem modeling
- Trait-survival relationship analysis

**Data Sufficiency**: Current CSV exports are **100% sufficient** for comprehensive analysis. See `analysis/DATA_SUFFICIENCY_REPORT.md` for details.

The simulation has been carefully balanced to support trait-based analysis. See **[Balance Adjustments](docs/BALANCE_ADJUSTMENTS.md)** for technical details on energy systems, trait ranges, and biome characteristics. For verification testing, see **[Balance Testing Guide](docs/BALANCE_TEST.md)**.

## 🎨 UI/UX Improvements

### Modernized Interface

The application features a completely redesigned user interface with:

- **Consistent Dialog System**: All pop-ups use the standardized `DialogUtils` class for uniform styling and behavior
- **Copyable Seed Export**: Long seed strings can be easily copied with a single click
- **Real-time Updates**: World display now updates every simulation step (previously every 10 steps)
- **Optimized Layout**: Graph relocated to side panel for better space utilization
- **Enhanced Terminal**: Improved terminal mode with Unicode symbols, borders, and colored output
- **Zoom & Pan**: Navigate large worlds with intuitive controls (Ctrl+Scroll to zoom, Space+Drag to pan)

### Terminal Display Enhancements

The terminal mode has been significantly improved:

- **Visual Clarity**: Unicode symbols (● for entities, ◆ for food) instead of ASCII characters
- **Bordered Display**: Clean box-drawing characters frame the world
- **Performance**: Up to 40x40 grid rendering with color caching
- **Legend Display**: Dynamic biome color reference
- **Rich Statistics**: Formatted tables with entity counts and percentages
- **Auto Mode**: Continuous simulation with real-time display updates

### JavaFX GUI Enhancements

The graphical interface now includes:

- **Modular Design**: Separated UI components for better maintainability
- **Responsive Layout**: Automatically adapts to window size changes
- **Side Panel Graph**: Population chart positioned for optimal visibility
- **Entity Selection**: Click on world tiles to select and inspect entities
- **Progress Indicators**: Visual feedback for long-running operations
- **Styled Dialogs**: Professional-looking alerts and input dialogs

## 🛠️ Development

### Project Structure

```
Evolution/
├── src/
│   ├── Main.java              # Enhanced terminal interface
│   ├── Main.java              # Terminal interface
│   ├── MainAppNew.java        # JavaFX GUI
│   ├── WorldBuilderNew.java  # World design tool
│   ├── BatchSimulation.java  # Batch simulation runner
│   ├── entities/
│   │   └── Entity.java        # Entity behavior and lifecycle
│   ├── ui/                    # UI components
│   │   ├── TerminalDisplay.java
│   │   ├── DialogUtils.java
│   │   ├── WorldDisplay.java
│   │   ├── GraphDisplay.java
│   │   └── ...
│   └── world/                 # Simulation engine
│       ├── World.java
│       ├── WorldSeed.java
│       ├── Tile.java
│       └── Type.java
├── docs/                      # Documentation
│   ├── GETTING_STARTED.md
│   ├── UI_GUIDE.md
│   ├── ARCHITECTURE.md
│   ├── SEED_SYSTEM.md
│   └── DATA_ANALYSIS.md
├── lib/                       # External libraries
│   ├── javafx/
│   └── gson-2.10.1.jar
└── .vscode/                   # VS Code configuration
    ├── tasks.json
    └── launch.json
```

For detailed architecture information, see **[Architecture Documentation](docs/ARCHITECTURE.md)**.

## 🌱 World Seeds

The simulation uses a seed-based world generation system:

**Seed Types:**

- **Procedural** - Numeric seeds generate worlds using BFS algorithm (e.g., `12345`)
- **String Hash** - Text seeds converted to numeric hash (e.g., `MyWorld`)
- **Designed** - Custom worlds from World Builder (e.g., `EVO_<compressed_data>`)

**Usage:**

```
# At startup, enter seed or leave empty for random
Enter world seed: 12345

# Export current world in World Builder
Click "Export Seed" → Copy seed string → Share
```

For complete seed system documentation, see **[Seed System Guide](docs/SEED_SYSTEM.md)**.

## 🔮 Future Enhancements

- Entity-level CSV export with genetic traits for detailed analysis
- Dynamic population graphs (auto-detect biomes)
- Seed import from file
- Predator-prey relationships
- Climate changes and seasonal effects
- Neural network-based entity behavior

## 📝 License

This project is available for educational and research purposes. Feel free to modify and extend the simulation for your own experiments with artificial life and ecosystem modeling.
