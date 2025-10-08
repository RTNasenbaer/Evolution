@echo off
echo Performing full clean...
if exist build rmdir /s /q build 2>nul
del /q *.jar 2>nul
echo Moving stray CSV files to data folder...
if not exist data mkdir data
for %%f in (*.csv) do move "%%f" "data\" 2>nul
echo ✓ Full clean completed successfully
