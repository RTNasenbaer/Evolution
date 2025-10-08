package export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import entities.Entity;
import world.Tile;
import world.Type;
import world.World;

/**
 * Centralized CSV export utility for all simulation data exports.
 * Provides consistent CSV formatting across Terminal, GUI, and Batch modes.
 */
public class CSVExporter {

    /** Directory where all CSV files are saved */
    private static final String DATA_DIRECTORY = "data";

    /**
     * Export simple biome population counts per step.
     * Format: Step,GRASS,MOUNTAIN,FOREST,DESERT,TUNDRA,SWAMP,OCEAN,VOLCANIC
     * 
     * @param counts     Map of biome types to entity counts
     * @param step       Current simulation step
     * @param filename   Output CSV filename
     * @param appendMode If true, append to existing file; if false, create new with header
     */
    public static void exportBiomeCounts(Map<Type, Integer> counts, int step, String filename, boolean appendMode)
            throws IOException {
        String fullPath = getDataPath(filename);
        boolean fileExists = new java.io.File(fullPath).exists();

        try (FileWriter writer = new FileWriter(fullPath, appendMode)) {
            // Write header if new file
            if (!fileExists || !appendMode) {
                writer.write("Step,GRASS,MOUNTAIN,FOREST,DESERT,TUNDRA,SWAMP,OCEAN,VOLCANIC\n");
            }

            // Write data row
            writer.write(String.format("%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                    step,
                    counts.getOrDefault(Type.GRASS, 0),
                    counts.getOrDefault(Type.MOUNTAIN, 0),
                    counts.getOrDefault(Type.FOREST, 0),
                    counts.getOrDefault(Type.DESERT, 0),
                    counts.getOrDefault(Type.TUNDRA, 0),
                    counts.getOrDefault(Type.SWAMP, 0),
                    counts.getOrDefault(Type.OCEAN, 0),
                    counts.getOrDefault(Type.VOLCANIC, 0)));
        }
    }

    /**
     * Export detailed entity-level data for trait analysis.
     * Format: Step,EntityID,X,Y,Energy,Age,Endurance,Adaptation,Mobility,Efficiency,BiomeType,HasFood
     * 
     * @param world    World instance containing entities
     * @param step     Current simulation step
     * @param filename Output CSV filename
     */
    public static void exportEntityDetails(World world, int step, String filename) throws IOException {
        String fullPath = getDataPath(filename);
        boolean fileExists = new java.io.File(fullPath).exists();

        try (FileWriter writer = new FileWriter(fullPath, true)) {
            // Write header if new file
            if (!fileExists) {
                writer.write("Step,EntityID,X,Y,Energy,Age,Endurance,Adaptation,Mobility,Efficiency,");
                writer.write("BiomeType,HasFood\n");
            }

            // Write entity data
            int entityId = 0;
            for (Entity entity : world.getEntities()) {
                int x = entity.getX();
                int y = entity.getY();
                Tile tile = world.getTile(x, y);
                Type biomeType = tile != null ? tile.getType() : Type.GRASS;
                boolean hasFood = tile != null && tile.hasFood();

                writer.write(String.format("%d,E%03d,%d,%d,%.2f,%d,%.2f,%.2f,%.2f,%.2f,%s,%b\n",
                        step,
                        entityId++,
                        x, y,
                        entity.getEnergy(),
                        entity.getAge(),
                        entity.getEndurance(),
                        entity.getAdaptation(),
                        entity.getMobility(),
                        entity.getEfficiency(),
                        biomeType.name(),
                        hasFood));
            }
        }
    }

    /**
     * Export detailed biome-level data including biome properties only (RAW DATA).
     * Format: Step,BiomeType,TotalTiles,TilesWithFood,EntityCount
     * 
     * Note: Individual entity data is in entity_details CSV. All analysis and
     * aggregation should be done in Python scripts.
     * 
     * @param world    World instance containing biomes and entities
     * @param step     Current simulation step
     * @param filename Output CSV filename
     */
    public static void exportBiomeDetails(World world, int step, String filename) throws IOException {
        String fullPath = getDataPath(filename);
        boolean fileExists = new java.io.File(fullPath).exists();

        try (FileWriter writer = new FileWriter(fullPath, true)) {
            // Write header if new file
            if (!fileExists) {
                writer.write("Step,BiomeType,TotalTiles,TilesWithFood,EntityCount\n");
            }

            // Count entities by biome
            Map<Type, Integer> entitiesByBiome = new HashMap<>();
            for (Type type : Type.values()) {
                entitiesByBiome.put(type, 0);
            }

            for (Entity entity : world.getEntities()) {
                Tile tile = world.getTile(entity.getX(), entity.getY());
                if (tile != null) {
                    entitiesByBiome.put(tile.getType(), entitiesByBiome.get(tile.getType()) + 1);
                }
            }

            // Count tiles and food for each biome
            Map<Type, Integer> tileCounts = new HashMap<>();
            Map<Type, Integer> foodCounts = new HashMap<>();
            for (Type type : Type.values()) {
                tileCounts.put(type, 0);
                foodCounts.put(type, 0);
            }

            for (int x = 0; x < World.SIZE; x++) {
                for (int y = 0; y < World.SIZE; y++) {
                    Tile tile = world.getTile(x, y);
                    if (tile != null) {
                        Type type = tile.getType();
                        tileCounts.put(type, tileCounts.get(type) + 1);
                        if (tile.hasFood()) {
                            foodCounts.put(type, foodCounts.get(type) + 1);
                        }
                    }
                }
            }

            // Write biome data (raw counts only)
            for (Type type : Type.values()) {
                writer.write(String.format("%d,%s,%d,%d,%d\n",
                        step,
                        type.name(),
                        tileCounts.get(type),
                        foodCounts.get(type),
                        entitiesByBiome.get(type)));
            }
        }
    }

    /**
     * Get the full path for a file in the data directory.
     * Creates the data directory if it doesn't exist.
     * 
     * @param filename Name of the file
     * @return Full path to the file in the data directory
     */
    private static String getDataPath(String filename) {
        // Ensure data directory exists
        java.io.File dataDir = new java.io.File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Return full path
        return DATA_DIRECTORY + java.io.File.separator + filename;
    }

    /**
     * Create a timestamped filename for exports.
     * 
     * @param prefix    Prefix for filename (e.g., "biome_counts", "entity_details")
     * @param extension File extension (e.g., "csv")
     * @return Timestamped filename
     */
    public static String createTimestampedFilename(String prefix, String extension) {
        long timestamp = System.currentTimeMillis();
        return String.format("%s_%d.%s", prefix, timestamp, extension);
    }

    /**
     * Validate that a file can be written.
     * 
     * @param filename File path to check
     * @return true if file is writable
     */
    public static boolean isWritable(String filename) {
        try {
            java.io.File file = new java.io.File(filename);
            if (file.exists()) {
                return file.canWrite();
            } else {
                // Try to create parent directories
                java.io.File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    return parent.mkdirs();
                }
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
