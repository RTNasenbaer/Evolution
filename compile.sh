#!/bin/bash
echo "Compiling Java files..."
find src -name "*.java" > sources.txt
javac -cp "lib/javafx/lib/*:lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web -d . @sources.txt
COMPILE_RESULT=$?
rm sources.txt
exit $COMPILE_RESULT
