#!/bin/bash
echo "Compiling Java files..."
mkdir -p build
find src -name "*.java" > sources.txt
javac -cp "lib/javafx/lib/*:lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web -d build @sources.txt
COMPILE_RESULT=$?
rm sources.txt
if [ $COMPILE_RESULT -eq 0 ]; then
    echo "✓ Compilation successful"
fi
exit $COMPILE_RESULT
