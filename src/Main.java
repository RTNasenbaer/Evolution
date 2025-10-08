import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import entities.Entity;
import export.CSVExporter;
import ui.TerminalDisplay;
import world.TraitTestingSeed;
import world.Type;
import world.World;
import world.WorldSeed;

/**
 * Terminal-based interface for the Evolution simulation.
 * Provides an interactive command-line experience with real-time world visualization.
 */
public class Main {
    private World world;
    private int tickspeed = 200; // milliseconds
    private int stepCounter = 0;
    private boolean running = false;
    private Scanner scanner;

    // CSV export tracking
    private boolean exportEntityDetails = false;
    private boolean exportBiomeDetails = false;
    private String entityDetailsFileName = "entity_details.csv";
    private String biomeDetailsFileName = "biome_details.csv";

    public Main() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    private void start() {
        printWelcomeBanner();
        initializeWorld();
        printHelp();

        // Main command loop
        while (true) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            if (!handleCommand(input)) {
                break; // Exit command received
            }
        }

        cleanup();
    }

    private void printWelcomeBanner() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║         🌍 EVOLUTION SIMULATION - TERMINAL MODE 🌍        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        System.out.println("\nA terminal-based evolution and ecosystem simulation.");
        System.out.println("Type '/help' for available commands.\n");
    }

    private void initializeWorld() {
        System.out.println("\n--- World Generation Options ---");
        System.out.println("1. Random world (leave empty)");
        System.out.println("2. Enter custom seed");
        System.out.println("3. Type 'test' for trait testing zones");
        System.out.print("\nChoice: ");
        String seedInput = scanner.nextLine().trim();

        if (seedInput.isEmpty()) {
            world = new World();
            System.out.println("✓ Generated random world: " + world.getWorldSeed().getDescription());
        } else if (seedInput.equalsIgnoreCase("test")) {
            WorldSeed testSeed = TraitTestingSeed.generate();
            world = new World(testSeed);
            System.out.println("✓ Generated TRAIT TESTING world");
            System.out.println("  " + TraitTestingSeed.getDescription());
        } else {
            try {
                world = new World(seedInput);
                System.out.println("✓ Generated world with seed: " + world.getWorldSeed().getDescription());
            } catch (Exception e) {
                System.out.println("⚠ Invalid seed format, generating random world: " + e.getMessage());
                world = new World();
            }
        }

        // Add initial entity at center for trait testing world
        int startX = seedInput.equalsIgnoreCase("test") ? World.SIZE / 2 : 0;
        int startY = seedInput.equalsIgnoreCase("test") ? World.SIZE / 2 : 0;
        world.addEntity(Entity.createDefaultEntity(startX, startY));
        System.out.println("✓ World initialized with 1 entity at (" + startX + ", " + startY + ")");
    }

    private boolean handleCommand(String input) {
        String[] parts = input.split("\\s+");
        String command = parts[0].toLowerCase();

        try {
            switch (command) {
            case "/help":
            case "/h":
                printHelp();
                break;

            case "/run":
            case "/r":
                int steps = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
                runSteps(steps);
                break;

            case "/spawn":
            case "/s":
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    spawnEntity(x, y);
                } else {
                    System.out.println("⚠ Usage: /spawn <x> <y>");
                }
                break;

            case "/move":
            case "/m":
                if (parts.length >= 3) {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    moveFirstEntity(x, y);
                } else if (parts.length >= 5) {
                    int ex = Integer.parseInt(parts[1]);
                    int ey = Integer.parseInt(parts[2]);
                    int x = Integer.parseInt(parts[3]);
                    int y = Integer.parseInt(parts[4]);
                    moveEntityAt(ex, ey, x, y);
                } else {
                    System.out.println("⚠ Usage: /move <x> <y> or /move <ex> <ey> <x> <y>");
                }
                break;

            case "/tickspeed":
            case "/t":
                if (parts.length >= 2) {
                    tickspeed = Integer.parseInt(parts[1]);
                    System.out.println("✓ Tickspeed set to " + tickspeed + " ms");
                } else {
                    System.out.println("⚠ Usage: /tickspeed <milliseconds>");
                }
                break;

            case "/display":
            case "/d":
                renderWorld();
                break;

            case "/stats":
            case "/st":
                showStatistics();
                break;

            case "/seed":
                showSeed();
                break;

            case "/import":
            case "/i":
                importSeedFromFile();
                break;

            case "/export":
            case "/e":
                if (parts.length >= 2 && parts[1].equalsIgnoreCase("details")) {
                    toggleEntityDetailsExport();
                } else if (parts.length >= 2 && parts[1].equalsIgnoreCase("biomes")) {
                    toggleBiomeDetailsExport();
                } else {
                    System.out.println("⚠ Usage: /export details  OR  /export biomes");
                }
                break;

            case "/batch":
            case "/b":
                if (parts.length >= 3) {
                    int runs = Integer.parseInt(parts[1]);
                    int batchSteps = Integer.parseInt(parts[2]);
                    runBatchSimulation(runs, batchSteps);
                } else {
                    System.out.println("⚠ Usage: /batch <runs> <steps>");
                }
                break;

            case "/clear":
            case "/c":
                clearScreen();
                break;

            case "/auto":
            case "/a":
                if (parts.length >= 2) {
                    int autoSteps = Integer.parseInt(parts[1]);
                    runAutoMode(autoSteps);
                } else {
                    System.out.println("⚠ Usage: /auto <steps>");
                }
                break;

            case "/exit":
            case "/quit":
            case "/q":
                System.out.println("\n👋 Thanks for using Evolution Simulation! Goodbye!");
                return false;

            default:
                System.out.println("⚠ Unknown command: " + command + ". Type /help for available commands.");
            }
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid number format. Please check your input.");
        } catch (Exception e) {
            System.out.println("⚠ Error executing command: " + e.getMessage());
        }

        return true;
    }

    private void printHelp() {
        System.out.println("\n╔═════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        AVAILABLE COMMANDS                        ║");
        System.out.println("╠═════════════════════════════════════════════════════════════════╣");
        System.out.println("║ /run [n]    (/r)  - Run n simulation steps (default: 1)         ║");
        System.out.println("║ /auto <n>   (/a)  - Run n steps with continuous display         ║");
        System.out.println("║ /spawn <x> <y> (/s) - Spawn entity at coordinates               ║");
        System.out.println("║ /move <x> <y>  (/m) - Move first entity to coordinates          ║");
        System.out.println("║ /display    (/d)  - Display current world state                 ║");
        System.out.println("║ /stats      (/st) - Show world statistics                       ║");
        System.out.println("║ /seed            - Display current world seed                    ║");
        System.out.println("║ /import     (/i)  - Import world from seed file                ║");
        System.out.println("║ /tickspeed <ms> (/t) - Set simulation speed                     ║");
        System.out.println("║ /export     (/e)  - Toggle CSV biome count export               ║");
        System.out.println("║ /export details  - Toggle CSV entity details export             ║");
        System.out.println("║ /export biomes   - Toggle CSV biome details export              ║");
        System.out.println("║ /batch <r> <s> (/b) - Run r batch simulations with s steps each ║");
        System.out.println("║ /clear      (/c)  - Clear terminal screen                       ║");
        System.out.println("║ /help       (/h)  - Show this help message                      ║");
        System.out.println("║ /exit       (/q)  - Exit simulation                             ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════╝");
    }

    private void runSteps(int steps) {
        System.out.println("\n▶ Running " + steps + " simulation step(s)...\n");

        for (int i = 0; i < steps; i++) {
            // Update all entities
            var entitiesCopy = java.util.List.copyOf(world.getEntities());
            for (Entity entity : entitiesCopy) {
                entity.update(world, tickspeed, World.SIZE);
            }

            stepCounter++;

            // Export data if enabled
            if (exportEntityDetails) {
                exportEntityDetailsData();
            }
            if (exportBiomeDetails) {
                exportBiomeDetailsData();
            }
        }

        System.out.println("✓ Completed " + steps + " step(s). Current step: " + stepCounter);
        System.out.println("  Entities alive: " + world.getEntities().size());

        // Show world after run
        renderWorld();
    }

    private void runAutoMode(int steps) {
        System.out.println("\n▶ Starting auto mode for " + steps + " steps...");
        System.out.println("  (Updates will be displayed continuously)\n");

        running = true;

        for (int i = 0; i < steps && running; i++) {
            // Update all entities
            var entitiesCopy = java.util.List.copyOf(world.getEntities());
            for (Entity entity : entitiesCopy) {
                entity.update(world, tickspeed, World.SIZE);
            }

            stepCounter++;

            // Display world
            clearScreen();
            System.out.println("Step: " + stepCounter + " | Entities: " + world.getEntities().size() +
                    " | Press Ctrl+C to stop");
            renderWorld();

            // Export data if enabled
            if (exportEntityDetails) {
                exportEntityDetailsData();
            }
            if (exportBiomeDetails) {
                exportBiomeDetailsData();
            }

            // Sleep for tickspeed
            try {
                Thread.sleep(tickspeed);
            } catch (InterruptedException e) {
                running = false;
                break;
            }

            // Check if all entities died
            if (world.getEntities().isEmpty()) {
                System.out.println("\n⚠ All entities have died. Simulation ended.");
                break;
            }
        }

        running = false;
        System.out.println("\n✓ Auto mode completed. Total steps: " + stepCounter);
    }

    private void spawnEntity(int x, int y) {
        if (x < 0 || x >= World.SIZE || y < 0 || y >= World.SIZE) {
            System.out.println("⚠ Invalid coordinates. Must be between 0 and " + (World.SIZE - 1));
            return;
        }

        world.addEntity(Entity.createDefaultEntity(x, y));
        System.out.println("✓ Entity spawned at (" + x + ", " + y + ")");
        renderWorld();
    }

    private void moveFirstEntity(int x, int y) {
        if (world.getEntities().isEmpty()) {
            System.out.println("⚠ No entities to move!");
            return;
        }

        Entity entity = world.getEntities().get(0);
        entity.moveTo(x, y);
        entity.eat(world.getTile(entity.getX(), entity.getY()), tickspeed, World.SIZE);

        if (!entity.isAlive()) {
            world.removeEntity(entity);
            System.out.println("⚠ Entity died during movement!");
        } else {
            System.out.println("✓ Entity moved to (" + x + ", " + y + ")");
        }

        renderWorld();
    }

    private void moveEntityAt(int ex, int ey, int x, int y) {
        Entity entity = world.getEntity(ex, ey);

        if (entity == null) {
            System.out.println("⚠ No entity found at (" + ex + ", " + ey + ")");
            return;
        }

        entity.moveTo(x, y);
        entity.eat(world.getTile(entity.getX(), entity.getY()), tickspeed, World.SIZE);

        if (!entity.isAlive()) {
            world.removeEntity(entity);
            System.out.println("⚠ Entity died during movement!");
        } else {
            System.out.println("✓ Entity moved from (" + ex + ", " + ey + ") to (" + x + ", " + y + ")");
        }

        renderWorld();
    }

    private void renderWorld() {
        System.out.println();
        TerminalDisplay.renderWorld(world);
        System.out.println("\nStep: " + stepCounter + " | Entities: " + world.getEntities().size());
    }

    private void showStatistics() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    WORLD STATISTICS                        ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        Map<Type, Integer> entityCounts = world.countEntitiesInAllBiomes();
        int totalEntities = entityCounts.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("║ World Size:        " + World.SIZE + "x" + World.SIZE + " tiles                       ║");
        System.out.println("║ Steps Completed:   " + String.format("%-37d", stepCounter) + "║");
        System.out.println("║ Total Entities:    " + String.format("%-37d", totalEntities) + "║");
        System.out.println("║ Tickspeed:         " + String.format("%-34dms", tickspeed) + "║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║                  ENTITIES BY BIOME                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        for (Type type : Type.values()) {
            int count = entityCounts.getOrDefault(type, 0);
            String biomeInfo = String.format("║ %-15s: %-40d║", type.name(), count);
            System.out.println(biomeInfo);
        }

        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println(
                "║ Entity Export:     " + String.format("%-37s", exportEntityDetails ? "ENABLED" : "DISABLED") + "║");
        System.out.println(
                "║ Biome Export:      " + String.format("%-37s", exportBiomeDetails ? "ENABLED" : "DISABLED") + "║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private void showSeed() {
        WorldSeed seed = world.getWorldSeed();
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                       WORLD SEED                           ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║ Description: " + String.format("%-46s", seed.getDescription()) + "║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║ Seed String:                                               ║");
        System.out.println("║ " + wrapText(seed.toSeedString(), 60) + " ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    private String wrapText(String text, int width) {
        if (text.length() <= width) {
            return String.format("%-" + width + "s", text);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += width) {
            if (i > 0)
                result.append("\n║ ");
            int endIndex = Math.min(i + width, text.length());
            result.append(String.format("%-" + width + "s", text.substring(i, endIndex)));
        }
        return result.toString();
    }

    private void toggleEntityDetailsExport() {
        exportEntityDetails = !exportEntityDetails;

        if (exportEntityDetails) {
            entityDetailsFileName = "entity_details_" + System.currentTimeMillis() + ".csv";
            // Create file with header
            try (FileWriter writer = new FileWriter(entityDetailsFileName)) {
                writer.write("Step,EntityID,X,Y,Energy,Age,Speed,Mass,EnergyEfficiency,ReproductionThreshold,");
                writer.write("SightRange,MetabolismRate,MaxLifespan,BiomeType,HasFood\\n");
                System.out.println("✓ Entity details export ENABLED. File: " + entityDetailsFileName);
            } catch (IOException e) {
                System.out.println("⚠ Failed to create entity details file: " + e.getMessage());
                exportEntityDetails = false;
            }
        } else {
            System.out.println("✓ Entity details export DISABLED. Data saved to: " + entityDetailsFileName);
        }
    }

    private void exportEntityDetailsData() {
        try {
            CSVExporter.exportEntityDetails(world, stepCounter, entityDetailsFileName);
        } catch (IOException e) {
            System.out.println("⚠ Failed to export entity details: " + e.getMessage());
        }
    }

    private void toggleBiomeDetailsExport() {
        exportBiomeDetails = !exportBiomeDetails;

        if (exportBiomeDetails) {
            biomeDetailsFileName = "biome_details_" + System.currentTimeMillis() + ".csv";
            System.out.println("✓ Biome details export ENABLED. File: " + biomeDetailsFileName);
        } else {
            System.out.println("✓ Biome details export DISABLED. Data saved to: " + biomeDetailsFileName);
        }
    }

    private void exportBiomeDetailsData() {
        try {
            CSVExporter.exportBiomeDetails(world, stepCounter, biomeDetailsFileName);
        } catch (IOException e) {
            System.out.println("⚠ Failed to export biome details: " + e.getMessage());
        }
    }

    private void runBatchSimulation(int runs, int stepsPerRun) {
        System.out.println("\n▶ Starting batch simulation...");
        System.out.println("  Runs: " + runs);
        System.out.println("  Steps per run: " + stepsPerRun);

        String timestamp = String.valueOf(System.currentTimeMillis());
        String batchFileName = "batch_results_terminal_" + timestamp + ".csv";

        // Use BatchSimulation class for proper simulation handling
        BatchSimulation batch = new BatchSimulation();
        BatchSimulation.BatchConfiguration config = new BatchSimulation.BatchConfiguration(
                runs, stepsPerRun, tickspeed, true, 1, batchFileName);

        try {
            java.util.List<BatchSimulation.SimulationResult> results = batch.runBatchSimulation(config,
                    new BatchSimulation.SimulationProgressCallback() {
                        @Override
                        public void onSimulationCompleted(int completed, int total,
                                BatchSimulation.SimulationResult result) {
                            System.out.println("\n  ✓ Run " + completed + "/" + total + " completed");
                            System.out.println("    Final entities: " + result.getFinalEntityCount() +
                                    " (Max: " + result.getMaxEntityCount() + ")");
                            System.out.println("    Execution time: " + result.getExecutionTime() + " ms");
                        }

                        @Override
                        public void onStepCompleted(int runIndex, int step, int entityCount) {
                            // Print progress every 100 steps
                            if (step % 100 == 0) {
                                System.out.println(
                                        "    Step " + step + "/" + stepsPerRun + " - Entities: " + entityCount);
                            }
                        }
                    });

            System.out.println("\n✓ Batch simulation completed!");
            System.out.println("  Total runs: " + results.size());
            System.out.println("  Results exported to: " + batchFileName);
        } catch (Exception e) {
            System.out.println("⚠ Batch simulation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            batch.shutdown();
        }
    }

    private void clearScreen() {
        // ANSI escape code to clear screen
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void importSeedFromFile() {
        System.out.print("Enter seed file path (e.g., examples/world.seed): ");
        String filePath = scanner.nextLine().trim();

        if (filePath.isEmpty()) {
            System.out.println("⚠ No file path provided");
            return;
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            String seedString = new String(java.nio.file.Files.readAllBytes(path)).trim();

            // Create new world with imported seed
            World newWorld = new World(seedString);
            world = newWorld;
            stepCounter = 0;

            // Add initial entity
            world.addEntity(Entity.createDefaultEntity(0, 0));

            System.out.println("✓ World imported from file: " + filePath);
            System.out.println("✓ Seed: " + world.getWorldSeed().getDescription());
            renderWorld();
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("⚠ File not found: " + filePath);
        } catch (IOException e) {
            System.out.println("⚠ Failed to read file: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("⚠ Invalid seed format: " + e.getMessage());
        }
    }

    private void cleanup() {
        scanner.close();
    }
}
