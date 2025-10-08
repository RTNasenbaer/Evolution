# User Interface Guide

## Overview

Evolution Simulation provides three distinct interfaces:

1. **Terminal Mode** - Text-based interactive command line
2. **JavaFX GUI** - Graphical interface with real-time charts
3. **World Builder** - Visual world design tool

## Terminal Mode (Main.java)

### Starting Terminal Mode

```bash
java -cp "build;lib/gson-2.10.1.jar" Main
```

### Display Features

- **Color-Coded Biomes**: Each biome type shown in distinct color
- **Entity Indicators**: ● symbol for entities, ◆ for food
- **Bordered Layout**: Clean ASCII box drawing characters
- **Dynamic Legend**: Shows biome colors and counts

### Visual Example

```
╔════════════════════════╗
║▓▓▓▓░░░░▒▒▒▒████▓▓▓▓░░║
║▓▓●▓░░●░▒▒◆▒████▓▓▓▓░░║
║▓▓▓▓░░░░▒▒▒▒████●▓▓▓░░║
╚════════════════════════╝

Legend:
░░ GRASSLAND (35)
▓▓ FOREST (28)
▒▒ WATER (15)
██ DESERT (22)
```

### Command Reference

#### Basic Commands

| Command     | Description                             | Example     |
| ----------- | --------------------------------------- | ----------- |
| `/run [n]`  | Run simulation for n steps (default: 1) | `/run 100`  |
| `/auto <n>` | Continuous mode with refresh            | `/auto 500` |
| `/display`  | Refresh world display                   | `/display`  |
| `/stats`    | Show population statistics              | `/stats`    |
| `/help`     | List all commands                       | `/help`     |
| `/exit`     | Quit simulation                         | `/exit`     |

#### Entity Management

| Command          | Description               | Example        |
| ---------------- | ------------------------- | -------------- |
| `/spawn <x> <y>` | Create entity at position | `/spawn 10 15` |
| `/move <x> <y>`  | Move nearest entity       | `/move 20 20`  |

#### Data Export

| Command           | Description                    | Example           |
| ----------------- | ------------------------------ | ----------------- |
| `/export`         | Toggle CSV export (population) | `/export`         |
| `/export details` | Toggle entity-level CSV export | `/export details` |
| `/export biomes`  | Toggle biome-level CSV export  | `/export biomes`  |
| `/import`         | Import world seed from file    | `/import`         |
| `/seed`           | Display current world seed     | `/seed`           |

#### Batch Simulation

| Command          | Description                    | Example          |
| ---------------- | ------------------------------ | ---------------- |
| `/batch <r> <s>` | Run r simulations with s steps | `/batch 10 1000` |

#### Configuration

| Command           | Description         | Example          |
| ----------------- | ------------------- | ---------------- |
| `/tickspeed <ms>` | Set auto mode delay | `/tickspeed 100` |

### Auto Mode

Auto mode runs the simulation continuously with periodic display updates:

```
> /auto 1000
Running 1000 steps in auto mode...
Press Ctrl+C to stop

Step 100/1000 - Population: 45
[World Display]

Step 200/1000 - Population: 52
[World Display]
...
```

### CSV Export

When export is enabled:

1. Toggle on: `/export` → "CSV export enabled"
2. Run simulation: `/run 1000`
3. Toggle off: `/export` → "Data exported to batch_results_TIMESTAMP.csv"

## JavaFX GUI (MainAppNew.java)

### Starting GUI Mode

```bash
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
     --module-path lib/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
     MainAppNew
```

**Seed Import**: On startup, you can:

- Enter a seed string directly
- Leave empty to browse for a `.seed` file using file dialog
- The file chooser allows selecting seed files created in WorldBuilder

### Layout Overview

```
┌─────────────────────────────────────────────────────────┐
│                     Evolution Simulation                 │
├────────────────────────────┬───────────────────────────┤
│                            │                           │
│                            │   CONTROLS                │
│                            │   ┌──────────────────┐   │
│                            │   │ Run Step         │   │
│        WORLD DISPLAY       │   │ Run N Steps      │   │
│                            │   │ Speed: [====]    │   │
│      (Zoom/Pan enabled)    │   │ Spawn Entity     │   │
│                            │   └──────────────────┘   │
│                            │                           │
│                            │   POPULATION GRAPH        │
│                            │   ┌──────────────────┐   │
│                            │   │     Chart        │   │
│                            │   └──────────────────┘   │
│                            │                           │
│                            │   CONFIG PANEL            │
│                            │   Step: 142               │
│                            │   Population: 37          │
└────────────────────────────┴───────────────────────────┘
```

### World Display

**Features**:

- **Zoom**: Scroll wheel or pinch gesture
- **Pan**: Click and drag
- **Entity Inspection**: Click on entity for details
- **Real-Time Updates**: Display refreshes every simulation step

**Color Coding**:

- **Green**: Grassland
- **Dark Green**: Forest
- **Blue**: Water
- **Yellow**: Desert
- **Gray**: Mountain
- **White**: Snow

### Control Panel

#### Run Controls

- **Run Step**: Execute single simulation step
- **Run N Steps**: Enter number, click to run multiple steps
- **Speed Slider**: Adjust simulation speed (delay between steps)

#### Entity Management

- **Spawn Entity**: Click, then click world location to place entity
- **Inspect Entity**: Click entity in world display for details dialog

#### Batch Simulation

```
┌──────────────────────────────┐
│ Runs: [10]                   │
│ Steps per run: [1000]        │
│ [Run Batch]                  │
└──────────────────────────────┘
```

Generates CSV with population data across multiple runs.

### Population Graph

**Features**:

- **Real-Time Updates**: Chart updates every step
- **Dynamic Biome Tracking**: Chart series are created dynamically as biomes become populated
  - Empty biomes are not shown on the graph
  - When entities first appear in a biome, its series is automatically added
  - Only active biomes appear in the legend
  - Works with all 6 biome types (GRASSLAND, FOREST, WATER, DESERT, MOUNTAIN, SNOW)
- **Legend**: Click to toggle biome visibility
- **Zoom**: Drag to select region
- **Reset**: Right-click to reset view
- **Performance**: Symbols disabled for smooth rendering with many data points

**Y-Axis**: Entity count
**X-Axis**: Simulation steps (last 20 points displayed for detailed view)

**Performance Optimizations**:

- Animation disabled for faster updates
- Only last 20 data points shown (instead of 100) for more detailed timeline
- Symbols disabled for smooth rendering

**Example**: If your world only has GRASSLAND and FOREST entities, only those two lines will appear on the graph, leaving more visual space for relevant data.

### Statistics Dialog

Click "Statistics" to view:

- Total population
- Entities per biome
- Average energy
- Average age
- Entity list with details

### Entity Inspector

Click an entity to see:

- **Position**: (x, y) coordinates
- **Energy**: Current energy level
- **Age**: Steps survived
- **Speed**: Movement rate
- **Mass**: Entity mass
- **Traits**:
  - Energy Efficiency
  - Reproduction Threshold
  - Sight Range
  - Metabolism Rate
  - Max Lifespan

### Dialogs

All dialogs use consistent styling (via DialogUtils):

- **Information**: Blue header
- **Success**: Green header with ✓
- **Error**: Red header with ✗
- **Input**: Text field with validation
- **Copyable**: Long text with "Copy to Clipboard" button

## World Builder (WorldBuilderNew.java)

### Starting World Builder

```bash
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
     --module-path lib/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
     WorldBuilderNew
```

### Layout Overview

```
┌─────────────────────────────────────────────────────────┐
│                      World Builder                       │
├────────────────────────────┬───────────────────────────┤
│                            │                           │
│                            │   BRUSH TOOLS             │
│                            │   ○ Grassland             │
│                            │   ○ Forest                │
│        CANVAS              │   ○ Water                 │
│                            │   ○ Desert                │
│    (Click to paint)        │   ○ Mountain              │
│                            │   ○ Snow                  │
│                            │                           │
│                            │   WORLD MANAGEMENT        │
│                            │   [Import Seed]           │
│                            │   [Export Seed]           │
│                            │   [Clear World]           │
│                            │   [Randomize]             │
└────────────────────────────┴───────────────────────────┘
```

### Brush Tools

1. **Select Biome Type**: Click radio button for desired biome
2. **Paint**: Click or drag on canvas to place biome
3. **Preview**: Hover to see where brush will paint

**Biome Types**:

- Grassland (Green)
- Forest (Dark Green)
- Water (Blue)
- Desert (Yellow)
- Mountain (Gray)
- Snow (White)

### World Management

#### Import Seed

1. Click "Import Seed"
2. Enter seed string (procedural or designed)
3. World loads into canvas
4. Modify as desired

#### Export Seed

1. Click "Export Seed"
2. Dialog shows copyable seed string
3. Click "Copy to Clipboard"
4. Share or save seed

**Seed Format**: `EVO_<compressed_data>`

#### Clear World

- Resets all tiles to empty
- Useful for starting fresh design

#### Randomize

- Generates random biome layout
- Uses procedural algorithm
- Click multiple times for different patterns

### Design Tips

1. **Start with Base Layer**: Fill large areas first
2. **Add Details**: Use smaller strokes for borders
3. **Natural Patterns**: Avoid checkerboard, use clusters
4. **Test in Simulation**: Export and load in MainAppNew
5. **Save Interesting Designs**: Keep library of seeds

### Workflow Example

```
1. Click "Randomize" for starting point
2. Select "Water" brush
3. Paint a river across map
4. Select "Desert" brush
5. Paint arid region near river
6. Click "Export Seed"
7. Copy seed string
8. Open MainAppNew
9. Enter seed at startup
10. Test entity behavior in designed world
```

## Keyboard Shortcuts

### Terminal Mode

- **Ctrl+C**: Stop auto mode
- **Ctrl+D**: Exit (Unix/Linux)
- **Up/Down**: Command history

### GUI Mode

- **Scroll Wheel**: Zoom in/out
- **Click+Drag**: Pan world view
- **Ctrl+Plus**: Zoom in
- **Ctrl+Minus**: Zoom out

### World Builder

- **Click**: Paint single tile
- **Click+Drag**: Paint multiple tiles
- **Ctrl+Z**: Undo (not yet implemented)

## Responsive Design

### Window Resizing

All JavaFX interfaces automatically adapt to window size:

- **Small Window**: Compact layout, smaller controls
- **Medium Window**: Standard layout
- **Large Window**: Expanded layout with more detail

### Minimum Recommended Size

- **Width**: 1024 pixels
- **Height**: 768 pixels

### Mobile Considerations

JavaFX interfaces are not optimized for mobile/touch:

- Use terminal mode on mobile devices
- Or use desktop with mouse/keyboard

## Accessibility

### Color Blindness Support

**Current**: Color-coded biomes
**Planned**: Pattern overlays for biome types

### Screen Readers

Not currently supported. Planned for future releases.

### High Contrast Mode

Use system high contrast mode. Application will inherit settings.

## Performance Tips

### Terminal Mode

- Use `/auto` for continuous simulation (faster than manual `/run`)
- Disable display updates by not running `/display`
- Smaller worlds render faster

### GUI Mode

- Close statistics dialogs when not needed
- Reduce window size for faster rendering
- Use batch mode for data collection (minimal UI updates)

### World Builder

- Smaller canvas sizes for complex designs
- Export early and often (no auto-save yet)

## Troubleshooting

### GUI Doesn't Start

- Check JavaFX libraries present
- Verify `--module-path` and `--add-modules` arguments
- Try updating JavaFX to latest version

### Terminal Colors Not Showing

- Use modern terminal emulator (Windows Terminal recommended)
- Enable ANSI color support
- Check terminal settings for 256-color mode

### World Display Blank

- Entities may have died - spawn new ones
- Check zoom level (may be zoomed too far in/out)
- Verify world generated successfully at startup

### Slow Performance

- Reduce world size (50x50 instead of 100x100)
- Decrease entity population
- Lower simulation speed
- Close other applications

## Related Documentation

- [GETTING_STARTED.md](GETTING_STARTED.md) - Installation and first steps
- [ARCHITECTURE.md](ARCHITECTURE.md) - Technical implementation details
- [SEED_SYSTEM.md](SEED_SYSTEM.md) - World seed system
- [DATA_ANALYSIS.md](DATA_ANALYSIS.md) - CSV export and data analysis
- [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md) - Simulation balance configuration
- [BALANCE_TEST.md](BALANCE_TEST.md) - Balance verification testing
