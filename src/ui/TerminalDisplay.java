package ui;

import entities.Entity;
import world.Tile;
import world.Type;
import world.World;

/**
 * Terminal-based display utility for world rendering.
 * Provides ANSI-colored terminal output with optimized rendering and clean visuals.
 */
public class TerminalDisplay {
    private static final int MAX_TERM_GRID_SIZE = 40; // Increased for better detail

    // ANSI color codes for better visuals
    private static final String RESET = "\u001B[0m";
    private static final String HIDE_CURSOR = "\033[?25l";
    private static final String SHOW_CURSOR = "\033[?25h";

    // Symbol choices for better visibility
    private static final String ENTITY_SYMBOL = "●"; // Filled circle for entities
    private static final String FOOD_SYMBOL = "◆"; // Diamond for food
    private static final String EMPTY_SYMBOL = " "; // Space for empty tiles

    // Color cache for performance
    private static final java.util.Map<Type, String> colorCache = new java.util.HashMap<>();

    static {
        // Pre-compute ANSI colors for each biome type
        for (Type type : Type.values()) {
            colorCache.put(type, hexToAnsiBackground(type.getHex()));
        }
    }

    /**
     * Renders the world to terminal with ANSI colors (optimized version)
     */
    public static void renderWorld(World world) {
        int worldSize = world.getSize();
        int gridSize = Math.min(MAX_TERM_GRID_SIZE, worldSize);
        int cellSize = (int) Math.ceil((double) worldSize / gridSize);
        int terminalGridWidth = (int) Math.ceil((double) worldSize / cellSize);
        int terminalGridHeight = (int) Math.ceil((double) worldSize / cellSize);

        // Double buffering: build the entire output first
        StringBuilder buffer = new StringBuilder(terminalGridWidth * terminalGridHeight * 20);

        // Hide cursor for smooth rendering
        buffer.append(HIDE_CURSOR);

        // Add top border
        buffer.append("╔");
        for (int i = 0; i < terminalGridWidth * 2; i++) {
            buffer.append("═");
        }
        buffer.append("╗\n");

        // Render grid with borders
        for (int row = 0; row < terminalGridHeight; row++) {
            buffer.append("║");
            for (int col = 0; col < terminalGridWidth; col++) {
                buffer.append(getTerminalCellBlock(world, col * cellSize, row * cellSize, cellSize, cellSize));
            }
            buffer.append(RESET).append("║\n");
        }

        // Add bottom border
        buffer.append("╚");
        for (int i = 0; i < terminalGridWidth * 2; i++) {
            buffer.append("═");
        }
        buffer.append("╝\n");

        // Add legend
        buffer.append("\n");
        buffer.append("Legend: ");
        buffer.append(ENTITY_SYMBOL).append("=Entity  ");
        buffer.append(FOOD_SYMBOL).append("=Food  ");

        // Show biome colors
        for (Type type : Type.values()) {
            buffer.append(colorCache.get(type))
                    .append("  ")
                    .append(RESET)
                    .append("=").append(type.name())
                    .append("  ");
        }

        // Show cursor again
        buffer.append("\n").append(SHOW_CURSOR);

        // Print everything at once
        System.out.print(buffer.toString());
        System.out.flush();
    }

    /**
     * Returns a summary cell for a region of the world as a string with ANSI codes.
     * Optimized for performance with early exits and cached colors.
     */
    private static String getTerminalCellBlock(World world, int startX, int startY,
            int cellWidth, int cellHeight) {
        boolean containsEntity = false;
        boolean containsFood = false;
        Type dominantBiome = null;

        // Scan the cell region
        for (int dx = 0; dx < cellWidth && !containsEntity; dx++) {
            for (int dy = 0; dy < cellHeight && !containsEntity; dy++) {
                int x = startX + dx;
                int y = startY + dy;
                if (x >= world.getSize() || y >= world.getSize())
                    continue;

                Entity entity = world.getEntity(x, y);
                Tile tile = world.getTile(x, y);

                if (entity != null) {
                    containsEntity = true;
                }
                if (tile != null && tile.hasFood()) {
                    containsFood = true;
                }
                if (tile != null && dominantBiome == null) {
                    dominantBiome = tile.getType();
                }
            }
        }

        // Choose symbol and color
        String symbol;
        String fgColor = "";

        if (containsEntity) {
            // White entity symbol
            symbol = ENTITY_SYMBOL;
            fgColor = "\u001B[38;2;255;255;255m\u001B[1m"; // White + bold
        } else if (containsFood) {
            // Bright red food symbol
            symbol = FOOD_SYMBOL;
            fgColor = "\u001B[38;2;255;100;100m\u001B[1m"; // Red + bold
        } else {
            symbol = EMPTY_SYMBOL;
            fgColor = "";
        }

        // Get background color from cache
        String bgColor = dominantBiome != null ? colorCache.get(dominantBiome) : hexToAnsiBackground("#000000");

        // Build the cell with spacing for better visibility
        return bgColor + fgColor + symbol + " " + RESET;
    }

    /**
     * Converts hex color to ANSI background color code
     */
    private static String hexToAnsiBackground(String hex) {
        if (hex == null || hex.length() != 7) {
            return "\u001B[48;2;0;0;0m";
        }

        try {
            int r = Integer.parseInt(hex.substring(1, 3), 16);
            int g = Integer.parseInt(hex.substring(3, 5), 16);
            int b = Integer.parseInt(hex.substring(5, 7), 16);
            return String.format("\u001B[48;2;%d;%d;%dm", r, g, b);
        } catch (NumberFormatException e) {
            return "\u001B[48;2;0;0;0m";
        }
    }

    /**
     * Prints world statistics to terminal with improved formatting
     */
    public static void printStatistics(World world) {
        var biomeCounts = world.countEntitiesInAllBiomes();
        int totalEntities = biomeCounts.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║                    WORLD STATISTICS                        ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println(String.format("║ World Size:        %-37s ║",
                world.getSize() + "x" + world.getSize() + " tiles"));
        System.out.println(String.format("║ Total Entities:    %-37d ║", totalEntities));
        System.out.println("╠═══════════════════════════════════════════════════════════╣");
        System.out.println("║                  ENTITIES BY BIOME                         ║");
        System.out.println("╠═══════════════════════════════════════════════════════════╣");

        for (Type type : Type.values()) {
            int count = biomeCounts.getOrDefault(type, 0);
            double percentage = totalEntities > 0 ? (count * 100.0 / totalEntities) : 0;
            System.out.println(String.format("║ %-15s: %4d (%.1f%%)%26s║",
                    type.name(), count, percentage, ""));
        }

        System.out.println("╚═══════════════════════════════════════════════════════════╝");
    }

    /**
     * Prints a compact world overview without clearing the screen
     */
    public static void printCompactWorld(World world) {
        System.out.print("World: " + world.getEntities().size() + " entities | ");

        var biomeCounts = world.countEntitiesInAllBiomes();
        for (Type type : Type.values()) {
            System.out.print(type.name() + ":" + biomeCounts.getOrDefault(type, 0) + " ");
        }
        System.out.println();
    }
}