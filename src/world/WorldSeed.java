package world;

import java.io.*;
import java.util.*;

/**
 * WorldSeed contains all the data needed to recreate a world exactly.
 * It can be serialized to/from a long value for easy sharing and storage.
 * The seed format supports both procedurally generated and manually designed worlds.
 */
public class WorldSeed implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Seed format identifiers
    public static final byte PROCEDURAL_SEED = 0x01;
    public static final byte DESIGNED_SEED = 0x02;
    
    private byte seedType;
    private long originalSeed; // For procedural seeds, this is the generation seed
    private Type[][] biomeData; // The actual biome layout
    private Map<String, Object> metadata; // Additional world properties
    
    // Constructor for procedural seeds
    public WorldSeed(long proceduralSeed) {
        this.seedType = PROCEDURAL_SEED;
        this.originalSeed = proceduralSeed;
        this.metadata = new HashMap<>();
        generateProceduralBiomes();
    }
    
    // Constructor for designed worlds
    public WorldSeed(Type[][] biomeData) {
        this.seedType = DESIGNED_SEED;
        this.originalSeed = System.currentTimeMillis();
        this.biomeData = deepCopyBiomeData(biomeData);
        this.metadata = new HashMap<>();
        this.metadata.put("created", System.currentTimeMillis());
        this.metadata.put("designer", "WorldBuilder");
    }
    
    // Constructor for loading from data
    private WorldSeed(byte seedType, long originalSeed, Type[][] biomeData, Map<String, Object> metadata) {
        this.seedType = seedType;
        this.originalSeed = originalSeed;
        this.biomeData = biomeData;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    /**
     * Generate procedural biomes using the original algorithm
     */
    private void generateProceduralBiomes() {
        Random random = new Random(originalSeed);
        biomeData = new Type[World.SIZE][World.SIZE];
        int biomeSeeds = 3; // Number of seeds per biome

        // Seed biomes
        for (Type type : Type.values()) {
            for (int i = 0; i < biomeSeeds; i++) {
                int sx = random.nextInt(World.SIZE);
                int sy = random.nextInt(World.SIZE);
                biomeData[sx][sy] = type;
            }
        }

        // Expand biomes using BFS
        Queue<int[]> queue = new LinkedList<>();
        for (int x = 0; x < World.SIZE; x++) {
            for (int y = 0; y < World.SIZE; y++) {
                if (biomeData[x][y] != null) {
                    queue.add(new int[] { x, y });
                }
            }
        }

        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0], y = pos[1];
            Type type = biomeData[x][y];
            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < World.SIZE && ny >= 0 && ny < World.SIZE && biomeData[nx][ny] == null) {
                    if (random.nextDouble() < 0.6) { // Controls biome "spread"
                        biomeData[nx][ny] = type;
                        queue.add(new int[] { nx, ny });
                    }
                }
            }
        }

        // Fill any remaining tiles with GRASS
        for (int x = 0; x < World.SIZE; x++) {
            for (int y = 0; y < World.SIZE; y++) {
                if (biomeData[x][y] == null) {
                    biomeData[x][y] = Type.GRASS;
                }
            }
        }
        
        // Store generation parameters in metadata
        metadata.put("biomeSeeds", biomeSeeds);
        metadata.put("spreadFactor", 0.6);
        metadata.put("generated", System.currentTimeMillis());
    }
    
    /**
     * Serialize the world seed to an ultra-compact string format
     */
    public String toSeedString() {
        // Always use the same unified format with compact Base36 encoding
        String compressed = compressBiomeData();
        // If compression is very efficient (mostly same biome), include it
        // Otherwise just store the seed for procedural recreation
        if (compressed.length() < 100 || seedType == DESIGNED_SEED) {
            return Long.toString(originalSeed, 36) + "-" + compressed;
        } else {
            // For complex procedural worlds, just store the compact seed
            return Long.toString(originalSeed, 36);
        }
    }
    
    /**
     * Deserialize a world seed from compact string format
     */
    public static WorldSeed fromSeedString(String seedString) {
        try {
            if (seedString.contains("-")) {
                // Format: seed-biomedata
                String[] parts = seedString.split("-", 2);
                long originalSeed = Long.parseLong(parts[0], 36);
                Type[][] biomeData = decompressBiomeData(parts[1]);
                return new WorldSeed(DESIGNED_SEED, originalSeed, biomeData, new HashMap<>());
            } else {
                // Simple compact seed
                long seed = Long.parseLong(seedString, 36);
                return new WorldSeed(seed);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid seed format: " + seedString);
        }
    }
    
    /**
     * Create a WorldSeed from a legacy numeric seed (backward compatibility)
     */
    public static WorldSeed fromLegacySeed(long legacySeed) {
        return new WorldSeed(legacySeed);
    }
    
    /**
     * Get biome at specific coordinates
     */
    public Type getBiome(int x, int y) {
        if (x >= 0 && x < World.SIZE && y >= 0 && y < World.SIZE) {
            return biomeData[x][y];
        }
        return Type.GRASS; // Default fallback
    }
    
    /**
     * Set biome at specific coordinates (for WorldBuilder)
     */
    public void setBiome(int x, int y, Type biome) {
        if (x >= 0 && x < World.SIZE && y >= 0 && y < World.SIZE) {
            biomeData[x][y] = biome;
            // Mark as designed if modified
            if (seedType == PROCEDURAL_SEED) {
                seedType = DESIGNED_SEED;
                metadata.put("modified", System.currentTimeMillis());
            }
        }
    }
    
    /**
     * Get a copy of the biome data array
     */
    public Type[][] getBiomeData() {
        return deepCopyBiomeData(biomeData);
    }
    
    /**
     * Get seed type
     */
    public byte getSeedType() {
        return seedType;
    }
    
    /**
     * Get original seed value
     */
    public long getOriginalSeed() {
        return originalSeed;
    }
    
    /**
     * Get metadata
     */
    public Map<String, Object> getMetadata() {
        return new HashMap<>(metadata);
    }
    
    /**
     * Check if this is a procedural seed
     */
    public boolean isProcedural() {
        return seedType == PROCEDURAL_SEED;
    }
    
    /**
     * Check if this is a designed seed
     */
    public boolean isDesigned() {
        return seedType == DESIGNED_SEED;
    }
    

    
    /**
     * Compress biome data using advanced run-length encoding
     */
    private String compressBiomeData() {
        StringBuilder compressed = new StringBuilder();
        
        // Read row by row for better compression
        for (int y = 0; y < World.SIZE; y++) {
            int x = 0;
            while (x < World.SIZE) {
                Type currentBiome = biomeData[x][y];
                int count = 1;
                
                // Count consecutive tiles of same type in this row
                while (x + count < World.SIZE && biomeData[x + count][y] == currentBiome) {
                    count++;
                }
                
                // Use very compact encoding: biome_ordinal + count
                compressed.append(Integer.toString(currentBiome.ordinal(), 36));
                if (count > 1) {
                    compressed.append(Integer.toString(count, 36));
                }
                compressed.append(":");
                
                x += count;
            }
        }
        
        // Remove trailing colon
        if (compressed.length() > 0 && compressed.charAt(compressed.length() - 1) == ':') {
            compressed.setLength(compressed.length() - 1);
        }
        
        return compressed.toString();
    }
    
    /**
     * Decompress biome data from run-length encoded format
     */
    private static Type[][] decompressBiomeData(String compressed) {
        Type[][] biomeData = new Type[World.SIZE][World.SIZE];
        Type[] types = Type.values();
        String[] segments = compressed.split(":");
        
        int x = 0, y = 0;
        for (String segment : segments) {
            if (segment.isEmpty()) continue;
            
            // Parse biome and count
            int biomeOrdinal = Integer.parseInt(segment.substring(0, 1), 36);
            int count = segment.length() > 1 ? 
                Integer.parseInt(segment.substring(1), 36) : 1;
            
            Type biome = types[biomeOrdinal];
            
            // Fill tiles
            for (int i = 0; i < count; i++) {
                if (x >= World.SIZE) {
                    x = 0;
                    y++;
                }
                if (y < World.SIZE) {
                    biomeData[x][y] = biome;
                    x++;
                }
            }
        }
        
        // Fill any remaining with GRASS
        for (int i = 0; i < World.SIZE; i++) {
            for (int j = 0; j < World.SIZE; j++) {
                if (biomeData[i][j] == null) {
                    biomeData[i][j] = Type.GRASS;
                }
            }
        }
        
        return biomeData;
    }
    
    // Utility methods
    private Type[][] deepCopyBiomeData(Type[][] original) {
        Type[][] copy = new Type[World.SIZE][World.SIZE];
        for (int x = 0; x < World.SIZE; x++) {
            System.arraycopy(original[x], 0, copy[x], 0, World.SIZE);
        }
        return copy;
    }
    

    
    /**
     * Generate a human-readable description of the seed
     */
    public String getDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("World Seed - ");
        desc.append(isProcedural() ? "Procedural" : "Designed");
        desc.append(" (").append(originalSeed).append(")");
        
        if (metadata.containsKey("created")) {
            desc.append(" - Created: ").append(new Date((Long) metadata.get("created")));
        }
        
        // Count biomes
        Map<Type, Integer> biomeCounts = new HashMap<>();
        for (Type type : Type.values()) {
            biomeCounts.put(type, 0);
        }
        
        for (int x = 0; x < World.SIZE; x++) {
            for (int y = 0; y < World.SIZE; y++) {
                Type biome = biomeData[x][y];
                biomeCounts.put(biome, biomeCounts.get(biome) + 1);
            }
        }
        
        desc.append(" - Biomes: ");
        for (Map.Entry<Type, Integer> entry : biomeCounts.entrySet()) {
            if (entry.getValue() > 0) {
                desc.append(entry.getKey()).append("(").append(entry.getValue()).append(") ");
            }
        }
        
        return desc.toString();
    }
    
    @Override
    public String toString() {
        return getDescription();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof WorldSeed)) return false;
        
        WorldSeed other = (WorldSeed) obj;
        return seedType == other.seedType && 
               originalSeed == other.originalSeed &&
               Arrays.deepEquals(biomeData, other.biomeData);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(seedType, originalSeed, Arrays.deepHashCode(biomeData));
    }
}