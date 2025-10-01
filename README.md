# Evolution Simulation

A Java-based evolution and ecosystem simulation that models entities living and evolving in a procedurally generated world with different biomes. The simulation tracks entity behavior, reproduction, survival, and population dynamics across various environmental conditions.

## 🌍 Overview

The Evolution simulation creates a 50x50 grid world populated with different biomes (Grass, Forest, Mountain, Desert) where entities can move, eat, reproduce, and die based on their energy levels and environmental conditions. The simulation supports both terminal-based and JavaFX graphical interfaces with real-time data visualization.

## 🏗️ Architecture

### Core Classes

#### `Main.java`
- **Purpose**: Command-line interface for the simulation
- **Features**:
  - Interactive terminal controls (`/run`, `/move`, `/spawn`, `/tickspeed`, `/exit`)
  - Step-by-step or continuous simulation execution
  - Entity management and world visualization
  - CSV data export for analysis
  - Configurable tick speed (default: 200ms)

#### `MainApp.java`
- **Purpose**: JavaFX-based graphical user interface
- **Features**:
  - Visual world representation with color-coded biomes
  - Real-time line charts showing population dynamics by biome
  - Interactive controls for simulation management
  - Responsive UI that adapts to screen size
  - Multi-threaded simulation execution
  - Automatic data visualization updates

#### `Display.java`
- **Purpose**: Rendering system for both terminal and JavaFX modes
- **Features**:
  - **Terminal Mode**: ANSI color-coded grid with symbols (@=entity, •=food, colored backgrounds=biomes)
  - **JavaFX Mode**: Graphical tiles with visual indicators
  - Smart block combining for large worlds to maintain performance
  - Double buffering for smooth terminal rendering

### Entity System

#### `Entity.java`
- **Attributes**:
  - Position (x, y coordinates)
  - Energy (survival resource, starts at 10)
  - Speed (movement capability, default: 1)
  - Mass (affects movement cost, default: 2)
  
- **Behaviors**:
  - **Movement**: Energy-based movement with physics simulation
  - **Food Seeking**: Intelligent pathfinding to nearby food sources within 10% of world size
  - **Random Movement**: Fallback behavior when no food is detected
  - **Eating**: Consumes food from tiles to gain energy
  - **Reproduction**: Asexual reproduction when energy ≥ 50 (splits energy in half)
  - **Death**: Occurs when energy drops below 1.0

- **Energy System**:
  - Movement cost: `0.5 × mass × speed² × distance`
  - Energy gain varies by biome food type
  - Survival threshold: 1.0 energy units

### World System

#### `World.java`
- **World Generation**:
  - 50×50 grid (2,500 tiles total)
  - Procedural biome generation using seed-based expansion
  - Breadth-first search (BFS) algorithm for natural biome boundaries
  - 4 different biome types with distinct characteristics

- **Data Management**:
  - Thread-safe collections (`CopyOnWriteArrayList`)
  - Entity and tile tracking systems
  - Population statistics by biome
  - CSV export functionality with step-by-step data logging

#### `Tile.java`
- **Properties**:
  - Position coordinates (x, y)
  - Biome type (affects food availability and energy)
  - Food presence (boolean state)
  - Automated food regeneration system

- **Food Regeneration**:
  - Asynchronous regeneration using `ScheduledExecutorService`
  - Base delay: 5 seconds, adjusted by tick speed and world size
  - Prevents immediate re-consumption of food sources

#### `Type.java` (Biome Definitions)
- **Grass Biome**: 
  - Color: Bright Green (#00FF00)
  - Food Chance: 10%
  - Food Energy: 2 units
  
- **Forest Biome**:
  - Color: Dark Green (#228B22)
  - Food Chance: 15% (highest food availability)
  - Food Energy: 5 units (highest energy gain)
  
- **Mountain Biome**:
  - Color: Gray (#808080)
  - Food Chance: 5%
  - Food Energy: 1 unit
  
- **Desert Biome**:
  - Color: Gold (#FFD700)
  - Food Chance: 2% (lowest food availability)
  - Food Energy: 0.5 units (lowest energy gain)

### Test Framework

#### `Test.java`
- Simple JavaFX application for testing chart functionality
- Demonstrates real-time data visualization capabilities
- Used for UI component development and testing

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- JavaFX library (included in `lib/javafx/`)
- Gson library (included in `lib/gson/`)

### Running the Simulation

#### Terminal Mode
```bash
java -cp "src:lib/gson/lib/*" Main
```

**Available Commands:**
- `/run` - Execute one simulation step
- `/run <n>` - Execute n simulation steps
- `/move <x> <y>` - Move first entity to coordinates
- `/move <ex> <ey> <x> <y>` - Move entity from (ex,ey) to (x,y)
- `/spawn <x> <y>` - Create new entity at coordinates
- `/tickspeed <ms>` - Set simulation speed in milliseconds
- `/exit` - Quit simulation

#### JavaFX GUI Mode
```bash
java --module-path lib/javafx/lib --add-modules javafx.controls,javafx.fxml -cp "src:lib/gson/lib/*" MainApp
```

**GUI Features:**
- **Run Step**: Execute single simulation step
- **Run N Steps**: Execute multiple steps continuously
- **Spawn Entity**: Add new entity at specified coordinates
- **Move Entity**: Relocate existing entity
- **Set Tickspeed**: Adjust simulation speed
- **Stop**: Halt continuous simulation
- **Real-time Charts**: Population tracking by biome

## 📊 Data Output

The simulation automatically generates `biome_counts.csv` with population data:
```csv
Step,GRASS,MOUNTAIN,FOREST,DESERT
10,1,0,1,0
20,0,0,2,0
...
```

This data can be imported into spreadsheet applications or data analysis tools for further study.

## 🎯 Simulation Mechanics

### Entity Lifecycle
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

## 📈 Analysis Possibilities

The simulation enables research into:
- Population dynamics and carrying capacity
- Evolutionary adaptation to environmental pressures
- Resource distribution effects on survival
- Spatial ecology and habitat preferences
- Energy-based ecosystem modeling

## 🛠️ Development

### Project Structure
```
Evolution/
├── src/
│   ├── Main.java           # Terminal interface
│   ├── MainApp.java        # JavaFX GUI
│   ├── Display.java        # Rendering system
│   ├── Test.java           # Testing utilities
│   ├── entities/
│   │   └── Entity.java     # Entity behavior and lifecycle
│   └── world/
│       ├── World.java      # World management and generation
│       ├── Tile.java       # Individual tile properties
│       └── Type.java       # Biome definitions
├── lib/                    # External libraries
├── biome_counts.csv       # Generated data output
└── README.md              # This file
```

## 🌍 **NEW: Advanced Seed System**

The world generation has been completely overhauled with a new unified seed system:

### **WorldSeed Features:**
- **Universal Format**: One seed format works across all applications (Main, MainApp, WorldBuilder)
- **Complete World Data**: Seeds contain all terrain information, no procedural generation needed
- **Backward Compatibility**: Legacy numeric seeds still work
- **Compressed Storage**: Efficient string representation for easy sharing
- **Rich Metadata**: Seeds include creation info, biome statistics, and generation details

### **Seed Types:**
- **Procedural Seeds**: Generated from numeric values using original BFS algorithm
- **Designed Seeds**: Hand-crafted in WorldBuilder with exact terrain control
- **Legacy Seeds**: Old numeric seeds automatically converted

### **Usage:**
```bash
# Terminal mode - supports all seed formats
Enter world seed: EVO1F8B2A...  # New seed string format
Enter world seed: 12345         # Legacy numeric seed
Enter world seed: MyWorld       # String hash seed

# WorldBuilder - create and export seeds
1. Design terrain by painting biomes
2. Click "Export as Seed" to get shareable seed string
3. Use "Import Seed" to load existing seeds for editing
```

### **Seed String Format:**
- **Prefix**: `EVO` identifies Evolution seeds
- **Compression**: GZIP compressed biome data and metadata
- **Portability**: Works across all Evolution applications seamlessly

The new system eliminates the need for separate `.dat` files and ensures perfect world recreation every time! 🎯

### Future Enhancement Ideas
- Genetic algorithms for entity trait evolution
- Predator-prey relationships
- Climate changes and seasonal effects
- Multiple species with different characteristics
- Advanced AI behaviors and decision making
- Network-based multi-user simulations

## 📝 License

This project is available for educational and research purposes. Feel free to modify and extend the simulation for your own experiments with artificial life and ecosystem modeling.