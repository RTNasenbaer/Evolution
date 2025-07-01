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

    public Display(World world) {
        this.world = world;
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
        // Move the cursor up by the grid height to overwrite the previous grid
        // (except for the very first render)
        if (renderedOnce) {
            System.out.print("\033[" + (World.HEIGHT + 1) + "A");
        } else {
            renderedOnce = true;
        }

        // Render the world with entities
        for (int y = 0; y < World.HEIGHT; y++) {
            for (int x = 0; x < World.WIDTH; x++) {
                printTileOrEntity(world, x, y);
            }
            System.out.println();
        }
    }

    /**
     * Prints either the entity (if present) or the tile at the given coordinates.
     * Cleaner, more compact display: entity is '@', food is '•', empty is ' '.
     */
    public static void printTileOrEntity(World world, int x, int y) {
        entities.Entity entity = world.getEntity(x, y);
        Tile tile = world.getTile(x, y);

        String hex = tile.getType().getHex();
        int r = Integer.parseInt(hex.substring(1, 3), 16);
        int g = Integer.parseInt(hex.substring(3, 5), 16);
        int b = Integer.parseInt(hex.substring(5, 7), 16);

        String symbol;
        String fgColor = "";

        if (entity != null) {
            symbol = "@";
            fgColor = "\u001B[38;2;255;255;255m"; // white foreground
        } else if (tile.hasFood()) {
            symbol = "•";
            fgColor = "\u001B[38;2;255;64;64m"; // red foreground for food
        } else {
            symbol = " ";
            fgColor = "";
        }

        String block = "\u001B[48;2;" + r + ";" + g + ";" + b + "m" + fgColor + symbol + "\u001B[0m";
        System.out.print(block + " ");
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
