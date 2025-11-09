@echo off
echo Starte Zellulären Automaten...

REM Compile first if needed
if not exist build\Hauptprogramm.class (
    call compile.bat
    if %errorlevel% neq 0 exit /b 1
)

REM Run the program
java -cp build Hauptprogramm
