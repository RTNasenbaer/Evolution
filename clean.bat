@echo off
echo Cleaning build directory...
if exist build rmdir /s /q build 2>nul
if exist build echo ✗ Failed to clean build directory
if not exist build echo ✓ Clean completed successfully
