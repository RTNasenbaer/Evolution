@echo off
echo Performing full clean...
del /s /q *.class 2>nul
del /q *.jar gui_batch_results_*.csv batch_results_*.csv designed_world_*.dat entity_details_*.csv biome_details_*.csv 2>nul
echo Full clean completed successfully
