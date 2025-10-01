package world;

import entities.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class World {

    private Tile[][] tileGrid; // 2D array for O(1) tile access
    private CopyOnWriteArrayList<Entity> entities;
    private WorldSeed worldSeed;
    public static final int SIZE = 50;
    public static int GLOBAL_STEP_COUNT = 0;

    // Default constructor - creates random procedural world
    public World() {
        this(new WorldSeed(System.currentTimeMillis()));
    }

    // Constructor with legacy numeric seed (backward compatibility)
    public World(long legacySeed) {
        this(WorldSeed.fromLegacySeed(legacySeed));
    }
    
    // Constructor with string seed
    public World(String seedString) {
        this(WorldSeed.fromSeedString(seedString));
    }

    // Primary constructor with WorldSeed
    public World(WorldSeed worldSeed) {
        this.worldSeed = worldSeed;
        this.tileGrid = new Tile[SIZE][SIZE];
        this.entities = new CopyOnWriteArrayList<>();
        generateWorldFromSeed();
    }

    // Legacy constructor for backward compatibility
    public World(CopyOnWriteArrayList<Tile> tiles, CopyOnWriteArrayList<Entity> entities) {
        this.entities = entities;
        this.tileGrid = new Tile[SIZE][SIZE];
        
        // Convert list to 2D array and create seed from existing data
        Type[][] biomeData = new Type[SIZE][SIZE];
        for (Tile tile : tiles) {
            tileGrid[tile.getX()][tile.getY()] = tile;
            biomeData[tile.getX()][tile.getY()] = tile.getType();
        }
        this.worldSeed = new WorldSeed(biomeData);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    private void generateWorldFromSeed() {
        // Generate world directly from seed data - no procedural generation needed
        Type[][] biomeData = worldSeed.getBiomeData();
        
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Type biome = biomeData[x][y];
                tileGrid[x][y] = new Tile(x, y, biome);
            }
        }
        
        System.out.println("World loaded: " + worldSeed.getDescription());
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < SIZE && y >= 0 && y < SIZE) {
            return tileGrid[x][y];
        }
        return null; // Out of bounds
    }

    public Entity getEntity(int x, int y) {
        for (Entity entity : entities) {
            if (entity.getX() == x && entity.getY() == y) {
                return entity;
            }
        }
        return null; // Entity not found
    }

    public int getSize() {
        return SIZE;
    }

    public long getSeed() {
        return worldSeed.getOriginalSeed();
    }
    
    public WorldSeed getWorldSeed() {
        return worldSeed;
    }
    
    public String getSeedString() {
        return worldSeed.toSeedString();
    }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }

    public CopyOnWriteArrayList<Tile> getTiles() {
        CopyOnWriteArrayList<Tile> tiles = new CopyOnWriteArrayList<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                tiles.add(tileGrid[x][y]);
            }
        }
        return tiles;
    }

    public Map<Type, Integer> countEntitiesInAllBiomes() {
        // Initialize counts for all biome types
        Map<Type, Integer> biomeEntityCounts = new HashMap<>();
        for (Type type : Type.values()) {
            biomeEntityCounts.put(type, 0);
        }

        // Count entities in each biome (optimized with direct tile access)
        for (Entity entity : entities) {
            Tile entityTile = getTile(entity.getX(), entity.getY());
            if (entityTile != null) {
                Type type = entityTile.getType();
                biomeEntityCounts.put(type, biomeEntityCounts.get(type) + 1);
            }
        }

        return biomeEntityCounts;
    }

    /**
     * Appends biome entity counts to a CSV file. Writes header if file is new.
     * @param biomeEntityCounts Map of biome type to entity count
     * @param step The simulation step number (can use World.GLOBAL_STEP_COUNT)
     * @param filePath The file path to write to
     */
    // Method to update world seed (for WorldBuilder integration)
    public void updateWorldSeed(WorldSeed newSeed) {
        this.worldSeed = newSeed;
        generateWorldFromSeed();
    }
    
    // Method to get biome at coordinates directly from seed
    public Type getBiomeType(int x, int y) {
        return worldSeed.getBiome(x, y);
    }
    
    public static void exportBiomeCountsToCSV(Map<Type, Integer> biomeEntityCounts, int step, String filePath) {
        boolean writeHeader = false;
        File file = new File(filePath);
        if (!file.exists()) {
            writeHeader = true;
        }
        try (FileWriter writer = new FileWriter(file, true)) {
            if (writeHeader) {
                writer.write("Step");
                for (Type type : Type.values()) {
                    writer.write("," + type.name());
                }
                writer.write("\n");
            }
            writer.write(Integer.toString(step));
            for (Type type : Type.values()) {
                writer.write("," + biomeEntityCounts.getOrDefault(type, 0));
            }
            writer.write("\n");
        } catch (IOException e) {
            System.err.println("Error writing biome counts to CSV: " + e.getMessage());
        }
    }
}
