import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Scanner;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ui.AppStyles;
import ui.ResponsiveLayoutManager;
import ui.WorldBuilderControls;
import world.Type;
import world.World;
import world.WorldSeed;

public class WorldBuilderNew extends Application {
    private static final int GRID_SIZE = World.SIZE;
    private static final int TILE_SIZE = 12;

    private WorldSeed worldSeed;
    private GridPane worldGrid;
    private Label coordinatesLabel;
    private boolean isDragging = false;

    // UI Components
    private WorldBuilderControls controlsDisplay;
    private ResponsiveLayoutManager layoutManager;

    // Pan state variables
    private double lastPanX = 0;
    private double lastPanY = 0;
    private boolean isPanning = false;
    private boolean spacePressed = false;
    private double zoomFactor = 1.0;
    private final double minZoom = 0.5;
    private final double maxZoom = 5.0;
    private final double zoomStep = 0.1;

    // Container-based pan/zoom components
    private javafx.scene.layout.StackPane viewContainer;
    private javafx.scene.Group worldGroup;
    private javafx.scene.shape.Rectangle clipRectangle;
    private javafx.scene.transform.Translate panTransform = new javafx.scene.transform.Translate();
    private javafx.scene.transform.Scale zoomTransform = new javafx.scene.transform.Scale();

    @Override
    public void start(Stage primaryStage) {
        initializeEmptyWorld();
        initializeComponents(primaryStage);
        setupLayout(primaryStage);
        setupEventHandlers();

        updateSeedDisplay();

        primaryStage.setTitle("Evolution World Builder");
        primaryStage.show();
    }

    private void initializeComponents(Stage primaryStage) {
        controlsDisplay = new WorldBuilderControls();
        layoutManager = new ResponsiveLayoutManager(primaryStage);
        coordinatesLabel = new Label("Position: hover over tiles");
    }

    private void setupLayout(Stage primaryStage) {
        // Main layout with responsive design
        VBox root = new VBox(AppStyles.SPACING_MEDIUM);
        root.setPadding(new Insets(AppStyles.PADDING_MEDIUM));
        root.setStyle(AppStyles.getBackgroundStyle());

        // Create main content area
        HBox mainContent = new HBox(AppStyles.SPACING_MEDIUM);

        // Left panel with world editor
        VBox leftPanel = createWorldEditorSection();

        // Right panel with controls
        VBox rightPanel = controlsDisplay.getContainer();

        // Set responsive sizes
        updateComponentSizes();

        mainContent.getChildren().addAll(leftPanel, rightPanel);

        // Status section
        HBox statusSection = createStatusBar();

        root.getChildren().addAll(mainContent, statusSection);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);

        // Setup zoom/pan key handlers
        setupZoomPanHandlers(scene);

        // Now that scene is set up, update component sizes
        updateComponentSizes();

        // Add resize listener for responsive behavior
        scene.widthProperty().addListener((obs, oldVal, newVal) -> updateComponentSizes());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> updateComponentSizes());
    }

    private VBox createWorldEditorSection() {
        VBox section = new VBox(AppStyles.SPACING_MEDIUM);
        section.setStyle(AppStyles.getCardStyle());

        Label title = new Label("World Editor");
        title.setStyle(AppStyles.getTitleStyle());

        worldGrid = createWorldGrid();
        ScrollPane scrollPane = new ScrollPane(worldGrid);
        scrollPane.setStyle("-fx-background: " + AppStyles.BACKGROUND_COLOR + "; -fx-border-color: " +
                AppStyles.BORDER_COLOR + "; -fx-border-width: 1; -fx-border-radius: 5;");

        worldGrid = createWorldGrid();

        // Create a clipping container for the world view
        javafx.scene.layout.StackPane viewContainer = new javafx.scene.layout.StackPane();
        viewContainer.setStyle("-fx-background-color: " + AppStyles.BACKGROUND_COLOR + "; " +
                "-fx-border-color: " + AppStyles.BORDER_COLOR + "; " +
                "-fx-border-width: 1; -fx-border-radius: 5;");

        // Create a group to hold the world grid for transformations
        javafx.scene.Group worldGroup = new javafx.scene.Group();
        worldGroup.getChildren().add(worldGrid);

        // Add clipping to prevent content from overflowing
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        viewContainer.setClip(clip);

        viewContainer.getChildren().add(worldGroup);

        // Setup zoom and pan functionality
        setupZoomPanOnContainer(viewContainer, worldGroup, clip);

        // Store references for later use
        this.viewContainer = viewContainer;
        this.worldGroup = worldGroup;
        this.clipRectangle = clip;

        section.getChildren().addAll(title, viewContainer);
        return section;
    }

    private void updateComponentSizes() {
        double controlPanelWidth = layoutManager.getOptimalControlPanelWidth();
        double worldDisplayWidth = layoutManager.getOptimalWorldDisplayWidth();
        double worldDisplayHeight = layoutManager.getOptimalWorldDisplayHeight();

        controlsDisplay.setPreferredWidth(controlPanelWidth);

        // Update view container size if it's available
        if (viewContainer != null) {
            viewContainer.setPrefSize(worldDisplayWidth, worldDisplayHeight);
            viewContainer.setMaxSize(worldDisplayWidth, worldDisplayHeight);
        }
    }

    private void setupEventHandlers() {
        // Tool button handlers
        controlsDisplay.clearBtn.setOnAction(e -> {
            initializeEmptyWorld();
            updateWorldGrid();
            updateSeedDisplay();
        });

        controlsDisplay.randomBtn.setOnAction(e -> {
            generateRandomWorld();
            updateWorldGrid();
            updateSeedDisplay();
        });

        controlsDisplay.saveBtn.setOnAction(e -> saveWorld());
        controlsDisplay.loadBtn.setOnAction(e -> loadWorld());
        controlsDisplay.exportSeedBtn.setOnAction(e -> exportAsSeed());
        controlsDisplay.importSeedBtn.setOnAction(e -> importFromSeed());
    }

    private void initializeEmptyWorld() {
        Type[][] initialBiomes = new Type[GRID_SIZE][GRID_SIZE];
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                initialBiomes[x][y] = Type.GRASS;
            }
        }
        worldSeed = new WorldSeed(initialBiomes);
    }

    private GridPane createWorldGrid() {
        GridPane grid = new GridPane();
        grid.setStyle("-fx-border-color: black;");

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                Label tile = createTile(x, y);
                grid.add(tile, x, y);
            }
        }

        // Add mouse handlers to the grid itself for dragging
        grid.setOnMouseDragged(e -> {
            if (isDragging && !spacePressed) {
                // Get the mouse position relative to the scaled grid
                double sceneX = e.getSceneX();
                double sceneY = e.getSceneY();

                // Convert scene coordinates to grid coordinates
                javafx.geometry.Bounds gridBounds = grid.localToScene(grid.getBoundsInLocal());
                double relativeX = (sceneX - gridBounds.getMinX()) / zoomFactor;
                double relativeY = (sceneY - gridBounds.getMinY()) / zoomFactor;

                int x = (int) Math.floor(relativeX / TILE_SIZE);
                int y = (int) Math.floor(relativeY / TILE_SIZE);

                if (x >= 0 && x < GRID_SIZE && y >= 0 && y < GRID_SIZE) {
                    paintTile(x, y);
                }
                e.consume();
            }
        });

        // Global mouse release handler to stop dragging
        grid.setOnMouseReleased(e -> {
            if (!spacePressed && !isPanning) {
                isDragging = false;
                e.consume();
            }
        });

        return grid;
    }

    private Label createTile(int x, int y) {
        Label tile = new Label();
        tile.setMinSize(TILE_SIZE, TILE_SIZE);
        tile.setMaxSize(TILE_SIZE, TILE_SIZE);
        tile.setStyle("-fx-background-color: " + worldSeed.getBiome(x, y).getHex() +
                "; -fx-border-color: gray; -fx-border-width: 0.5;");

        tile.setOnMousePressed(e -> {
            // Only start painting if space is not pressed (not in pan mode)
            if (!spacePressed && !isPanning) {
                isDragging = true;
                paintTile(x, y);
                e.consume();
            }
        });

        tile.setOnMouseReleased(e -> {
            if (!spacePressed && !isPanning) {
                isDragging = false;
                e.consume();
            }
        });

        tile.setOnMouseEntered(e -> {
            coordinatesLabel.setText("Position: (" + x + ", " + y + ") - " + worldSeed.getBiome(x, y).name());
            if (isDragging && !spacePressed && !isPanning) {
                paintTile(x, y);
            }
        });

        return tile;
    }

    private void paintTile(int x, int y) {
        paintWithBrush(x, y);
    }

    private void paintWithBrush(int centerX, int centerY) {
        int brushSize = controlsDisplay.getBrushSize();
        WorldBuilderControls.BrushShape brushShape = controlsDisplay.getBrushShape();
        Type selectedBiome = controlsDisplay.getSelectedBiome();

        int radius = brushSize / 2;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int paintX = centerX + dx;
                int paintY = centerY + dy;

                if (paintX >= 0 && paintX < GRID_SIZE && paintY >= 0 && paintY < GRID_SIZE) {
                    boolean shouldPaint = false;

                    if (brushShape == WorldBuilderControls.BrushShape.SQUARE) {
                        shouldPaint = true;
                    } else if (brushShape == WorldBuilderControls.BrushShape.CIRCLE) {
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        shouldPaint = distance <= radius;
                    }

                    if (shouldPaint && worldSeed.getBiome(paintX, paintY) != selectedBiome) {
                        worldSeed.setBiome(paintX, paintY, selectedBiome);
                        updateTile(paintX, paintY);
                    }
                }
            }
        }
    }

    private void updateTile(int x, int y) {
        Label tile = (Label) worldGrid.getChildren().get(y * GRID_SIZE + x);
        tile.setStyle("-fx-background-color: " + worldSeed.getBiome(x, y).getHex() +
                "; -fx-border-color: gray; -fx-border-width: 0.5;");
    }

    private void updateWorldGrid() {
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                updateTile(x, y);
            }
        }
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(20);
        statusBar.setStyle(AppStyles.getStatusBarStyle());

        Label seedLabel = new Label();
        seedLabel.setStyle(AppStyles.getStatusTextStyle() + " -fx-font-family: 'Consolas', monospace;");

        coordinatesLabel.setStyle(AppStyles.getStatusTextStyle());

        Label instructionsLabel = new Label("| Ctrl+Scroll: Zoom | Space+Drag: Pan | Ctrl+R: Reset View");
        instructionsLabel.setStyle(AppStyles.getStatusTextStyle() + " -fx-text-fill: #7f8c8d;");

        statusBar.getChildren().addAll(seedLabel, coordinatesLabel, instructionsLabel);
        return statusBar;
    }

    private void updateSeedDisplay() {
        String seedString = worldSeed.toSeedString();
        String description = worldSeed.getDescription();
        controlsDisplay.updateSeedDisplay(seedString, description);
    }

    private void generateRandomWorld() {
        worldSeed = new WorldSeed(System.currentTimeMillis());
        updateWorldGrid();
        updateSeedDisplay();
    }

    private void saveWorld() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save World");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("World Seed Files", "*.seed"));

        File file = fileChooser.showSaveDialog(worldGrid.getScene().getWindow());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(worldSeed.toSeedString());
                showAlert("Success", "World seed saved successfully!");
            } catch (IOException e) {
                showAlert("Error", "Failed to save world seed: " + e.getMessage());
            }
        }
    }

    private void loadWorld() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load World");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("World Seed Files", "*.seed"),
                new FileChooser.ExtensionFilter("Legacy World Files", "*.world"));

        File file = fileChooser.showOpenDialog(worldGrid.getScene().getWindow());
        if (file != null) {
            try {
                if (file.getName().endsWith(".seed")) {
                    try (Scanner scanner = new Scanner(file)) {
                        String seedString = scanner.nextLine();
                        worldSeed = WorldSeed.fromSeedString(seedString);
                    }
                } else {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                        int[][] loadedMap = (int[][]) ois.readObject();
                        Type[][] biomeData = arrayToBiomeMap(loadedMap);
                        worldSeed = new WorldSeed(biomeData);
                    }
                }
                updateWorldGrid();
                updateSeedDisplay();
                showAlert("Success", "World loaded successfully!");
            } catch (IOException | ClassNotFoundException e) {
                showAlert("Error", "Failed to load world: " + e.getMessage());
            }
        }
    }

    private void exportAsSeed() {
        String seedString = worldSeed.toSeedString();

        TextInputDialog dialog = new TextInputDialog(seedString);
        dialog.setTitle("Export Seed");
        dialog.setHeaderText("World Seed Generated");
        dialog.setContentText("Copy this seed to use in the main application:\\n\\n" +
                worldSeed.getDescription() + "\\n\\n" +
                "Seed String: " + seedString);

        dialog.showAndWait();
    }

    private void importFromSeed() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Import Seed");
        dialog.setHeaderText("Import World from Seed");
        dialog.setContentText("Enter seed string:");

        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            try {
                worldSeed = WorldSeed.fromSeedString(result.get().trim());
                updateWorldGrid();
                updateSeedDisplay();
                showAlert("Success", "World imported successfully!\\n" + worldSeed.getDescription());
            } catch (Exception e) {
                showAlert("Error", "Failed to import seed: " + e.getMessage());
            }
        }
    }

    private Type[][] arrayToBiomeMap(int[][] array) {
        Type[][] biomeData = new Type[GRID_SIZE][GRID_SIZE];
        Type[] types = Type.values();
        for (int x = 0; x < GRID_SIZE; x++) {
            for (int y = 0; y < GRID_SIZE; y++) {
                if (array[x][y] >= 0 && array[x][y] < types.length) {
                    biomeData[x][y] = types[array[x][y]];
                } else {
                    biomeData[x][y] = Type.GRASS;
                }
            }
        }
        return biomeData;
    }

    private ScrollPane currentScrollPane;

    private void setupZoomPanOnContainer(javafx.scene.layout.StackPane container, javafx.scene.Group group,
            javafx.scene.shape.Rectangle clip) {
        // Add transforms to the group
        group.getTransforms().addAll(zoomTransform, panTransform);

        // Set initial transform values
        zoomTransform.setX(1.0);
        zoomTransform.setY(1.0);
        panTransform.setX(0);
        panTransform.setY(0);

        // Update clip size when container size changes
        container.widthProperty().addListener((obs, oldVal, newVal) -> {
            clip.setWidth(newVal.doubleValue());
        });
        container.heightProperty().addListener((obs, oldVal, newVal) -> {
            clip.setHeight(newVal.doubleValue());
        });

        // Zoom with Ctrl+Scroll
        group.setOnScroll(event -> {
            if (event.isControlDown()) {
                event.consume();

                double deltaY = event.getDeltaY();
                double scaleFactor = (deltaY > 0) ? (1 + zoomStep) : (1 - zoomStep);

                double newZoom = zoomFactor * scaleFactor;
                newZoom = Math.max(minZoom, Math.min(maxZoom, newZoom));

                if (newZoom != zoomFactor) {
                    zoomFactor = newZoom;
                    zoomTransform.setX(zoomFactor);
                    zoomTransform.setY(zoomFactor);
                }
            }
        });

        // Manual panning implementation
        group.setOnMousePressed(event -> {
            if (spacePressed) {
                isPanning = true;
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                container.setCursor(javafx.scene.Cursor.MOVE);
                event.consume();
            }
        });

        group.setOnMouseDragged(event -> {
            if (spacePressed && isPanning) {
                double deltaX = event.getSceneX() - lastPanX;
                double deltaY = event.getSceneY() - lastPanY;

                // Apply pan translation
                panTransform.setX(panTransform.getX() + deltaX);
                panTransform.setY(panTransform.getY() + deltaY);

                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                event.consume();
            }
        });

        group.setOnMouseReleased(event -> {
            if (spacePressed && isPanning) {
                isPanning = false;
                container.setCursor(javafx.scene.Cursor.DEFAULT);
                event.consume();
            }
        });
    }

    private void setupZoomPanHandlers(Scene scene) {
        // Set up key handlers for zoom and pan functionality
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                spacePressed = true;
                coordinatesLabel.setText("Pan mode: Hold Space and drag to move around");
                event.consume();
            } else if (event.getCode() == javafx.scene.input.KeyCode.R && event.isControlDown()) {
                // Reset zoom and pan
                zoomFactor = 1.0;
                zoomTransform.setX(1.0);
                zoomTransform.setY(1.0);
                panTransform.setX(0);
                panTransform.setY(0);
                coordinatesLabel.setText("View reset to center");
                event.consume();
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                spacePressed = false;
                isPanning = false;
                if (viewContainer != null) {
                    viewContainer.setCursor(javafx.scene.Cursor.DEFAULT);
                }
                coordinatesLabel.setText("Position: hover over tiles");
                event.consume();
            }
        });

        // Make sure the scene can receive key events
        scene.getRoot().setFocusTraversable(true);
        scene.getRoot().requestFocus();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}