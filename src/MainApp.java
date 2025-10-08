import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import entities.Entity;
import export.CSVExporter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.AppStyles;
import ui.ConfigDisplay;
import ui.GraphDisplay;
import ui.ResponsiveLayoutManager;
import ui.WorldDisplay;
import world.Type;
import world.World;

public class MainApp extends Application {
    private World world;
    private int tickspeed = 200;
    private Stage primaryStage;
    private Label statusLabel;
    private Thread simulationThread;
    private volatile boolean running = false;
    private int stepCounter = 0;

    // Export tracking
    private boolean exportEntityDetails = false;
    private boolean exportBiomeDetails = false;
    private String entityDetailsFileName;
    private String biomeDetailsFileName;

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

        // Left panel with world display only
        VBox leftPanel = new VBox(AppStyles.SPACING_MEDIUM);
        leftPanel.getChildren().add(worldDisplay.getContainer());

        // Right panel with controls and chart
        VBox rightPanel = new VBox(AppStyles.SPACING_MEDIUM);
        rightPanel.getChildren().addAll(configDisplay.getContainer(), graphDisplay.getContainer());

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

        // Update world display size - now takes full height
        worldDisplay.setPreferredSize(worldDisplayWidth, worldDisplayHeight + chartHeight);

        // Graph now uses control panel width for better fit
        graphDisplay.setPreferredSize(controlPanelWidth, chartHeight);
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

        // Export controls
        configDisplay.exportEntityDetailsBtn.setOnAction(e -> toggleEntityDetailsExport());
        configDisplay.exportBiomeDetailsBtn.setOnAction(e -> toggleBiomeDetailsExport());
    }

    private void toggleEntityDetailsExport() {
        exportEntityDetails = !exportEntityDetails;

        if (exportEntityDetails) {
            entityDetailsFileName = "entity_details_gui_" + System.currentTimeMillis() + ".csv";
            statusLabel.setText("Entity details export ENABLED: " + entityDetailsFileName);
            configDisplay.exportEntityDetailsBtn.setText("Stop Entity Export");
        } else {
            statusLabel.setText("Entity details export DISABLED. Saved to: " + entityDetailsFileName);
            configDisplay.exportEntityDetailsBtn.setText("Export Entity Details");
        }
    }

    private void toggleBiomeDetailsExport() {
        exportBiomeDetails = !exportBiomeDetails;

        if (exportBiomeDetails) {
            biomeDetailsFileName = "biome_details_gui_" + System.currentTimeMillis() + ".csv";
            statusLabel.setText("Biome details export ENABLED: " + biomeDetailsFileName);
            configDisplay.exportBiomeDetailsBtn.setText("Stop Biome Export");
        } else {
            statusLabel.setText("Biome details export DISABLED. Saved to: " + biomeDetailsFileName);
            configDisplay.exportBiomeDetailsBtn.setText("Export Biome Details");
        }
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

                // Export data if enabled
                if (exportEntityDetails) {
                    try {
                        CSVExporter.exportEntityDetails(world, stepCounter, entityDetailsFileName);
                    } catch (IOException ex) {
                        Platform.runLater(() -> statusLabel.setText("Export error: " + ex.getMessage()));
                    }
                }
                if (exportBiomeDetails) {
                    try {
                        CSVExporter.exportBiomeDetails(world, stepCounter, biomeDetailsFileName);
                    } catch (IOException ex) {
                        Platform.runLater(() -> statusLabel.setText("Export error: " + ex.getMessage()));
                    }
                }

                // Update chart and world display every step
                Platform.runLater(() -> {
                    Map<Type, Integer> entityCounts = world.countEntitiesInAllBiomes();
                    graphDisplay.updateChart(stepCounter, entityCounts);
                    renderWorld();
                });

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
            String content = String.format(
                    "Age: %d\nEnergy: %.2f\nSpeed: %.2f\nMass: %.2f\nAlive: %s",
                    entity.getAge(), entity.getEnergy(), entity.getSpeed(), entity.getMass(), entity.isAlive());

            ui.DialogUtils.showInfo(
                    "Entity Inspector",
                    "Entity at (" + x + ", " + y + ")",
                    content);
        } else {
            statusLabel.setText("No entity found at (" + x + ", " + y + ")");
        }
    }

    private void showStatistics() {
        Map<Type, Integer> entityCounts = world.countEntitiesInAllBiomes();
        int totalEntities = entityCounts.values().stream().mapToInt(Integer::intValue).sum();

        String content = String.format(
                "Total Entities: %d\nGrass Biome: %d\nForest Biome: %d\nMountain Biome: %d\nDesert Biome: %d\nSteps Completed: %d",
                totalEntities,
                entityCounts.getOrDefault(Type.GRASS, 0),
                entityCounts.getOrDefault(Type.FOREST, 0),
                entityCounts.getOrDefault(Type.MOUNTAIN, 0),
                entityCounts.getOrDefault(Type.DESERT, 0),
                stepCounter);

        ui.DialogUtils.showInfo("World Statistics", "Current World State", content);
    }

    private void runBatchSimulation(int runs, int steps) {
        ui.DialogUtils.ProgressDialog progressDialog = ui.DialogUtils.showProgress(
                "Batch Simulation",
                "Running batch simulation...");
        progressDialog.updateProgress(0, "Starting " + runs + " simulations with " + steps + " steps each.");
        progressDialog.show();

        Thread batchThread = new Thread(() -> {
            BatchSimulation batchSim = new BatchSimulation();
            BatchSimulation.BatchConfiguration config = new BatchSimulation.BatchConfiguration(
                    runs, steps, tickspeed, true, 1, "gui_batch_results_" + System.currentTimeMillis() + ".csv");

            try {
                List<BatchSimulation.SimulationResult> results = batchSim.runBatchSimulation(config, null);

                Platform.runLater(() -> {
                    progressDialog.close();

                    if (!results.isEmpty()) {
                        double avgFinalEntities = results.stream()
                                .mapToInt(BatchSimulation.SimulationResult::getFinalEntityCount).average().orElse(0);
                        double avgSteps = results.stream().mapToInt(BatchSimulation.SimulationResult::getStepsRun)
                                .average().orElse(0);
                        String content = String.format(
                                "Completed %d simulations\nAverage final entities: %.1f\nAverage steps completed: %.1f\nResults exported to CSV",
                                results.size(), avgFinalEntities, avgSteps);
                        ui.DialogUtils.showSuccess("Batch Results", content);
                    } else {
                        ui.DialogUtils.showInfo("Batch Results", "Batch simulation completed", "No results generated");
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    progressDialog.close();
                    ui.DialogUtils.showError("Batch Simulation Error",
                            "Error running batch simulation: " + e.getMessage());
                });
            }
        });

        batchThread.setDaemon(true);
        batchThread.start();
    }

    private void initializeWorld() {
        // Create custom dialog with text field and browse button
        javafx.scene.control.Dialog<javafx.scene.control.ButtonType> dialog = ui.DialogUtils
                .createCustomDialog("Initialize World", "Enter world seed or browse for file");

        // Content area
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(AppStyles.SPACING_MEDIUM);
        content.setPadding(new javafx.geometry.Insets(AppStyles.PADDING_MEDIUM));

        // Label
        javafx.scene.control.Label label = new javafx.scene.control.Label(
                "Seed (leave empty for random world):");
        label.setStyle(AppStyles.getLabelStyle());

        // Text field and browse button in horizontal layout
        javafx.scene.layout.HBox inputBox = new javafx.scene.layout.HBox(AppStyles.SPACING_SMALL);
        javafx.scene.control.TextField seedField = new javafx.scene.control.TextField();
        seedField.setPromptText("Enter seed or browse file...");
        seedField.setPrefWidth(300);
        seedField.setStyle(AppStyles.getTextFieldStyle());

        javafx.scene.control.Button browseBtn = new javafx.scene.control.Button("Browse...");
        browseBtn.setStyle(AppStyles.getSecondaryButtonStyle());
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import World Seed");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("World Seed Files", "*.seed"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try (Scanner scanner = new Scanner(file)) {
                    String seedString = scanner.nextLine().trim();
                    seedField.setText(seedString);
                } catch (IOException ex) {
                    ui.DialogUtils.showError("Import Failed", "Could not read file: " + ex.getMessage());
                }
            }
        });

        inputBox.getChildren().addAll(seedField, browseBtn);
        content.getChildren().addAll(label, inputBox);

        dialog.getDialogPane().setContent(content);

        // Add OK and Cancel buttons
        dialog.getDialogPane().getButtonTypes().addAll(
                javafx.scene.control.ButtonType.OK,
                javafx.scene.control.ButtonType.CANCEL);

        // Style buttons
        javafx.scene.control.Button okBtn = (javafx.scene.control.Button) dialog.getDialogPane()
                .lookupButton(javafx.scene.control.ButtonType.OK);
        javafx.scene.control.Button cancelBtn = (javafx.scene.control.Button) dialog.getDialogPane()
                .lookupButton(javafx.scene.control.ButtonType.CANCEL);

        if (okBtn != null)
            okBtn.setStyle(AppStyles.getPrimaryButtonStyle());
        if (cancelBtn != null)
            cancelBtn.setStyle(AppStyles.getSecondaryButtonStyle());

        // Show dialog and process result
        java.util.Optional<javafx.scene.control.ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            String seedInput = seedField.getText().trim();
            if (!seedInput.isEmpty()) {
                try {
                    world = new World(seedInput);
                    System.out.println("Generated world with seed: " + world.getWorldSeed().getDescription());
                } catch (Exception e) {
                    System.out.println("Invalid seed format, generating random world: " + e.getMessage());
                    ui.DialogUtils.showError("Invalid Seed", "Could not parse seed, using random world");
                    world = new World();
                }
            } else {
                world = new World();
                System.out.println("Generated random world: " + world.getWorldSeed().getDescription());
            }
        } else {
            // Cancelled - generate random world as default
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
