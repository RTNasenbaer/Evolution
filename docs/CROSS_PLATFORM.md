# Cross-Platform Setup Guide

This guide explains how to run the Evolution Simulation on Windows, macOS, and Linux.

## Platform Compatibility

The Evolution Simulation is fully compatible with:

- ✅ **Windows 10/11**
- ✅ **macOS** (Intel and Apple Silicon)
- ✅ **Linux** (Ubuntu, Fedora, Debian, etc.)

## Prerequisites

### All Platforms

1. **Java Development Kit (JDK) 11 or higher**

   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **JavaFX Libraries** (Platform-specific)
   - The included `lib/javafx/` directory contains JavaFX libraries
   - **Important**: You must download the correct version for your OS

### Platform-Specific JavaFX Setup

#### Windows

The current JavaFX libraries are for Windows. No changes needed.

#### macOS

1. Download JavaFX SDK for macOS from: https://gluonhq.com/products/javafx/
2. Replace the contents of `lib/javafx/lib/` with the macOS version
3. For Apple Silicon (M1/M2/M3), use the ARM64 version

#### Linux

1. Download JavaFX SDK for Linux from: https://gluonhq.com/products/javafx/
2. Replace the contents of `lib/javafx/lib/` with the Linux version
3. Install required libraries:

   ```bash
   # Ubuntu/Debian
   sudo apt-get install libgl1-mesa-glx libgtk-3-0

   # Fedora
   sudo dnf install mesa-libGL gtk3
   ```

## Running the Simulation

### Option 1: Universal Shell Script (Recommended for macOS/Linux)

1. Make the script executable:

   ```bash
   chmod +x run.sh
   ```

2. Run the launcher:

   ```bash
   ./run.sh
   ```

3. Choose from the menu:
   - `1` - Terminal Mode (text-based)
   - `2` - GUI Mode (JavaFX)
   - `3` - World Builder
   - `4` - Compile
   - `5` - Clean

**Direct command-line usage:**

```bash
./run.sh terminal    # Run terminal mode
./run.sh gui         # Run GUI mode
./run.sh builder     # Run World Builder
./run.sh compile     # Compile only
./run.sh clean       # Clean build files
```

### Option 2: Batch Files (Windows)

Use the provided `.bat` files:

```cmd
compile.bat          # Compile Java files
clean.bat            # Clean build directory
fullclean.bat        # Full clean (includes CSV files)
```

Then run with:

```cmd
# Terminal mode
java -cp ".;lib/gson-2.10.1.jar" Main

# GUI mode
java -cp ".;lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media MainApp

# World Builder
java -cp ".;lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media WorldBuilder
```

### Option 3: VS Code Tasks (All Platforms)

The included `.vscode/tasks.json` provides tasks that work on all platforms:

1. Press `Ctrl+Shift+B` (Windows/Linux) or `Cmd+Shift+B` (macOS)
2. Select the desired task:
   - 🏗️ Compile All Java Files
   - 🚀 Run Evolution Simulation (GUI)
   - 🌍 Run World Builder
   - ⚡ Run Terminal Simulation
   - 🔨 Clean Build Directory

**Note:** VS Code tasks currently use Windows-style batch files. For macOS/Linux, use the shell scripts instead.

## Compilation

### macOS/Linux

```bash
./compile.sh
```

Or manually:

```bash
find src -name "*.java" > sources.txt
javac -cp "lib/javafx/lib/*:lib/gson-2.10.1.jar" \
      --module-path "lib/javafx/lib" \
      --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
      -d . @sources.txt
rm sources.txt
```

### Windows

```cmd
compile.bat
```

Or manually:

```cmd
dir /s /b src\*.java > sources.txt
javac -cp "lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web -d . @sources.txt
del sources.txt
```

## Classpath Differences

The key difference between platforms is the classpath separator:

| Platform | Separator | Example          |
| -------- | --------- | ---------------- |
| Windows  | `;`       | `.;lib/gson.jar` |
| macOS    | `:`       | `.:lib/gson.jar` |
| Linux    | `:`       | `.:lib/gson.jar` |

The `run.sh` script automatically detects your platform and uses the correct separator.

## Troubleshooting

### JavaFX Not Found

**Error:** `Error: JavaFX runtime components are missing`

**Solution:** Download the correct JavaFX SDK for your platform and replace `lib/javafx/lib/`

### Permission Denied (macOS/Linux)

**Error:** `Permission denied: ./run.sh`

**Solution:** Make scripts executable:

```bash
chmod +x run.sh compile.sh clean.sh fullclean.sh
```

### Java Not Found

**Error:** `java: command not found`

**Solution:** Install JDK and add to PATH:

```bash
# macOS (using Homebrew)
brew install openjdk@17

# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# Fedora
sudo dnf install java-17-openjdk
```

### Graphics Library Missing (Linux)

**Error:** `libGL error` or `GTK not found`

**Solution:** Install graphics libraries:

```bash
# Ubuntu/Debian
sudo apt-get install libgl1-mesa-glx libgtk-3-0 libglib2.0-0

# Fedora
sudo dnf install mesa-libGL gtk3 glib2
```

### Apple Silicon (M1/M2/M3) Performance

For best performance on Apple Silicon Macs, use the ARM64 version of JavaFX and JDK.

## File Compatibility

All simulation data files are cross-platform compatible:

- ✅ `.seed` world files
- ✅ `.csv` export files
- ✅ `.json` configuration files

You can create a world on Windows and load it on macOS/Linux without any modifications.

## Building JAR (Advanced)

To create a standalone JAR file:

### With Dependencies

```bash
# Compile first
./compile.sh  # or compile.bat on Windows

# Create JAR
jar cfm Evolution.jar MANIFEST.MF *.class src/ lib/

# Run JAR
java -jar Evolution.jar
```

**Note:** JAR distribution still requires platform-specific JavaFX libraries to be available.

## Next Steps

Once you have the simulation running on your platform:

1. Read [Getting Started Guide](GETTING_STARTED.md) for basic usage
2. Check [UI Guide](UI_GUIDE.md) for interface instructions
3. Explore [Seed System Guide](SEED_SYSTEM.md) for world creation

## Platform-Specific Notes

### Windows

- Works out of the box with included libraries
- Use PowerShell or Command Prompt
- Git Bash can run `.sh` scripts if preferred

### macOS

- Requires platform-specific JavaFX download
- Terminal app works perfectly
- Apple Silicon users: use ARM64 JavaFX for better performance

### Linux

- Requires platform-specific JavaFX download
- May need additional graphics libraries
- Works on any modern distribution with Java 11+

## Support

If you encounter platform-specific issues not covered here, please:

1. Check Java version: `java -version`
2. Verify JavaFX is correct for your OS
3. Check file permissions on scripts (`chmod +x`)
4. Review error messages carefully
