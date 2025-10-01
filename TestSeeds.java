import world.*;import world.*;

import java.util.HashMap;

/**

/** * Quick test to demonstrate the new compact seed system

 * Quick test to demonstrate the new compact seed system */

 */public class TestSeeds {

public class TestSeeds {    public static void main(String[] args) {

    public static void main(String[] args) {        System.out.println("=== SEED SIZE COMPARISON ===\n");

        System.out.println("=== SEED SIZE COMPARISON ===\n");        

                // Test 1: Procedural seed

        // Test 1: Procedural seed        System.out.println("1. PROCEDURAL SEED:");

        System.out.println("1. PROCEDURAL SEED:");        long proceduralSeed = 123456789L;

        long proceduralSeed = 123456789L;        WorldSeed procSeed = new WorldSeed(proceduralSeed);

        WorldSeed procSeed = new WorldSeed(proceduralSeed);        String procSeedString = procSeed.toSeedString();

        String procSeedString = procSeed.toSeedString();        System.out.println("   Original seed: " + proceduralSeed);

        System.out.println("   Original seed: " + proceduralSeed);        System.out.println("   Compact seed: " + procSeedString);

        System.out.println("   Compact seed: " + procSeedString);        System.out.println("   Length: " + procSeedString.length() + " characters");

        System.out.println("   Length: " + procSeedString.length() + " characters");        

                // Test recreating the world

        // Test recreating the world        WorldSeed recreatedProc = WorldSeed.fromSeedString(procSeedString);

        WorldSeed recreatedProc = WorldSeed.fromSeedString(procSeedString);        System.out.println("   Recreation successful: " + (recreatedProc.getOriginalSeed() == proceduralSeed));

        System.out.println("   Recreation successful: " + (recreatedProc.getOriginalSeed() == proceduralSeed));        System.out.println();

        System.out.println();        

                // Test 2: Designed seed (create a simple pattern)

        // Test 2: Designed seed (create a simple pattern)        System.out.println("2. DESIGNED SEED:");

        System.out.println("2. DESIGNED SEED:");        Type[][] biomeData = new Type[World.SIZE][World.SIZE];

        Type[][] biomeData = new Type[World.SIZE][World.SIZE];        

                // Create a simple pattern: mostly grass with some water and desert

        // Create a simple pattern: mostly grass with some water and desert        for (int x = 0; x < World.SIZE; x++) {

        for (int x = 0; x < World.SIZE; x++) {            for (int y = 0; y < World.SIZE; y++) {

            for (int y = 0; y < World.SIZE; y++) {                if (x < 10 && y < 10) {

                if (x < 10 && y < 10) {                    biomeData[x][y] = Type.WATER;

                    biomeData[x][y] = Type.WATER;                } else if (x > 40 && y > 40) {

                } else if (x > 40 && y > 40) {                    biomeData[x][y] = Type.DESERT;

                    biomeData[x][y] = Type.DESERT;                } else {

                } else {                    biomeData[x][y] = Type.GRASS;

                    biomeData[x][y] = Type.GRASS;                }

                }            }

            }        }\n        \n        WorldSeed designedSeed = new WorldSeed(WorldSeed.DESIGNED_SEED, 987654321L, biomeData, new java.util.HashMap<>());\n        String designedSeedString = designedSeed.toSeedString();\n        System.out.println("   Original seed: 987654321");\n        System.out.println("   Compact seed: " + designedSeedString);\n        System.out.println("   Length: " + designedSeedString.length() + " characters");\n        \n        // Test recreating the designed world\n        WorldSeed recreatedDesigned = WorldSeed.fromSeedString(designedSeedString);\n        System.out.println("   Recreation successful: " + (recreatedDesigned.getOriginalSeed() == 987654321L));\n        \n        // Verify biome data matches\n        boolean biomesMatch = true;\n        Type[][] recreatedBiomes = recreatedDesigned.getBiomeData();\n        for (int x = 0; x < World.SIZE && biomesMatch; x++) {\n            for (int y = 0; y < World.SIZE && biomesMatch; y++) {\n                if (biomeData[x][y] != recreatedBiomes[x][y]) {\n                    biomesMatch = false;\n                }\n            }\n        }\n        System.out.println("   Biome data matches: " + biomesMatch);\n        System.out.println();\n        \n        System.out.println("=== SUMMARY ===");\n        System.out.println("Procedural seeds are now ultra-compact: ~10-15 characters");\n        System.out.println("Designed seeds use run-length encoding for efficiency");\n        System.out.println("Both maintain perfect world recreation capability!");\n    }\n}
        }
        
        WorldSeed designedSeed = new WorldSeed(WorldSeed.DESIGNED_SEED, 987654321L, biomeData, new HashMap<>());
        String designedSeedString = designedSeed.toSeedString();
        System.out.println("   Original seed: 987654321");
        System.out.println("   Compact seed: " + designedSeedString);
        System.out.println("   Length: " + designedSeedString.length() + " characters");
        
        // Test recreating the designed world
        WorldSeed recreatedDesigned = WorldSeed.fromSeedString(designedSeedString);
        System.out.println("   Recreation successful: " + (recreatedDesigned.getOriginalSeed() == 987654321L));
        
        // Verify biome data matches
        boolean biomesMatch = true;
        Type[][] recreatedBiomes = recreatedDesigned.getBiomeData();
        for (int x = 0; x < World.SIZE && biomesMatch; x++) {
            for (int y = 0; y < World.SIZE && biomesMatch; y++) {
                if (biomeData[x][y] != recreatedBiomes[x][y]) {
                    biomesMatch = false;
                }
            }
        }
        System.out.println("   Biome data matches: " + biomesMatch);
        System.out.println();
        
        System.out.println("=== SUMMARY ===");
        System.out.println("Procedural seeds are now ultra-compact: ~10-15 characters");
        System.out.println("Designed seeds use run-length encoding for efficiency");
        System.out.println("Both maintain perfect world recreation capability!");
    }
}