import java.util.List;
import java.util.Map;

import entities.Entity;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.AppStyles;
import ui.ConfigDisplay;
import ui.GraphDisplay;
import ui.ResponsiveLayoutManager;
import ui.WorldDisplay;
import world.Type;
import world.World;

public class MainAppNew extends Application {
    private World world;
    private int tickspeed = 200;
    private Stage primaryStage;
    private Label statusLabel;
    private Thread simulationThread;
    private volatile boolean running = false;
    private int stepCounter = 0;

    // UI Components
    private WorldDisplay worldDisplay;
    private GraphDisplay graphDisplay;
    private ConfigDisplay configDisplay;
    private ResponsiveLayoutManager layoutManager;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        initializeWorld();
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        world.addEntity(Entity.createDefaultEntity(0, 0));

        renderWorld();
        updateSeedDisplay();

        primaryStage.setTitle("Evolution Simulation");
        primaryStage.show();
    }

    private void initializeComponents() {
        statusLabel = new Label("Ready");
        worldDisplay = new WorldDisplay(world);
        graphDisplay = new GraphDisplay();
        configDisplay = new ConfigDisplay();
        layoutManager = new ResponsiveLayoutManager(primaryStage);
    }

    private void setupLayout() {
        // Main layout with responsive design
        VBox root = new VBox(AppStyles.SPACING_MEDIUM);
        root.setPadding(new Insets(AppStyles.PADDING_MEDIUM));
        root.setStyle(AppStyles.getBackgroundStyle());

        // Create main content area with responsive sizing
        HBox mainContent = new HBox(AppStyles.SPACING_MEDIUM);

        // Left panel with world display and chart
        VBox leftPanel = new VBox(AppStyles.SPACING_MEDIUM);
        leftPanel.getChildren().addAll(worldDisplay.getContainer(), graphDisplay.getContainer());

        // Right panel with controls
        VBox rightPanel = configDisplay.getContainer();

        // Set responsive sizes
        updateComponentSizes();

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        // Status section
        HBox statusSection = createStatusSection();

        root.getChildren().addAll(mainContent, statusSection);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Setup zoom/pan key handlers
        worldDisplay.setupKeyHandlers(scene); // Add resize listener for responsive behavior
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateComponentSizes());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateComponentSizes());
    }

    private void updateComponentSizes() {
        double controlPanelWidth = layoutManager.getOptimalControlPanelWidth();
        double worldDisplayWidth = layoutManager.getOptimalWorldDisplayWidth();
        double worldDisplayHeight = layoutManager.getOptimalWorldDisplayHeight();
        double chartHeight = layoutManager.getOptimalChartHeight();

        configDisplay.setPreferredWidth(controlPanelWidth);

        // Update world display size
        worldDisplay.setPreferredSize(worldDisplayWidth, worldDisplayHeight);

        graphDisplay.setPreferredSize(worldDisplayWidth, chartHeight);
    }

    private void setupEventHandlers() {
        // Simulation controls
        configDisplay.runStepBtn.setOnAction(e -> runSteps(1));
        configDisplay.runManyBtn.setOnAction(e -> {
            int steps = parseIntOr(configDisplay.stepsField.getText(), 1);
            runSteps(steps);
        });
        configDisplay.stopBtn.setOnAction(e -> stopSimulation());
        configDisplay.setTickspeedBtn.setOnAction(e -> {
            int newTickspeed = parseIntOr(configDisplay.tickspeedField.getText(), 200);
            if (newTickspeed >= 0) {
                tickspeed = newTickspeed;
                statusLabel.setText("Tickspeed set to " + tickspeed + " ms.");
            } else {
                statusLabel.setText("Tickspeed must be non-negative.");
            }
        });

        // Entity controls
        configDisplay.spawnBtn.setOnAction(e -> {
            int x = parseIntOr(configDisplay.spawnX.getText(), 0);
            int y = parseIntOr(configDisplay.spawnY.getText(), 0);
            world.addEntity(Entity.createDefaultEntity(x, y));
            renderWorld();
        });
        // Entity selection handler
        configDisplay.selectEntityBtn.setOnAction(e -> {
            statusLabel.setText("Click on the world grid to select an entity...");
            enableEntitySelection();
        });

        // Move selected entity handler
        configDisplay.moveSelectedBtn.setOnAction(e -> {
            Entity selectedEntity = configDisplay.getSelectedEntity();
            if (selectedEntity != null) {
                int targetX = parseIntOr(configDisplay.moveX.getText(), selectedEntity.getX());
                int targetY = parseIntOr(configDisplay.moveY.getText(), selectedEntity.getY());

                // Validate target coordinates
                if (targetX >= 0 && targetX < World.SIZE && targetY >= 0 && targetY < World.SIZE) {
                    // Check if target position is occupied
                    Entity existingEntity = world.getEntity(targetX, targetY);
                    if (existingEntity == null || existingEntity == selectedEntity) {
                        selectedEntity.moveTo(targetX, targetY);
                        selectedEntity.eat(world.getTile(selectedEntity.getX(), selectedEntity.getY()), tickspeed,
                                World.SIZE);

                        // Update entity selection display
                        configDisplay.setSelectedEntity(selectedEntity);

                        // Check if entity died from movement
                        if (!selectedEntity.isAlive()) {
                            world.removeEntity(selectedEntity);
                            configDisplay.setSelectedEntity(null);
                            statusLabel.setText("Entity died during movement.");
                        } else {
                            statusLabel.setText(String.format("Entity moved to (%d, %d).", targetX, targetY));
                        }
                    } else {
                        statusLabel.setText("Target position is occupied!");
                    }
                } else {
                    statusLabel.setText("Invalid target coordinates!");
                }
            } else {
                statusLabel.setText("No entity selected!");
            }
            renderWorld();
        });

        // Analysis controls
        configDisplay.inspectBtn.setOnAction(e -> {
            Entity selectedEntity = configDisplay.getSelectedEntity();
            if (selectedEntity != null) {
                showEntityInspector(selectedEntity.getX(), selectedEntity.getY());
            } else {
                statusLabel.setText("Please select an entity first!");
            }
        });
        configDisplay.statsBtn.setOnAction(e -> showStatistics());
        configDisplay.batchBtn.setOnAction(e -> {
            int runs = parseIntOr(configDisplay.batchRunsField.getText(), 5);
            int steps = parseIntOr(configDisplay.batchStepsField.getText(), 1000);
            runBatchSimulation(runs, steps);
        });
    }

    private HBox createStatusSection() {
        HBox section = new HBox(AppStyles.SPACING_SMALL);
        section.setStyle(AppStyles.getStatusBarStyle());

        Label statusTitle = new Label("Status:");
        statusTitle.setStyle(AppStyles.getStatusTextStyle() + " -fx-font-weight: bold;");
        statusLabel.setStyle(AppStyles.getStatusTextStyle());

        Label instructionsLabel = new Label("| Ctrl+Scroll: Zoom | Space+Drag: Pan | Ctrl+R: Reset View");
        instructionsLabel.setStyle(AppStyles.getStatusTextStyle() + " -fx-text-fill: #7f8c8d;");

        section.getChildren().addAll(statusTitle, statusLabel, instructionsLabel);
        return section;
    }

    private void runSteps(int steps) {
        if (simulationThread != null && simulationThread.isAlive()) {
            statusLabel.setText("Simulation already running.");
            return;
        }
        running = true;
        simulationThread = new Thread(() -> {
            for (int i = 0; i < steps && running; i++) {
                List<Entity> entitiesCopy = List.copyOf(world.getEntities());
                for (Entity entity : entitiesCopy) {
                    entity.update(world, tickspeed, World.SIZE);
                }

                stepCounter++;

                // Update chart every 10 steps
                if (stepCounter % 10 == 0) {
                    Platform.runLater(() -> {
                        Map<Type, Integer> entityCounts = world.countEntitiesInAllBiomes();
                        graphDisplay.updateChart(stepCounter, entityCounts);
                        renderWorld();
                    });
                }

                try {
                    Thread.sleep(tickspeed);
                } catch (InterruptedException e) {
                    break;
                }
            }
            running = false;
            Platform.runLater(() -> statusLabel.setText("Simulation completed " + steps + " steps."));
        });
        simulationThread.setDaemon(true);
        simulationThread.start();
        statusLabel.setText("Running " + steps + " steps...");
    }

    private void stopSimulation() {
        running = false;
        if (simulationThread != null) {
            simulationThread.interrupt();
        }
        statusLabel.setText("Simulation stopped.");
    }

    public void renderWorld() {
        // Use WorldDisplay's renderWorld with entity click handler
        worldDisplay.renderWorld((x, y) -> {
            Entity entity = world.getEntity(x, y);
            if (entity != null) {
                configDisplay.setSelectedEntity(entity);
                statusLabel.setText(String.format("Selected entity at (%d, %d)", x, y));
            } else {
                configDisplay.setSelectedEntity(null);
                statusLabel.setText("No entity at this position");
            }
        });

        // Ensure the scene maintains focus for key events after re-rendering
        if (primaryStage != null && primaryStage.getScene() != null) {
            primaryStage.getScene().getRoot().requestFocus();
        }
    }

    private void enableEntitySelection() {
        // Entity selection is now always enabled through tile click handlers
        statusLabel.setText("Click on any tile to select an entity");
    }

    private void updateSeedDisplay() {
        String seedString = world.getSeedString();
        configDisplay.updateSeedDisplay(seedString);
    }

    private void showEntityInspector(int x, int y) {
        Entity entity = world.getEntity(x, y);
        if (entity != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Entity Inspector");
            alert.setHeaderText("Entity at (" + x + ", " + y + ")");
            alert.setContentText(String.format(
                    "Age: %d\\nEnergy: %.2f\\nSpeed: %.2f\\nMass: %.2f\\nAlive: %s",
                    entity.getAge(), entity.getEnergy(), entity.getSpeed(), entity.getMass(), entity.isAlive()));
            alert.showAndWait();
        } else {
            statusLabel.setText("No entity found at (" + x + ", " + y + ")");
        }
    }

    private void showStatistics() {
        Map<Type, Integer> entityCounts = world.countEntitiesInAllBiomes();
        int totalEntities = entityCounts.values().stream().mapToInt(Integer::intValue).sum();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("World Statistics");
        alert.setHeaderText("Current World State");
        alert.setContentText(String.format(
                "Total Entities: %d\\nGrass Biome: %d\\nForest Biome: %d\\nMountain Biome: %d\\nDesert Biome: %d\\nSteps Completed: %d",
                totalEntities,
                entityCounts.getOrDefault(Type.GRASS, 0),
                entityCounts.getOrDefault(Type.FOREST, 0),
                entityCounts.getOrDefault(Type.MOUNTAIN, 0),
                entityCounts.getOrDefault(Type.DESERT, 0),
                stepCounter));
        alert.showAndWait();
    }

    private void runBatchSimulation(int runs, int steps) {
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Batch Simulation");
        progressAlert.setHeaderText("Running batch simulation...");
        progressAlert.setContentText("Starting " + runs + " simulations with " + steps + " steps each.");

        Thread batchThread = new Thread(() -> {
            BatchSimulation batchSim = new BatchSimulation();
            BatchSimulation.BatchConfiguration config = new BatchSimulation.BatchConfiguration(
                    runs, steps, tickspeed, true, 1, "gui_batch_results_" + System.currentTimeMillis() + ".csv");

            try {
                List<BatchSimulation.SimulationResult> results = batchSim.runBatchSimulation(config, null);

                Platform.runLater(() -> {
                    progressAlert.close();

                    Alert resultAlert = new Alert(Alert.AlertType.INFORMATION);
                    resultAlert.setTitle("Batch Results");
                    resultAlert.setHeaderText("Batch simulation completed");

                    if (!results.isEmpty()) {
                        double avgFinalEntities = results.stream()
                                .mapToInt(BatchSimulation.SimulationResult::getFinalEntityCount).average().orElse(0);
                        double avgSteps = results.stream().mapToInt(BatchSimulation.SimulationResult::getStepsRun)
                                .average().orElse(0);
                        resultAlert.setContentText(String.format(
                                "Completed %d simulations\\nAverage final entities: %.1f\\nAverage steps completed: %.1f\\nResults exported to CSV",
                                results.size(), avgFinalEntities, avgSteps));
                    } else {
                        resultAlert.setContentText("No results generated");
                    }

                    resultAlert.showAndWait();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressAlert.close();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Batch Simulation Error");
                    errorAlert.setContentText("Error running batch simulation: " + e.getMessage());
                    errorAlert.showAndWait();
                });
            }
        });

        batchThread.setDaemon(true);
        batchThread.start();
        progressAlert.showAndWait();
    }

    private void initializeWorld() {
        TextInputDialog seedDialog = new TextInputDialog();
        seedDialog.setTitle("World Seed");
        seedDialog.setHeaderText("Enter World Seed");
        seedDialog.setContentText("Seed (compact format, or leave empty for random):");

        java.util.Optional<String> seedResult = seedDialog.showAndWait();
        if (seedResult.isPresent()) {
            String seedInput = seedResult.get().trim();
            if (seedInput.isEmpty()) {
                world = new World();
                System.out.println("Generated random world: " + world.getWorldSeed().getDescription());
            } else {
                try {
                    world = new World(seedInput);
                    System.out.println("Generated world with seed: " + world.getWorldSeed().getDescription());
                } catch (Exception e) {
                    System.out.println("Invalid seed format, generating random world: " + e.getMessage());
                    world = new World();
                }
            }
        } else {
            world = new World();
            System.out.println("Generated random world: " + world.getWorldSeed().getDescription());
        }
    }

    private int parseIntOr(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
