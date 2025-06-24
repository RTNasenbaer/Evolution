import world.Tile;
import world.World;

public class Display {

    private World world;
    private boolean renderedOnce = false;

    Display(World world) {
        this.world = world;
    }

    public void render() {
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

        String block = "\u001B[48;2;" + r + ";" + g + ";" + b + "m" +
                fgColor + symbol + "\u001B[0m";
        System.out.print(block + " ");
    }
}
