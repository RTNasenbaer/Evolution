@echo off
echo Kompiliere Zellulären Automaten...

REM Create build directory
if not exist build mkdir build

REM Compile all Java files
javac -d build -encoding UTF-8 src/*.java

if %errorlevel% equ 0 (
    echo ✓ Kompilierung erfolgreich
) else (
    echo ✗ Kompilierung fehlgeschlagen
    exit /b 1
)
