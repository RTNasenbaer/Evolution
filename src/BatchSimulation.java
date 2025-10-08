import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import entities.Entity;
import export.CSVExporter;
import world.Type;
import world.World;

public class BatchSimulation {

    public static class SimulationResult {
        private long seed;
        private int finalEntityCount;
        private int maxEntityCount;
        private int stepsRun;
        private long executionTime;
        private Map<Type, Integer> finalBiomeDistribution;
        private List<Entity> finalEntities; // RAW DATA: All final entities for analysis

        public SimulationResult(long seed, int finalEntityCount, int maxEntityCount, int stepsRun,
                long executionTime, Map<Type, Integer> finalBiomeDistribution,
                List<Entity> finalEntities) {
            this.seed = seed;
            this.finalEntityCount = finalEntityCount;
            this.maxEntityCount = maxEntityCount;
            this.stepsRun = stepsRun;
            this.executionTime = executionTime;
            this.finalBiomeDistribution = finalBiomeDistribution;
            this.finalEntities = new ArrayList<>(finalEntities); // Copy for safety
        }

        // Getters
        public long getSeed() {
            return seed;
        }

        public int getFinalEntityCount() {
            return finalEntityCount;
        }

        public int getMaxEntityCount() {
            return maxEntityCount;
        }

        public int getStepsRun() {
            return stepsRun;
        }

        public long getExecutionTime() {
            return executionTime;
        }

        public Map<Type, Integer> getFinalBiomeDistribution() {
            return finalBiomeDistribution;
        }

        public List<Entity> getFinalEntities() {
            return finalEntities;
        }
    }

    public static class BatchConfiguration {
        private int numberOfRuns;
        private int maxStepsPerRun;
        private int tickSpeed;
        private boolean useRandomSeeds;
        private List<Long> specificSeeds;
        private int initialEntityCount;
        private String outputFileName;
        private long entityPlacementSeed; // Fixed seed for consistent entity placement
        private boolean useConsistentEntityPlacement;

        public BatchConfiguration(int numberOfRuns, int maxStepsPerRun, int tickSpeed,
                boolean useRandomSeeds, int initialEntityCount, String outputFileName) {
            this.numberOfRuns = numberOfRuns;
            this.maxStepsPerRun = maxStepsPerRun;
            this.tickSpeed = tickSpeed;
            this.useRandomSeeds = useRandomSeeds;
            this.initialEntityCount = initialEntityCount;
            this.outputFileName = outputFileName;
            this.specificSeeds = new ArrayList<>();
            this.entityPlacementSeed = 12345L; // Fixed seed for consistency
            this.useConsistentEntityPlacement = true;
        }

        public void addSpecificSeed(long seed) {
            specificSeeds.add(seed);
        }

        // Getters
        public int getNumberOfRuns() {
            return numberOfRuns;
        }

        public int getMaxStepsPerRun() {
            return maxStepsPerRun;
        }

        public int getTickSpeed() {
            return tickSpeed;
        }

        public boolean isUseRandomSeeds() {
            return useRandomSeeds;
        }

        public List<Long> getSpecificSeeds() {
            return specificSeeds;
        }

        public int getInitialEntityCount() {
            return initialEntityCount;
        }

        public String getOutputFileName() {
            return outputFileName;
        }

        public long getEntityPlacementSeed() {
            return entityPlacementSeed;
        }

        public boolean isUseConsistentEntityPlacement() {
            return useConsistentEntityPlacement;
        }

        public void setEntityPlacementSeed(long seed) {
            this.entityPlacementSeed = seed;
        }

        public void setUseConsistentEntityPlacement(boolean use) {
            this.useConsistentEntityPlacement = use;
        }

        /**
         * Configure entity placement for batch simulation.
         * 
         * @param useConsistent If true, all simulations will use the same entity starting positions
         * @param seed          The seed to use for entity placement (only used if useConsistent is true)
         */
        public void configureEntityPlacement(boolean useConsistent, long seed) {
            this.useConsistentEntityPlacement = useConsistent;
            this.entityPlacementSeed = seed;
        }
    }

    private ExecutorService executorService;
    private List<Future<SimulationResult>> runningSimulations;
    private volatile boolean stopRequested = false;

    public BatchSimulation() {
        this.executorService = Executors.newCachedThreadPool();
        this.runningSimulations = new ArrayList<>();
    }

    public List<SimulationResult> runBatchSimulation(BatchConfiguration config,
            SimulationProgressCallback callback) {
        List<SimulationResult> results = new ArrayList<>();
        List<Future<SimulationResult>> futures = new ArrayList<>();

        // Prepare seeds
        List<Long> seeds = new ArrayList<>();
        if (config.isUseRandomSeeds()) {
            Random random = new Random();
            for (int i = 0; i < config.getNumberOfRuns(); i++) {
                seeds.add(random.nextLong());
            }
        } else {
            seeds.addAll(config.getSpecificSeeds());
        }

        // Start simulations
        for (int i = 0; i < Math.min(config.getNumberOfRuns(), seeds.size()); i++) {
            final long seed = seeds.get(i);
            final int runIndex = i;

            Future<SimulationResult> future = executorService.submit(() -> {
                return runSingleSimulation(seed, config, runIndex, callback);
            });

            futures.add(future);
            runningSimulations.add(future);
        }

        // Collect results
        for (int i = 0; i < futures.size(); i++) {
            try {
                SimulationResult result = futures.get(i).get();
                if (result != null) {
                    results.add(result);
                    if (callback != null) {
                        callback.onSimulationCompleted(i + 1, config.getNumberOfRuns(), result);
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Simulation " + (i + 1) + " failed: " + e.getMessage());
            }
        }

        // Export results
        if (config.getOutputFileName() != null && !config.getOutputFileName().isEmpty()) {
            exportResults(results, config.getOutputFileName());
        }

        return results;
    }

    private SimulationResult runSingleSimulation(long seed, BatchConfiguration config,
            int runIndex, SimulationProgressCallback callback) {
        if (stopRequested) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        World world = new World(seed);

        // Add initial entities
        Random random;
        if (config.isUseConsistentEntityPlacement()) {
            // Use fixed seed for consistent entity placement across all simulations
            random = new Random(config.getEntityPlacementSeed());
        } else {
            // Use world seed offset for varied placement (original behavior)
            random = new Random(seed + 1000);
        }

        for (int i = 0; i < config.getInitialEntityCount(); i++) {
            int x = random.nextInt(World.SIZE);
            int y = random.nextInt(World.SIZE);
            world.addEntity(Entity.createDefaultEntity(x, y));
        }

        int maxEntityCount = world.getEntities().size();
        int stepsRun = 0;

        // Run simulation
        for (int step = 0; step < config.getMaxStepsPerRun() && !stopRequested; step++) {
            // Update all entities
            List<Entity> entitiesCopy = new ArrayList<>(world.getEntities());
            for (Entity entity : entitiesCopy) {
                entity.update(world, config.getTickSpeed(), World.SIZE);
            }

            // Track maximum entity count
            maxEntityCount = Math.max(maxEntityCount, world.getEntities().size());
            stepsRun = step + 1;

            // Check if all entities died
            if (world.getEntities().isEmpty()) {
                break;
            }

            // Progress callback every 100 steps
            if (callback != null && step % 100 == 0) {
                callback.onStepCompleted(runIndex, step, world.getEntities().size());
            }
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // Collect final state (RAW DATA - no averages)
        Map<Type, Integer> biomeDistribution = world.countEntitiesInAllBiomes();
        List<Entity> finalEntities = new ArrayList<>(world.getEntities());

        return new SimulationResult(seed, world.getEntities().size(), maxEntityCount, stepsRun,
                executionTime, biomeDistribution, finalEntities);
    }

    public void stopBatch() {
        stopRequested = true;
        for (Future<SimulationResult> future : runningSimulations) {
            future.cancel(true);
        }
    }

    public void shutdown() {
        stopBatch();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    /**
     * Export batch simulation results (RAW DATA - final entity states).
     * Format: One row per entity with Seed, RunMetadata, and all entity traits.
     * All analysis and aggregation should be done in Python scripts.
     * 
     * @param results  List of simulation results with raw final entity data
     * @param fileName Output CSV filename
     */
    private void exportResults(List<SimulationResult> results, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write header for entity-level data
            writer.write("Seed,FinalEntityCount,MaxEntityCount,StepsRun,ExecutionTime(ms),");
            writer.write("EntityID,X,Y,Energy,Age,Endurance,Adaptation,Mobility,Efficiency\n");

            // Write one row per entity in each simulation
            for (SimulationResult result : results) {
                long seed = result.getSeed();
                int finalCount = result.getFinalEntityCount();
                int maxCount = result.getMaxEntityCount();
                int steps = result.getStepsRun();
                long execTime = result.getExecutionTime();

                // If no entities survived, write one summary row with null entity data
                if (result.getFinalEntities().isEmpty()) {
                    writer.write(String.format("%d,%d,%d,%d,%d,,,,,,,,,\n",
                            seed, finalCount, maxCount, steps, execTime));
                } else {
                    // Write each entity as a row
                    int entityId = 0;
                    for (Entity entity : result.getFinalEntities()) {
                        writer.write(
                                String.format("%d,%d,%d,%d,%d,%d,%d,%d,%.2f,%d,%.3f,%.3f,%.3f,%.3f\n",
                                        seed,
                                        finalCount,
                                        maxCount,
                                        steps,
                                        execTime,
                                        entityId++,
                                        entity.getX(),
                                        entity.getY(),
                                        entity.getEnergy(),
                                        entity.getAge(),
                                        entity.getEndurance(),
                                        entity.getAdaptation(),
                                        entity.getMobility(),
                                        entity.getEfficiency()));
                    }
                }
            }

            System.out.println("Batch simulation raw entity data exported to: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to export results: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Export detailed entity-level data for trait analysis.
     * Uses CSVExporter for consistent CSV formatting.
     */
    public static void exportEntityDetails(World world, int step, String filename) throws IOException {
        CSVExporter.exportEntityDetails(world, step, filename);
    }

    /**
     * Export detailed biome-level data including biome properties and entities within each biome.
     * Uses CSVExporter for consistent CSV formatting.
     */
    public static void exportBiomeDetails(World world, int step, String filename) throws IOException {
        CSVExporter.exportBiomeDetails(world, step, filename);
    }

    public interface SimulationProgressCallback {
        void onSimulationCompleted(int completed, int total, SimulationResult result);

        void onStepCompleted(int runIndex, int step, int entityCount);
    }

    /**
     * Main method for running batch simulations from command line
     * Usage: java BatchSimulation [numRuns] [stepsPerRun] [initialEntities]
     */
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println(" BATCH SIMULATION RUNNER");
        System.out.println(" Generate data for analysis");
        System.out.println("================================================================================");

        // Parse command line arguments
        int numRuns = 5;
        int stepsPerRun = 1000;
        int initialEntities = 10;

        if (args.length > 0) {
            try {
                numRuns = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of runs, using default: " + numRuns);
            }
        }
        if (args.length > 1) {
            try {
                stepsPerRun = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid steps per run, using default: " + stepsPerRun);
            }
        }
        if (args.length > 2) {
            try {
                initialEntities = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid initial entities, using default: " + initialEntities);
            }
        }

        System.out.println("\nConfiguration:");
        System.out.println("  Runs: " + numRuns);
        System.out.println("  Steps per run: " + stepsPerRun);
        System.out.println("  Initial entities: " + initialEntities);
        System.out.println();

        // Create batch simulation
        BatchSimulation batchSim = new BatchSimulation();
        BatchConfiguration config = new BatchConfiguration(
                numRuns,
                stepsPerRun,
                200, // tickspeed
                true, // use random seeds
                initialEntities,
                "data/batch_results_" + System.currentTimeMillis() + ".csv");

        // Progress callback
        SimulationProgressCallback callback = new SimulationProgressCallback() {
            @Override
            public void onSimulationCompleted(int completed, int total, SimulationResult result) {
                System.out.println(String.format("[%d/%d] Completed - Seed: %d, Final: %d, Max: %d, Steps: %d",
                        completed, total, result.getSeed(), result.getFinalEntityCount(),
                        result.getMaxEntityCount(), result.getStepsRun()));
            }

            @Override
            public void onStepCompleted(int runIndex, int step, int entityCount) {
                // Print progress dot every 100 steps
                if (step > 0 && step % 100 == 0) {
                    System.out.print(".");
                }
            }
        };

        // Run batch simulation
        System.out.println("Starting batch simulation...\n");
        List<SimulationResult> results = batchSim.runBatchSimulation(config, callback);

        // Print summary
        System.out.println("\n================================================================================");
        System.out.println("✓ Batch simulation complete!");
        System.out.println("  Total simulations: " + results.size());
        System.out.println("  Results saved to: " + config.getOutputFileName());

        if (!results.isEmpty()) {
            double avgFinal = results.stream()
                    .mapToInt(SimulationResult::getFinalEntityCount).average().orElse(0);
            double avgMax = results.stream()
                    .mapToInt(SimulationResult::getMaxEntityCount).average().orElse(0);
            long totalTime = results.stream()
                    .mapToLong(SimulationResult::getExecutionTime).sum();

            System.out.println("\nStatistics:");
            System.out.println("  Average final entities: " + String.format("%.1f", avgFinal));
            System.out.println("  Average max entities: " + String.format("%.1f", avgMax));
            System.out.println("  Total execution time: " + totalTime + "ms");
        }
        System.out.println("================================================================================");

        // Shutdown
        batchSim.shutdown();
    }
}