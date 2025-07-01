import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import world.Tile;
import world.World;

public class Display {

    public enum DisplayMode {
        TERMINAL, JAVAFX
    }

    private World world;
    private boolean renderedOnce = false;
    private DisplayMode mode = DisplayMode.JAVAFX;
    private static final int MAX_TERM_GRID_SIZE = 20;

    public Display(World world, DisplayMode mode) {
        this.world = world;
        this.mode = mode;
    }

    public void setMode(DisplayMode mode) {
        this.mode = mode;
    }

    public void render() {
        if (mode == DisplayMode.TERMINAL) {
            renderTerminal();
        }
        // For JavaFX, call renderJavaFX(Stage) from your Application class
    }

    private void renderTerminal() {
        int worldWidth = world.getWidth();
        int worldHeight = world.getHeight();
        int gridSize = Math.min(MAX_TERM_GRID_SIZE, Math.min(worldWidth, worldHeight));
        int cellWidth = (int) Math.ceil((double) worldWidth / gridSize);
        int cellHeight = (int) Math.ceil((double) worldHeight / gridSize);
        int terminalGridWidth = (int) Math.ceil((double) worldWidth / cellWidth);
        int terminalGridHeight = (int) Math.ceil((double) worldHeight / cellHeight);
        // Double buffering: build the grid in a StringBuilder, then print once
        StringBuilder buffer = new StringBuilder();
        // Hide cursor for smooth rendering
        buffer.append("\033[?25l");
        // Clear the terminal and move cursor to top
        buffer.append("\033[2J\033[H");
        for (int row = 0; row < terminalGridHeight; row++) {
            for (int col = 0; col < terminalGridWidth; col++) {
                buffer.append(getTerminalCellBlock(world, col * cellWidth, row * cellHeight, cellWidth, cellHeight, col == terminalGridWidth - 1));
            }
            buffer.append("\u001B[0m\n"); // Reset color at end of line
        }
        // Show cursor again
        buffer.append("\033[?25h");
        System.out.print(buffer.toString());
        System.out.flush();
        // Optional: add a tiny delay to reduce flicker on some terminals
        try { Thread.sleep(5); } catch (InterruptedException ignored) {}
    }

    /**
     * Returns a summary cell for a region of the world as a string with ANSI codes.
     * If any entity is present, show '@'.
     * Else if any food is present, show '•'.
     * Else show the dominant tile type color.
     * The lastCellInRow flag ensures no extra space is printed at the end of the line.
     */
    public static String getTerminalCellBlock(World world, int startX, int startY, int cellWidth, int cellHeight, boolean isLastCellInRow) {
        boolean containsEntity = false;
        boolean containsFood = false;
        String hexColor = null;
        for (int dx = 0; dx < cellWidth; dx++) {
            for (int dy = 0; dy < cellHeight; dy++) {
                int x = startX + dx;
                int y = startY + dy;
                if (x >= world.getWidth() || y >= world.getHeight()) continue;
                entities.Entity entity = world.getEntity(x, y);
                Tile tile = world.getTile(x, y);
                if (entity != null) containsEntity = true;
                if (tile != null && tile.hasFood()) containsFood = true;
                if (tile != null && hexColor == null) hexColor = tile.getType().getHex();
            }
        }
        String symbol;
        String fgAnsi = "";
        if (containsEntity) {
            symbol = "@";
            fgAnsi = "\u001B[38;2;255;255;255m";
        } else if (containsFood) {
            symbol = "•";
            fgAnsi = "\u001B[38;2;255;64;64m";
        } else {
            symbol = " ";
            fgAnsi = "";
        }
        if (hexColor == null) hexColor = "#000000";
        int r = Integer.parseInt(hexColor.substring(1, 3), 16);
        int g = Integer.parseInt(hexColor.substring(3, 5), 16);
        int b = Integer.parseInt(hexColor.substring(5, 7), 16);
        String block = "\u001B[48;2;" + r + ";" + g + ";" + b + "m" + fgAnsi + symbol + "\u001B[0m";
        return block + (isLastCellInRow ? "" : " ");
    }

    // --- JavaFX rendering ---

    public void renderJavaFX(Stage stage) {
        Platform.runLater(() -> {
            GridPane grid = new GridPane();
            for (int y = 0; y < World.HEIGHT; y++) {
                for (int x = 0; x < World.WIDTH; x++) {
                    Label label = createTileLabel(world, x, y);
                    grid.add(label, x, y);
                }
            }
            Scene scene = new Scene(grid);
            stage.setScene(scene);
            stage.setTitle("Evolution World");
            stage.show();
        });
    }

    public Label createTileLabel(World world, int x, int y) {
        entities.Entity entity = world.getEntity(x, y);
        Tile tile = world.getTile(x, y);

        String hex = tile.getType().getHex();
        Color bgColor = Color.web(hex);

        String symbol;
        Color fgColor = Color.BLACK;

        if (entity != null) {
            symbol = "@";
            fgColor = Color.WHITE;
        } else if (tile.hasFood()) {
            symbol = "•";
            fgColor = Color.RED;
        } else {
            symbol = " ";
        }

        Label label = new Label(symbol);
        label.setFont(Font.font("Monospaced", 18));
        label.setMinSize(24, 24);
        label.setMaxSize(24, 24);
        label.setStyle("-fx-background-color: " + hex + "; -fx-alignment: center;");
        label.setTextFill(fgColor);
        return label;
    }
}
