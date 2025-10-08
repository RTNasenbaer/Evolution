@echo off
echo Compiling Java files...
dir /s /b src\*.java > sources.txt
javac -cp "lib/javafx/lib/*;lib/gson-2.10.1.jar" --module-path "lib/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.base,javafx.swing,javafx.web -d . @sources.txt
set COMPILE_RESULT=%ERRORLEVEL%
del sources.txt
exit /b %COMPILE_RESULT%
