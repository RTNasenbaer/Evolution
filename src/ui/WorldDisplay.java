package ui;

import entities.Entity;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import world.Tile;
import world.World;

/**
 * Component responsible for displaying the world simulation grid.
 * Handles rendering of entities and terrain in a visual grid format.
 * Supports container-based zoom and pan functionality.
 */
public class WorldDisplay {
    private GridPane gridPane;
    private VBox container;
    private Label titleLabel;
    private World world;
    private int tileSize = 12;

    // Container-based zoom/pan components
    private javafx.scene.layout.StackPane viewContainer;
    private javafx.scene.Group worldGroup;
    private javafx.scene.shape.Rectangle clipRectangle;
    private javafx.scene.transform.Translate panTransform = new javafx.scene.transform.Translate();
    private javafx.scene.transform.Scale zoomTransform = new javafx.scene.transform.Scale();

    // Pan state variables
    private double lastPanX = 0;
    private double lastPanY = 0;
    private boolean isPanning = false;
    private boolean spacePressed = false;
    private double zoomFactor = 1.0;
    private final double minZoom = 0.5;
    private final double maxZoom = 5.0;
    private final double zoomStep = 0.1;

    public WorldDisplay(World world) {
        this.world = world;
        initializeComponents();
    }

    private void initializeComponents() {
        container = new VBox(AppStyles.SPACING_MEDIUM);
        container.setStyle(AppStyles.getCardStyle());

        titleLabel = new Label("World Simulation");
        titleLabel.setStyle(AppStyles.getTitleStyle());

        gridPane = new GridPane();
        gridPane.setStyle("-fx-border-color: " + AppStyles.BORDER_COLOR + "; -fx-border-width: 1;");

        // Create container-based zoom/pan system (like WorldBuilderNew)
        viewContainer = new javafx.scene.layout.StackPane();
        viewContainer.setStyle("-fx-background-color: " + AppStyles.BACKGROUND_COLOR + "; " +
                "-fx-border-color: " + AppStyles.BORDER_COLOR + "; " +
                "-fx-border-width: 1; -fx-border-radius: 5;");

        // Create a group to hold the world grid for transformations
        worldGroup = new javafx.scene.Group();
        worldGroup.getChildren().add(gridPane);

        // Add clipping to prevent content from overflowing
        clipRectangle = new javafx.scene.shape.Rectangle();
        viewContainer.setClip(clipRectangle);

        viewContainer.getChildren().add(worldGroup);

        // Setup zoom and pan functionality
        setupZoomPanOnContainer();

        container.getChildren().addAll(titleLabel, viewContainer);
    }

    private void setupZoomPanOnContainer() {
        // Add transforms to the group
        worldGroup.getTransforms().addAll(zoomTransform, panTransform);

        // Set initial transform values
        zoomTransform.setX(1.0);
        zoomTransform.setY(1.0);
        panTransform.setX(0);
        panTransform.setY(0);

        // Update clip size when container size changes
        viewContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            clipRectangle.setWidth(newVal.doubleValue());
        });
        viewContainer.heightProperty().addListener((obs, oldVal, newVal) -> {
            clipRectangle.setHeight(newVal.doubleValue());
        });

        // Zoom with Ctrl+Scroll
        worldGroup.setOnScroll(event -> {
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
        worldGroup.setOnMousePressed(event -> {
            if (spacePressed) {
                isPanning = true;
                lastPanX = event.getSceneX();
                lastPanY = event.getSceneY();
                viewContainer.setCursor(javafx.scene.Cursor.MOVE);
                event.consume();
            }
        });

        worldGroup.setOnMouseDragged(event -> {
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

        worldGroup.setOnMouseReleased(event -> {
            if (spacePressed && isPanning) {
                isPanning = false;
                viewContainer.setCursor(javafx.scene.Cursor.DEFAULT);
                event.consume();
            }
        });
    }

    /**
     * Creates a tile label for JavaFX display with proper styling
     */
    public Label createTileLabel(World world, int x, int y) {
        Entity entity = world.getEntity(x, y);
        Tile tile = world.getTile(x, y);

        String hex = tile.getType().getHex();
        String symbol;
        Color fgColor = Color.BLACK;

        if (entity != null) {
            symbol = "●";
            fgColor = Color.WHITE;
        } else if (tile.hasFood()) {
            symbol = "•";
            fgColor = Color.RED;
        } else {
            symbol = "";
        }

        Label label = new Label(symbol);
        label.setFont(Font.font("Monospaced", Math.max(8, tileSize - 4)));
        label.setMinSize(tileSize, tileSize);
        label.setMaxSize(tileSize, tileSize);
        label.setPrefSize(tileSize, tileSize);
        label.setStyle(String.format(
                "-fx-background-color: %s; -fx-alignment: center; " +
                        "-fx-border-color: %s; -fx-border-width: 0.5;",
                hex, AppStyles.BORDER_COLOR));
        label.setTextFill(fgColor);

        return label;
    }

    /**
     * Creates a combined tile label for block rendering with entity count
     */
    public Label createCombinedTileLabel(World world, int startX, int startY, int blockSize) {
        int entityCount = 0;
        boolean containsFood = false;
        String hexColor = null;

        // Sample the block area
        for (int dx = 0; dx < blockSize && startX + dx < World.SIZE; dx++) {
            for (int dy = 0; dy < blockSize && startY + dy < World.SIZE; dy++) {
                int x = startX + dx;
                int y = startY + dy;

                Entity entity = world.getEntity(x, y);
                Tile tile = world.getTile(x, y);

                if (entity != null)
                    entityCount++;
                if (tile != null && tile.hasFood())
                    containsFood = true;
                if (tile != null && hexColor == null)
                    hexColor = tile.getType().getHex();
            }
        }

        String symbol;
        Color fgColor = Color.BLACK;

        if (entityCount > 0) {
            symbol = entityCount == 1 ? "●" : String.valueOf(entityCount);
            fgColor = Color.WHITE;
        } else if (containsFood) {
            symbol = "•";
            fgColor = Color.RED;
        } else {
            symbol = "";
        }

        if (hexColor == null)
            hexColor = "#000000";

        Label label = new Label(symbol);
        label.setFont(Font.font("Monospaced", Math.max(8, tileSize - 4)));
        label.setStyle(String.format(
                "-fx-background-color: %s; -fx-alignment: center; " +
                        "-fx-border-color: %s; -fx-border-width: 0.5;",
                hexColor, AppStyles.BORDER_COLOR));
        label.setTextFill(fgColor);

        return label;
    }

    /**
     * Renders the complete world grid with click handlers
     */
    public void renderWorld(java.util.function.BiConsumer<Integer, Integer> onTileClick) {
        gridPane.getChildren().clear();

        for (int y = 0; y < World.SIZE; y++) {
            for (int x = 0; x < World.SIZE; x++) {
                Label tileLabel = createTileLabel(world, x, y);

                // Add click handler if provided
                if (onTileClick != null) {
                    final int finalX = x;
                    final int finalY = y;
                    tileLabel.setOnMouseClicked(event -> onTileClick.accept(finalX, finalY));
                }

                gridPane.add(tileLabel, x, y);
            }
        }
    }

    public VBox getContainer() {
        return container;
    }

    public GridPane getGridPane() {
        return gridPane;
    }

    public void updateDisplay() {
        // Trigger a re-render without click handlers
        renderWorld(null);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPreferredSize(double width, double height) {
        if (viewContainer != null) {
            viewContainer.setPrefSize(width, height);
            viewContainer.setMaxSize(width, height);
        }
    }

    public void setTileSize(int size) {
        this.tileSize = Math.max(8, size);
        // Re-render if world exists
        if (world != null) {
            renderWorld(null);
        }
    }

    public int getTileSize() {
        return tileSize;
    }

    public javafx.scene.layout.StackPane getViewContainer() {
        return viewContainer;
    }

    public javafx.scene.Group getWorldGroup() {
        return worldGroup;
    }

    /**
     * Sets up key event handlers for zoom and pan functionality
     */
    public void setupKeyHandlers(javafx.scene.Scene scene) {
        if (scene != null) {
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.SPACE) {
                    spacePressed = true;
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
                    event.consume();
                }
            });
        }
    }

    /**
     * Resets zoom to 1.0 and centers the view
     */
    public void resetView() {
        zoomFactor = 1.0;
        zoomTransform.setX(1.0);
        zoomTransform.setY(1.0);
        panTransform.setX(0);
        panTransform.setY(0);
    }
}