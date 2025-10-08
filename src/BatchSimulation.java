import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
        private double averageAge;
        private double averageEnergy;
        private Map<String, Double> averageTraits;

        public SimulationResult(long seed, int finalEntityCount, int maxEntityCount, int stepsRun,
                long executionTime, Map<Type, Integer> finalBiomeDistribution,
                double averageAge, double averageEnergy, Map<String, Double> averageTraits) {
            this.seed = seed;
            this.finalEntityCount = finalEntityCount;
            this.maxEntityCount = maxEntityCount;
            this.stepsRun = stepsRun;
            this.executionTime = executionTime;
            this.finalBiomeDistribution = finalBiomeDistribution;
            this.averageAge = averageAge;
            this.averageEnergy = averageEnergy;
            this.averageTraits = averageTraits;
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

        public double getAverageAge() {
            return averageAge;
        }

        public double getAverageEnergy() {
            return averageEnergy;
        }

        public Map<String, Double> getAverageTraits() {
            return averageTraits;
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

        // Calculate final statistics
        Map<Type, Integer> biomeDistribution = world.countEntitiesInAllBiomes();
        double averageAge = 0;
        double averageEnergy = 0;
        Map<String, Double> averageTraits = new HashMap<>();

        if (!world.getEntities().isEmpty()) {
            double totalAge = 0;
            double totalEnergy = 0;
            double totalSpeed = 0;
            double totalMass = 0;
            double totalEnergyEfficiency = 0;
            double totalSightRange = 0;
            double totalMetabolismRate = 0;
            double totalReproductionThreshold = 0;

            for (Entity entity : world.getEntities()) {
                totalAge += entity.getAge();
                totalEnergy += entity.getEnergy();
                totalSpeed += entity.getSpeed();
                totalMass += entity.getMass();
                totalEnergyEfficiency += entity.getEnergyEfficiency();
                totalSightRange += entity.getSightRange();
                totalMetabolismRate += entity.getMetabolismRate();
                totalReproductionThreshold += entity.getReproductionThreshold();
            }

            int entityCount = world.getEntities().size();
            averageAge = totalAge / entityCount;
            averageEnergy = totalEnergy / entityCount;
            averageTraits.put("speed", totalSpeed / entityCount);
            averageTraits.put("mass", totalMass / entityCount);
            averageTraits.put("energyEfficiency", totalEnergyEfficiency / entityCount);
            averageTraits.put("sightRange", totalSightRange / entityCount);
            averageTraits.put("metabolismRate", totalMetabolismRate / entityCount);
            averageTraits.put("reproductionThreshold", totalReproductionThreshold / entityCount);
        }

        return new SimulationResult(seed, world.getEntities().size(), maxEntityCount, stepsRun,
                executionTime, biomeDistribution, averageAge, averageEnergy, averageTraits);
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

    private void exportResults(List<SimulationResult> results, String fileName) {
        StringBuilder csvBuilder = new StringBuilder();

        // Write header
        csvBuilder.append("Seed,FinalEntityCount,MaxEntityCount,StepsRun,ExecutionTime(ms),AverageAge,AverageEnergy");
        csvBuilder.append(
                ",AvgSpeed,AvgMass,AvgEnergyEfficiency,AvgSightRange,AvgMetabolismRate,AvgReproductionThreshold");
        for (Type type : Type.values()) {
            csvBuilder.append(",").append(type.name()).append("Count");
        }
        csvBuilder.append("\n");

        // Write data using StringBuilder for better performance
        for (SimulationResult result : results) {
            csvBuilder.append(result.getSeed()).append(",")
                    .append(result.getFinalEntityCount()).append(",")
                    .append(result.getMaxEntityCount()).append(",")
                    .append(result.getStepsRun()).append(",")
                    .append(result.getExecutionTime()).append(",")
                    .append(String.format("%.2f", result.getAverageAge())).append(",")
                    .append(String.format("%.2f", result.getAverageEnergy())).append(",");

            Map<String, Double> traits = result.getAverageTraits();
            csvBuilder.append(String.format("%.3f", traits.getOrDefault("speed", 0.0))).append(",")
                    .append(String.format("%.3f", traits.getOrDefault("mass", 0.0))).append(",")
                    .append(String.format("%.3f", traits.getOrDefault("energyEfficiency", 0.0))).append(",")
                    .append(String.format("%.3f", traits.getOrDefault("sightRange", 0.0))).append(",")
                    .append(String.format("%.3f", traits.getOrDefault("metabolismRate", 0.0))).append(",")
                    .append(String.format("%.3f", traits.getOrDefault("reproductionThreshold", 0.0))).append(",");

            Map<Type, Integer> distribution = result.getFinalBiomeDistribution();
            for (Type type : Type.values()) {
                csvBuilder.append(distribution.getOrDefault(type, 0)).append(",");
            }
            csvBuilder.append("\n");
        }

        // Write all at once for better I/O performance
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(csvBuilder.toString());
            System.out.println("Batch simulation results exported to: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to export results: " + e.getMessage());
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
}