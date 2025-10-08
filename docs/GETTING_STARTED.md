# Getting Started with Evolution Simulation

## Prerequisites

- **Java Development Kit (JDK)**: Version 11 or higher
- **JavaFX**: Included in `lib/javafx/` directory
- **Gson**: Included in `lib/gson-2.10.1.jar`

## Quick Start

### Using VS Code Tasks

The easiest way to run the simulation is using the pre-configured VS Code tasks:

1. Press `Ctrl+Shift+B` to see available tasks
2. Choose from:
   - **🚀 Run Evolution Simulation** - JavaFX GUI mode
   - **⚡ Run Terminal Simulation** - Terminal mode
   - **🌍 Run World Builder** - World design tool

### Using Launch Configurations

Press `F5` or go to Run and Debug panel to use:

- **⚡ Terminal Simulation** - Interactive command-line mode
- **🚀 Evolution Simulation** - Full GUI with charts
- **🌍 World Builder** - Create custom worlds
- **🧪 Batch Simulation** - Automated testing

## Running from Command Line

### Terminal Mode

```bash
# Compile first
javac -cp ".;lib/gson-2.10.1.jar" -d build src/**/*.java

# Run
java -cp "build;lib/gson-2.10.1.jar" Main
```

**Available Commands:**

```
/run [n]        Run simulation steps
/auto <n>       Auto mode with continuous display
/spawn <x> <y>  Create entity at position
/move <x> <y>   Move entity to position
/display        Show world state
/stats          Show statistics
/seed           Display world seed
/tickspeed <ms> Set simulation speed
/export         Toggle CSV export
/help           Show all commands
/exit           Quit
```

### JavaFX GUI Mode

```bash
# Compile with JavaFX
javac -cp ".;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
      --module-path lib/javafx/lib \
      --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
      -d build src/**/*.java

# Run
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
     --module-path lib/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
     MainAppNew
```

### World Builder

```bash
# Use same compilation as GUI mode, then:
java -cp "build;lib/javafx/lib/*;lib/gson-2.10.1.jar" \
     --module-path lib/javafx/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
     WorldBuilderNew
```

## First Steps

### 1. Start the Simulation

Choose your preferred interface (Terminal or GUI) and launch it.

### 2. Enter a World Seed (Optional)

- Leave empty for a random world
- Enter a seed string from a previous session to recreate the same world
- Enter a file path to import a seed from file (e.g., `examples/test_world.seed`)
- Use a numeric seed for procedural generation

**Importing Seed from File:**

**Terminal Mode:**

```
> /import
Enter seed file path (e.g., examples/world.seed): examples/test_world.seed
✓ World imported from file: examples/test_world.seed
✓ Seed: Procedural (seed: 42)
```

**GUI Mode:**

- At startup, enter file path instead of seed string
- Example: `examples/test_world.seed`
- Dialog will detect file path automatically (looks for `.seed` extension or path separators)
- Success/error dialog will appear

### 3. Run Simulation Steps

**Terminal Mode:**

```
> /run 10        # Run 10 steps
> /auto 100      # Run 100 steps with continuous display
```

**GUI Mode:**

- Click "Run Step" for single step
- Enter number and click "Run N Steps" for multiple steps

### 4. Monitor Population

- Watch the world display for entity movement (● symbols or colored tiles)
- Check population statistics
- View population charts (GUI mode)

### 5. Export Data (Optional)

**Terminal Mode:**

```
> /export        # Toggle CSV export
> /run 1000      # Run simulation
> /export        # Stop and save
```

**GUI Mode:**

- Enter batch parameters
- Click "Run Batch" to generate comprehensive CSV data

## World Seeds

### Using Seeds

Seeds allow you to recreate exact worlds:

1. **Get Current Seed:**

   - Terminal: `/seed`
   - GUI: Check seed display in control panel

2. **Use a Seed:**

   - Enter seed string at startup
   - Or use "Import Seed" in World Builder

3. **Share Seeds:**
   - Copy seed string from export dialog
   - Seeds work across all applications (Main, MainAppNew, WorldBuilder)

### Seed Formats

- **Procedural**: Numeric seed (e.g., `12345`)
- **String Hash**: Text seed (e.g., `MyWorld`)
- **Designed**: EVO-prefixed compressed seed from World Builder

## Tips for Beginners

1. **Start with GUI**: Easier to visualize what's happening
2. **Small Steps First**: Run 10-20 steps to understand entity behavior
3. **Watch Energy Levels**: Entities die when energy drops below 1.0
4. **Experiment with Biomes**: Different biomes have different food availability
5. **Use Auto Mode**: Great for observing long-term population dynamics
6. **Save Interesting Seeds**: Keep seeds of interesting worlds for later study

## Troubleshooting

### "Module not found" Error

- Ensure JavaFX libraries are in `lib/javafx/lib/`
- Check `--module-path` points to correct directory

### Terminal Display Issues

- Ensure terminal supports ANSI colors
- Try Windows Terminal or a modern terminal emulator

### No Entities Visible

- Entities may have died - spawn new ones
- Check if world display is properly rendering

### Simulation Too Fast/Slow

- Adjust tickspeed: `/tickspeed 500` (terminal) or use control panel (GUI)

### Entities Dying Too Quickly

- The simulation has been carefully balanced for trait analysis
- See [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md) for technical details
- Run verification tests from [BALANCE_TEST.md](BALANCE_TEST.md)
- Default settings should allow entities to survive 100+ steps

## Next Steps

- Read [UI_GUIDE.md](UI_GUIDE.md) for detailed interface documentation
- Explore [ARCHITECTURE.md](ARCHITECTURE.md) to understand the system
- Check [DATA_ANALYSIS.md](DATA_ANALYSIS.md) for research and analysis techniques
- Review [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md) to understand simulation parameters
