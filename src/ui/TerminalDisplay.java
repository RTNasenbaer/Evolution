package ui;

import entities.Entity;
import world.Tile;
import world.World;

/**
 * Terminal-based display utility for world rendering.
 * Provides ANSI-colored terminal output for debugging and lightweight display.
 */
public class TerminalDisplay {
    private static final int MAX_TERM_GRID_SIZE = 20;

    /**
     * Renders the world to terminal with ANSI colors
     */
    public static void renderWorld(World world) {
        int worldWidth = world.getSize();
        int worldHeight = world.getSize();
        int gridSize = Math.min(MAX_TERM_GRID_SIZE, Math.min(worldWidth, worldHeight));
        int cellWidth = (int) Math.ceil((double) worldWidth / gridSize);
        int cellHeight = (int) Math.ceil((double) worldHeight / gridSize);
        int terminalGridWidth = (int) Math.ceil((double) worldWidth / cellWidth);
        int terminalGridHeight = (int) Math.ceil((double) worldHeight / cellHeight);

        // Double buffering: build the grid in a StringBuilder, then print once
        StringBuilder buffer = new StringBuilder();

        // Hide cursor for smooth rendering
        buffer.append("\\033[?25l");
        // Clear the terminal and move cursor to top
        buffer.append("\\033[2J\\033[H");

        for (int row = 0; row < terminalGridHeight; row++) {
            for (int col = 0; col < terminalGridWidth; col++) {
                buffer.append(getTerminalCellBlock(world, col * cellWidth, row * cellHeight,
                        cellWidth, cellHeight, col == terminalGridWidth - 1));
            }
            buffer.append("\\u001B[0m\\n"); // Reset color at end of line
        }

        // Show cursor again
        buffer.append("\\033[?25h");
        System.out.print(buffer.toString());
        System.out.flush();

        // Optional: add a tiny delay to reduce flicker on some terminals
        try {
            Thread.sleep(5);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Returns a summary cell for a region of the world as a string with ANSI codes.
     * If any entity is present, show '@'.
     * Else if any food is present, show '•'.
     * Else show the dominant tile type color.
     */
    private static String getTerminalCellBlock(World world, int startX, int startY,
            int cellWidth, int cellHeight, boolean isLastCellInRow) {
        boolean containsEntity = false;
        boolean containsFood = false;
        String hexColor = null;

        for (int dx = 0; dx < cellWidth; dx++) {
            for (int dy = 0; dy < cellHeight; dy++) {
                int x = startX + dx;
                int y = startY + dy;
                if (x >= world.getSize() || y >= world.getSize())
                    continue;

                Entity entity = world.getEntity(x, y);
                Tile tile = world.getTile(x, y);

                if (entity != null)
                    containsEntity = true;
                if (tile != null && tile.hasFood())
                    containsFood = true;
                if (tile != null && hexColor == null)
                    hexColor = tile.getType().getHex();
            }
        }

        String symbol;
        String fgAnsi = "";

        if (containsEntity) {
            symbol = "@";
            fgAnsi = "\\u001B[38;2;255;255;255m";
        } else if (containsFood) {
            symbol = "•";
            fgAnsi = "\\u001B[38;2;255;64;64m";
        } else {
            symbol = " ";
            fgAnsi = "";
        }

        if (hexColor == null)
            hexColor = "#000000";

        int r = Integer.parseInt(hexColor.substring(1, 3), 16);
        int g = Integer.parseInt(hexColor.substring(3, 5), 16);
        int b = Integer.parseInt(hexColor.substring(5, 7), 16);

        String block = "\\u001B[48;2;" + r + ";" + g + ";" + b + "m" + fgAnsi + symbol + "\\u001B[0m";
        return block + (isLastCellInRow ? "" : " ");
    }

    /**
     * Prints world statistics to terminal
     */
    public static void printStatistics(World world) {
        System.out.println("\\n=== World Statistics ===");
        System.out.println("Size: " + world.getSize() + "x" + world.getSize());
        System.out.println("Total Entities: " + world.getEntities().size());

        var biomeCounts = world.countEntitiesInAllBiomes();
        biomeCounts.forEach((biome, count) -> System.out.println(biome.name() + " Biome: " + count + " entities"));

        System.out.println("========================\\n");
    }
}