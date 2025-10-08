#!/bin/bash

# Evolution Simulation Universal Launcher
# Compatible with Windows (Git Bash), macOS, and Linux

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Detect platform
detect_platform() {
    case "$(uname -s)" in
        Linux*)     PLATFORM="Linux";;
        Darwin*)    PLATFORM="macOS";;
        CYGWIN*)    PLATFORM="Windows";;
        MINGW*)     PLATFORM="Windows";;
        MSYS*)      PLATFORM="Windows";;
        *)          PLATFORM="Unknown";;
    esac
}

# Set classpath separator based on platform
set_classpath() {
    if [[ "$PLATFORM" == "Windows" ]]; then
        CP_SEP=";"
    else
        CP_SEP=":"
    fi
}

# Display menu
show_menu() {
    echo -e "${BLUE}╔════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║     Evolution Simulation Launcher          ║${NC}"
    echo -e "${BLUE}║     Platform: ${GREEN}$PLATFORM${BLUE}                       ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "${YELLOW}Select mode:${NC}"
    echo "  1) Terminal Mode (Text-based)"
    echo "  2) GUI Mode (JavaFX)"
    echo "  3) World Builder"
    echo "  4) Compile"
    echo "  5) Clean"
    echo "  6) Exit"
    echo ""
}

# Compile Java files
compile() {
    echo -e "${YELLOW}Compiling Java files...${NC}"
    find src -name "*.java" > sources.txt
    javac -cp "lib/javafx/lib/*${CP_SEP}lib/gson-2.10.1.jar" \
          --module-path "lib/javafx/lib" \
          --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
          -d . @sources.txt
    
    COMPILE_RESULT=$?
    rm sources.txt
    
    if [ $COMPILE_RESULT -eq 0 ]; then
        echo -e "${GREEN}✓ Compilation successful!${NC}"
        return 0
    else
        echo -e "${RED}✗ Compilation failed!${NC}"
        return 1
    fi
}

# Clean build files
clean() {
    echo -e "${YELLOW}Cleaning build directory...${NC}"
    find . -maxdepth 1 -name "*.class" -type f -delete
    find entities -name "*.class" -type f -delete 2>/dev/null
    find export -name "*.class" -type f -delete 2>/dev/null
    find ui -name "*.class" -type f -delete 2>/dev/null
    find world -name "*.class" -type f -delete 2>/dev/null
    echo -e "${GREEN}✓ Clean complete!${NC}"
}

# Run terminal mode
run_terminal() {
    echo -e "${GREEN}Starting Terminal Mode...${NC}"
    java -cp ".${CP_SEP}lib/gson-2.10.1.jar" Main
}

# Run GUI mode
run_gui() {
    echo -e "${GREEN}Starting GUI Mode...${NC}"
    java -cp ".${CP_SEP}lib/javafx/lib/*${CP_SEP}lib/gson-2.10.1.jar" \
         --module-path "lib/javafx/lib" \
         --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
         MainApp
}

# Run World Builder
run_world_builder() {
    echo -e "${GREEN}Starting World Builder...${NC}"
    java -cp ".${CP_SEP}lib/javafx/lib/*${CP_SEP}lib/gson-2.10.1.jar" \
         --module-path "lib/javafx/lib" \
         --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web \
         WorldBuilder
}

# Check if compiled
check_compiled() {
    if [ ! -f "Main.class" ] && [ ! -f "MainApp.class" ]; then
        echo -e "${YELLOW}No compiled classes found. Compiling first...${NC}"
        compile || exit 1
    fi
}

# Main script
detect_platform
set_classpath

# If command line argument provided, run directly
if [ $# -gt 0 ]; then
    case "$1" in
        terminal|1)
            check_compiled
            run_terminal
            ;;
        gui|2)
            check_compiled
            run_gui
            ;;
        builder|3)
            check_compiled
            run_world_builder
            ;;
        compile|4)
            compile
            ;;
        clean|5)
            clean
            ;;
        *)
            echo -e "${RED}Invalid option: $1${NC}"
            echo "Usage: ./run.sh [terminal|gui|builder|compile|clean]"
            exit 1
            ;;
    esac
    exit 0
fi

# Interactive menu mode
while true; do
    show_menu
    read -p "Enter choice [1-6]: " choice
    echo ""
    
    case $choice in
        1)
            check_compiled
            run_terminal
            break
            ;;
        2)
            check_compiled
            run_gui
            break
            ;;
        3)
            check_compiled
            run_world_builder
            break
            ;;
        4)
            compile
            ;;
        5)
            clean
            ;;
        6)
            echo -e "${GREEN}Goodbye!${NC}"
            exit 0
            ;;
        *)
            echo -e "${RED}Invalid option. Please try again.${NC}"
            echo ""
            ;;
    esac
done
