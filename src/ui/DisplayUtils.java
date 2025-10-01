package ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import world.World;

/**
 * Display utilities for various rendering modes and contexts.
 * Provides factory methods and utilities for different display scenarios.
 */
public class DisplayUtils {

    /**
     * Display modes for different contexts
     */
    public enum DisplayMode {
        TERMINAL, JAVAFX, HEADLESS
    }

    /**
     * Creates a standalone JavaFX window for world display
     */
    public static void createStandaloneWindow(World world, String title) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            WorldDisplay worldDisplay = new WorldDisplay(world);
            worldDisplay.renderWorld(null); // No click handlers for standalone

            Scene scene = new Scene(worldDisplay.getContainer());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        });
    }

    /**
     * Renders world to terminal using TerminalDisplay
     */
    public static void renderToTerminal(World world) {
        TerminalDisplay.renderWorld(world);
    }

    /**
     * Prints world statistics to terminal
     */
    public static void printStatistics(World world) {
        TerminalDisplay.printStatistics(world);
    }

    /**
     * Creates a minimal grid pane for embedding in other layouts
     */
    public static GridPane createMinimalGrid(World world, int tileSize) {
        WorldDisplay display = new WorldDisplay(world);
        display.setTileSize(tileSize);
        display.renderWorld(null);
        return display.getGridPane();
    }
}