@echo off
echo Compiling Java files...
if not exist build mkdir build
dir /s /b src\*.java > sources.txt
javac -cp "lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web -d build @sources.txt
set COMPILE_RESULT=%ERRORLEVEL%
del sources.txt
if %COMPILE_RESULT% == 0 echo ✓ Compilation successful
exit /b %COMPILE_RESULT%
