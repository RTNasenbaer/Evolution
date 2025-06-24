import javafx.application.Application;
import javafx.stage.Stage;
import world.Tile;
import world.World;

public class Display extends Application {

    private World world;

    Display(World world) {
        this.world = world;
    }

    public void render() {
        // Clear the console
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // Render the world
        for (int y = 0; y < world.HEIGHT; y++) {
            for (int x = 0; x < world.WIDTH; x++) {
                printColoredBlock(world.getTile(x, y));
            }
            System.out.println();
        }
    }

    public static void printColoredBlock(Tile tile) {
        String hex = tile.getType().getHex();
        // Convert hex to RGB
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        // ANSI escape for 24-bit color
        String block = "\u001B[48;2;" + r + ";" + g + ";" + b + "m \u001B[0m";
        if (tile.hasFood()) block += "\u001B[48;2;255;0;0m \u001B[0m";
        else block = block.repeat(2);

        System.out.print(block);
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
