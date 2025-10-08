package world;

/**
 * Helper class to generate a specialized world seed for testing trait adaptation.
 * Creates distinct biome zones to observe how Endurance, Adaptation, Mobility,
 * and Efficiency traits help entities survive in different terrains.
 */
public class TraitTestingSeed {

    /**
     * Generate a trait testing world with distinct biome quadrants.
     * 
     * Layout:
     * - Northwest: FOREST (excellent habitat) - low stress, high food
     * - Northeast: DESERT (harsh) - high temp stress, low food
     * - Southwest: TUNDRA (challenging) - cold stress, moderate food
     * - Southeast: MOUNTAIN (harsh terrain) - elevation stress, difficult movement
     * - Center: GRASS (baseline) - neutral testing ground
     * - Strategic patches: VOLCANIC, SWAMP, OCEAN for extreme testing
     * 
     * This allows observation of:
     * 1. High Adaptation entities thriving in DESERT/VOLCANIC (extreme temps)
     * 2. High Endurance entities navigating MOUNTAIN/SWAMP (difficult terrain)
     * 3. High Mobility entities covering ground efficiently
     * 4. High Efficiency entities surviving in low-food biomes
     * 
     * @return WorldSeed configured for trait testing
     */
    public static WorldSeed generate() {
        int size = World.SIZE;
        Type[][] biomes = new Type[size][size];

        // Initialize all with GRASS (default)
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                biomes[x][y] = Type.GRASS;
            }
        }

        // Define zone boundaries
        int mid = size / 2;
        int quarterSize = size / 4;

        // Northwest quadrant - FOREST (excellent habitat)
        fillZone(biomes, 0, 0, mid - 1, mid - 1, Type.FOREST);

        // Northeast quadrant - DESERT (harsh, hot)
        fillZone(biomes, mid, 0, size - 1, mid - 1, Type.DESERT);

        // Southwest quadrant - TUNDRA (cold, challenging)
        fillZone(biomes, 0, mid, mid - 1, size - 1, Type.TUNDRA);

        // Southeast quadrant - MOUNTAIN (harsh terrain)
        fillZone(biomes, mid, mid, size - 1, size - 1, Type.MOUNTAIN);

        // Center baseline - GRASS (neutral ground for spawning)
        int centerStart = mid - 5;
        int centerEnd = mid + 5;
        fillZone(biomes, centerStart, centerStart, centerEnd, centerEnd, Type.GRASS);

        // Add strategic extreme challenges - corners
        // VOLCANIC patches in each corner (extreme temperature)
        fillZone(biomes, 2, 2, 5, 5, Type.VOLCANIC);
        fillZone(biomes, size - 6, 2, size - 3, 5, Type.VOLCANIC);
        fillZone(biomes, 2, size - 6, 5, size - 3, Type.VOLCANIC);
        fillZone(biomes, size - 6, size - 6, size - 3, size - 3, Type.VOLCANIC);

        // SWAMP strips (difficult movement)
        // Top strip
        fillZone(biomes, quarterSize, 2, quarterSize * 3, 5, Type.SWAMP);
        // Bottom strip
        fillZone(biomes, quarterSize, size - 6, quarterSize * 3, size - 3, Type.SWAMP);
        // Left strip
        fillZone(biomes, 2, quarterSize, 5, quarterSize * 3, Type.SWAMP);
        // Right strip
        fillZone(biomes, size - 6, quarterSize, size - 3, quarterSize * 3, Type.SWAMP);

        // OCEAN barriers (underwater stress)
        // Vertical barriers
        fillZone(biomes, quarterSize - 2, quarterSize, quarterSize + 1, quarterSize * 3, Type.OCEAN);
        fillZone(biomes, quarterSize * 3 - 1, quarterSize, quarterSize * 3 + 2, quarterSize * 3, Type.OCEAN);

        return new WorldSeed(biomes);
    }

    /**
     * Fill a rectangular zone with a specific biome type.
     */
    private static void fillZone(Type[][] biomes, int x1, int y1, int x2, int y2, Type biome) {
        for (int x = x1; x <= x2 && x < biomes.length; x++) {
            for (int y = y1; y <= y2 && y < biomes[0].length; y++) {
                if (x >= 0 && y >= 0) {
                    biomes[x][y] = biome;
                }
            }
        }
    }

    /**
     * Get a descriptive name for this seed type.
     */
    public static String getSeedName() {
        return "trait-testing-zones";
    }

    /**
     * Get a description of what this seed is designed for.
     */
    public static String getDescription() {
        return "Specialized world with distinct biome quadrants for testing trait adaptation. " +
                "Each zone tests different trait advantages:\n" +
                "- FOREST (NW): Abundant food for all strategies\n" +
                "- DESERT (NE): High adaptation needed (extreme heat)\n" +
                "- TUNDRA (SW): High efficiency needed (low food)\n" +
                "- MOUNTAIN (SE): High endurance needed (difficult terrain)\n" +
                "- Central GRASS: Neutral spawning and testing ground\n" +
                "- Strategic VOLCANIC/SWAMP/OCEAN patches for extreme challenges";
    }
}
