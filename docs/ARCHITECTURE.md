# Evolution Simulation Architecture

## Overview

The Evolution simulation is built with a modular architecture that separates concerns between simulation logic, UI presentation, and data management. The system uses JavaFX for graphical interfaces and supports both GUI and terminal modes.

## Core Classes

### Main.java

**Purpose**: Terminal-based interactive interface for the simulation

**Key Components**:

- Command parser supporting 11 different commands
- Auto mode for continuous simulation with periodic display refresh
- Interactive entity spawning and movement
- CSV export toggle for data collection (uses CSVExporter)
- Seed display and management

**Main Methods**:

```java
void run()                              // Main game loop
void processCommand(String input)       // Command dispatcher
void autoMode(int steps)                // Continuous simulation mode
void spawnEntityInteractive(int x, int y)  // Entity placement
```

**Dependencies**: World, Entity, TerminalDisplay, WorldSeed

### MainAppNew.java

**Purpose**: Modern JavaFX GUI with responsive design and real-time visualization

**Key Components**:

- Three-panel layout: world display, controls, population graph
- Zoom/pan functionality for world view
- Entity inspection on click
- Batch simulation support
- Real-time chart updates

**Main Methods**:

```java
void start(Stage primaryStage)          // JavaFX entry point
void setupLayout()                      // Build UI components
void runSteps(int n)                    // Execute simulation steps
void showEntityInspector(Entity e)      // Display entity details
void showStatistics()                   // Population statistics dialog
```

**Dependencies**: WorldDisplay, GraphDisplay, ConfigDisplay, DialogUtils, ResponsiveLayoutManager

**Update Frequency**: Changed from every 10 steps to every step for real-time display

### WorldBuilderNew.java

**Purpose**: Visual world design tool for creating custom biome layouts

**Key Components**:

- Canvas-based world editor
- Brush tools for each biome type
- Seed import/export functionality
- Random world generation
- World reset and clear functions

**Main Methods**:

```java
void start(Stage primaryStage)          // JavaFX entry point
void setupCanvas()                      // Initialize drawing canvas
void handleCanvasClick(MouseEvent e)    // Tile painting
void exportAsSeed()                     // Generate shareable seed
void importFromSeed()                   // Load world from seed
```

**Dependencies**: WorldSeed, WorldBuilderControls, DialogUtils

## Entity System

### Entity.java

**Purpose**: Core organism with genetic traits, energy management, and behavior

**Genetic Traits**:

```java
double speed               // Movement rate (0.5-2.0)
double mass                // Entity mass (50-150)
double energyEfficiency    // Energy use multiplier (0.7-1.3)
double reproductionThreshold  // Energy needed to reproduce (60-140)
double sightRange          // Food detection distance (3.0-8.0)
double metabolismRate      // Base energy consumption (0.8-1.2)
double maxLifespan         // Maximum age (800-1200 steps)
```

**Core Methods**:

```java
void update(World world)                // Per-step behavior
void move(int dx, int dy, World world)  // Position change with energy cost
void eat(Tile tile)                     // Consume food from tile
Entity createOffspring()                // Reproduction with mutation
boolean shouldReproduce()               // Energy threshold check
void die()                              // Death handling
```

**Energy System**:

- Base consumption: `mass * metabolismRate * 0.01`
- Movement cost: `speed * mass * 0.001 * movementModifier * energyEfficiency`
- Food energy gain: Biome-specific (5-25 per tile)
- Death threshold: Energy < 1.0 or age > maxLifespan

**Mutation System**:

- 50% chance of mutation per trait
- ±10% variation from parent value
- Clamped to valid ranges

## World System

### World.java

**Purpose**: Main simulation engine managing entities, tiles, and world state

**Key Components**:

```java
static final int SIZE = 100            // World dimensions
Tile[][] tiles                         // 2D grid of tiles
CopyOnWriteArrayList<Entity> entities  // Thread-safe entity list
int stepCounter                        // Simulation step counter
String worldSeed                       // Current world seed
```

**Core Methods**:

```java
void step()                            // Execute one simulation step
void spawnEntity(int x, int y)         // Create new entity
void removeEntity(Entity e)            // Remove dead entity
Tile getTile(int x, int y)             // Get tile at position
List<Entity> getEntitiesInRange(int x, int y, double range)
int getPopulation()                    // Count living entities
Map<Type, Integer> getEntityDistribution()  // Entities per biome
```

**Simulation Loop**:

1. Update all entities (movement, feeding, reproduction)
2. Process entity deaths
3. Regenerate food on tiles (probability-based)
4. Increment step counter
5. Notify observers (GUI/terminal)

**Thread Safety**: Uses `CopyOnWriteArrayList` to allow concurrent read/write during iteration

### WorldSeed.java

**Purpose**: Seed-based world generation and import/export

**Seed Types**:

1. **Procedural**: Numeric seed → BFS biome generation
2. **String Hash**: Text seed → numeric hash → BFS generation
3. **Designed**: `EVO_<base64>` → compressed tile data

**Core Methods**:

```java
World generateWorld(String seed, int width, int height)
String exportWorldToSeed(World world)
World importSeedAndCreateWorld(String seed)
boolean isValidSeed(String seed)
String compressTileData(String json)
String decompressTileData(String compressed)
```

**BFS Generation Algorithm**:

1. Parse seed to numeric value (hash if string)
2. Initialize Random with seed
3. Select N random starting positions for biomes
4. For each starting position:
   - Create queue with starting tile
   - While queue not empty:
     - Pop tile and check neighbors
     - Randomly assign biome type to empty neighbors
     - Add new tiles to queue
5. Fill remaining empty tiles with default biome

**Compression**:

- JSON → GZIP → Base64 → `EVO_` prefix
- Reduces large world data by ~90%

### Tile.java

**Purpose**: Individual grid cell with biome type and food state

**Properties**:

```java
Type type          // Biome type (GRASSLAND, FOREST, etc.)
boolean hasFood    // Food availability flag
int x, y           // Grid position
```

**Methods**:

```java
void regenerateFood()  // Probability-based food respawn
boolean hasFood()      // Check food availability
void consumeFood()     // Remove food from tile
```

### Type.java (Enum)

**Purpose**: Biome definitions with properties

**Biome Types**:

```java
GRASSLAND  // High food chance, low energy, easy movement
FOREST     // Medium food chance, high energy, slow movement
WATER      // No food, blocks movement
DESERT     // Low food chance, medium energy, normal movement
MOUNTAIN   // Low food chance, low energy, very slow movement
SNOW       // Very low food chance, medium energy, slow movement
```

**Properties**:

```java
double foodChance           // Probability of food respawn
double foodEnergy           // Energy gained from food
double movementModifier     // Movement cost multiplier
String color                // Hex color code for rendering
```

## Export System

### CSVExporter.java

**Purpose**: Centralized CSV export utility for consistent data formatting across all interfaces

**Location**: `src/export/CSVExporter.java`

**Key Features**:

- Automatic header generation for new files
- Append mode for continuous data collection
- Timestamped filename generation
- File writability validation
- Thread-safe export operations

**Export Methods**:

```java
// Export simple biome population counts
void exportBiomeCounts(Map<Type, Integer> counts, int step,
                       String filename, boolean appendMode)

// Export detailed per-entity data
void exportEntityDetails(World world, int step, String filename)

// Export biome-level ecosystem statistics
void exportBiomeDetails(World world, int step, String filename)

// Generate timestamped filename
String createTimestampedFilename(String prefix, String extension)

// Validate file writability
boolean isWritable(String filename)
```

**Usage Examples**:

```java
import export.CSVExporter;

// Export entity details
CSVExporter.exportEntityDetails(world, stepNumber, "entity_data.csv");

// Export biome statistics
CSVExporter.exportBiomeDetails(world, stepNumber, "biome_data.csv");

// Create timestamped file
String filename = CSVExporter.createTimestampedFilename("results", "csv");
// Results in: "results_1234567890.csv"
```

**Integration Points**:

- Used by `Main.java` for terminal export commands
- Used by `MainApp.java` for GUI export buttons
- Used by `BatchSimulation.java` for automated batch exports

## UI Components

### DialogUtils.java

**Purpose**: Standardized dialog system for consistent UI

**Dialog Types**:

```java
void showInfo(String title, String message)
void showError(String title, String message)
void showSuccess(String title, String message)
Optional<String> showTextInput(String title, String prompt)
void showCopyableText(String title, String text)
boolean showConfirmation(String title, String message)
Dialog<?> createCustomDialog(String title)
```

**Styling**:

- Consistent header colors (blue/green/red)
- AppStyles integration for fonts and spacing
- Null-safe CSS application (fixed NPE issue)

**Special Features**:

- **Copyable Text Dialog**: Large text areas with clipboard copy button
- **Progress Dialog**: Non-blocking progress indicators
- **Custom Dialogs**: Builder pattern for complex dialogs

### WorldDisplay.java

**Purpose**: JavaFX canvas rendering of world grid

**Features**:

- Tile-based rendering with biome colors
- Entity overlay (red circles)
- Zoom and pan support
- Entity click detection
- Real-time updates

**Rendering Process**:

```java
void render() {
    clearCanvas();
    for (Tile tile : world.tiles) {
        drawTile(tile);
    }
    for (Entity entity : world.entities) {
        drawEntity(entity);
    }
}
```

### GraphDisplay.java

**Purpose**: Real-time population dynamics chart

**Current Implementation**:

- LineChart with time on X-axis
- Separate series for each biome type
- Updates every simulation step
- Legend for biome identification

**Planned Enhancement**:

- Dynamic series creation based on biomes present in world
- Only show biomes that actually exist
- Automatic color assignment

### TerminalDisplay.java

**Purpose**: ANSI-colored terminal rendering

**Features**:

- Color-coded biomes using ANSI escape codes
- Unicode symbols: ● (entity), ◆ (food)
- Box drawing borders: ╔═╗║╚╝
- Dynamic legend showing biome counts
- Color caching for performance

**Rendering Algorithm**:

```java
void renderWorld(World world) {
    // Print top border
    System.out.println("╔" + "═".repeat(width) + "╗");

    // Print tiles with colors
    for (int y = 0; y < height; y++) {
        System.out.print("║");
        for (int x = 0; x < width; x++) {
            Tile tile = world.getTile(x, y);
            String color = getCachedColor(tile.type);
            String symbol = getSymbol(tile);
            System.out.print(color + symbol + RESET);
        }
        System.out.println("║");
    }

    // Print bottom border and legend
    System.out.println("╚" + "═".repeat(width) + "╝");
    printLegend();
}
```

**Color Conversion**:

- Hex colors → RGB → ANSI 256-color codes
- Cached in HashMap for fast lookup

### Other UI Classes

**AppStyles.java**: Centralized styling constants

- Colors (primary, success, error, background)
- Fonts (title, content sizes)
- Spacing (small, medium, large)

**ConfigDisplay.java**: Simulation control panel

- Step counter display
- Population counter
- Run buttons (single step, N steps)
- Speed slider
- Batch simulation controls

**WorldBuilderControls.java**: World Builder tool panel

- Biome brush radio buttons
- Import/Export seed buttons
- Clear and randomize buttons

**ResponsiveLayoutManager.java**: Adaptive layout sizing

- Calculates optimal sizes based on window dimensions
- Responsive font scaling
- Dynamic component sizing

**ZoomPanHandler.java**: World navigation controls

- Mouse wheel zoom
- Click-drag panning
- Zoom limits (min/max)
- Coordinate translation

## Data Flow

```
┌──────────────────────────────────────────────────────────┐
│                      User Input                          │
│          (Terminal commands / GUI interactions)          │
└────────────────┬─────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────┐
│                   Controller Layer                       │
│              (Main / MainAppNew / WorldBuilder)          │
└────────────────┬─────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────┐
│                   World (Simulation Engine)              │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │  Step Loop:                                    │    │
│  │  1. Update all entities                        │    │
│  │  2. Process deaths                             │    │
│  │  3. Regenerate food                            │    │
│  │  4. Increment counter                          │    │
│  └────────────────────────────────────────────────┘    │
└────────┬──────────────────────────────────┬─────────────┘
         │                                  │
         ▼                                  ▼
┌──────────────────────┐      ┌──────────────────────────┐
│   Entity Updates     │      │    Tile Updates          │
│                      │      │                          │
│  - Move              │      │  - Food regeneration     │
│  - Eat               │      │  - Type-specific rules   │
│  - Reproduce         │      │                          │
│  - Die               │      │                          │
└──────────────────────┘      └──────────────────────────┘
         │
         │
         ▼
┌──────────────────────────────────────────────────────────┐
│                    Display Update                        │
│                                                          │
│  Terminal: TerminalDisplay.renderWorld()                │
│  GUI: WorldDisplay.render() + GraphDisplay.update()     │
└────────────────┬─────────────────────────────────────────┘
                 │
                 ▼
┌──────────────────────────────────────────────────────────┐
│                    Data Export (Optional)                │
│                                                          │
│  CSV: BatchSimulation / Terminal export toggle          │
└──────────────────────────────────────────────────────────┘
```

## Thread Safety

### CopyOnWriteArrayList for Entities

- Allows iteration while modifying entity list
- Thread-safe for concurrent reads
- Used in World.entities

### JavaFX Thread Management

```java
// UI updates must use Platform.runLater()
Platform.runLater(() -> {
    worldDisplay.render();
    graphDisplay.update();
});
```

### Simulation Thread

- Daemon thread for continuous simulation
- Separate from JavaFX application thread
- Uses sleep() for speed control

## Extension Points

### Adding New Biomes

1. **Add to Type.java**:

```java
public enum Type {
    // ... existing types ...
    TUNDRA(0.05, 8.0, 1.5, "#A0D0F0");
}
```

2. **Update Rendering**:

- WorldDisplay: Add color mapping
- TerminalDisplay: Add ANSI color

3. **Update World Generation**:

- WorldSeed: Include in BFS algorithm

### Adding Entity Traits

1. **Add Field to Entity.java**:

```java
private double agility; // New trait
```

2. **Update Constructor**:

```java
this.agility = 0.5 + random.nextDouble();
```

3. **Mutation Logic**:

```java
offspring.agility = mutate(this.agility, 0.1, 0.1, 1.0);
```

4. **CSV Export**:

```java
csvWriter.write(entity.getAgility() + ",");
```

### Adding UI Components

1. **Create Component Class** in `ui/` package
2. **Use AppStyles**:

```java
button.setStyle(AppStyles.BUTTON_STYLE);
```

3. **Register in Main Application**:

```java
MyComponent component = new MyComponent();
layout.getChildren().add(component);
```

## Performance Considerations

### World Grid Size

- Default: 100×100 tiles
- Configurable: `World.SIZE`
- Larger worlds: exponentially slower

### Terminal Display

- Max render size: 40×40
- Cell combining for larger worlds
- Color caching: ~10% speedup

### Entity Update Complexity

- O(n) per step where n = entity count
- Sight range limits food search
- Entity list iteration: read-only during update

### JavaFX Rendering

- Canvas clear and redraw each frame
- Entity overlay separate from tile layer
- Chart updates throttled to every step

### Memory Usage

- Tile array: 100×100×Tile = ~80KB
- Entity list: n×Entity = ~100 bytes per entity
- Chart data: Limited to last 1000 points

## Build System

### Compilation

```bash
javac -cp ".;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
      --module-path lib/javafx/lib \
      --add-modules javafx.controls,javafx.fxml,javafx.media \
      -d build src/**/*.java
```

### Running

```bash
# Terminal mode
java -cp "build;lib/gson-2.10.1.jar" Main

# GUI mode
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
     --module-path lib/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     MainAppNew
```

### VS Code Tasks

- **🔨 Clean Build Directory**: Remove \*.class files
- **🏗️ Compile All Java Files**: Full project compilation
- **🚀 Run Evolution Simulation**: GUI mode
- **⚡ Run Terminal Simulation**: Terminal mode
- **🌍 Run World Builder**: World design tool
- **🧪 Run Tests**: Test suite execution

## Testing

### Current Test Suite

- TestSeeds.java: Seed generation and import validation
- Entity behavior tests (planned)
- World generation tests (planned)

### Test Coverage Goals

- Entity: Reproduction, death, movement
- World: Step execution, entity management
- WorldSeed: Generation consistency, compression
- UI: Dialog creation, display rendering

## Future Enhancements

### Planned Features

1. **Seed Import from File**: Load .seed files from disk
2. **Dynamic Graph**: Auto-detect biomes, only show present types
3. **Enhanced CSV Export**: Per-entity trait tracking for analysis
4. **Undo/Redo in World Builder**: Command pattern implementation
5. **Save/Load Simulation State**: Pause and resume simulations
6. **Multi-threaded Simulation**: Parallel entity updates
7. **Neural Network Entities**: AI-controlled behavior
8. **Seasonal Cycles**: Dynamic biome food availability

### Performance Optimization

- Spatial partitioning for entity lookup
- GPU-accelerated rendering
- Incremental chart updates
- Compressed save states

## Related Documentation

- [GETTING_STARTED.md](GETTING_STARTED.md) - Installation and usage
- [UI_GUIDE.md](UI_GUIDE.md) - User interface documentation
- [SEED_SYSTEM.md](SEED_SYSTEM.md) - World seed technical details
- [DATA_ANALYSIS.md](DATA_ANALYSIS.md) - CSV export and analysis
- [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md) - Simulation balance configuration
- [BALANCE_TEST.md](BALANCE_TEST.md) - Balance verification testing
