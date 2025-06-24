package world;

import entities.Entity;

import java.util.ArrayList;

public class World {

    private ArrayList<Tile> tiles;
    private ArrayList<Entity> entities;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    public World() {
        tiles = new ArrayList<>();
        entities = new ArrayList<>();
        generateWorld();
    }

    public World(ArrayList<Tile> tiles, ArrayList<Entity> entities) {
        this.tiles = tiles;
        this.entities = entities;
    }

    private void generateWorld() {
        Type[][] biomeMap = new Type[WIDTH][HEIGHT];
        int biomeSeeds = 3; // Number of seeds per biome

        // Seed biomes
        for (Type type : Type.values()) {
            for (int i = 0; i < biomeSeeds; i++) {
                int sx = (int) (Math.random() * WIDTH);
                int sy = (int) (Math.random() * HEIGHT);
                biomeMap[sx][sy] = type;
            }
        }

        // Expand biomes using BFS
        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (biomeMap[x][y] != null) {
                    queue.add(new int[]{x, y});
                }
            }
        }

        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0], y = pos[1];
            Type type = biomeMap[x][y];
            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < WIDTH && ny >= 0 && ny < HEIGHT && biomeMap[nx][ny] == null) {
                    if (Math.random() < 0.6) { // Controls biome "spread"
                        biomeMap[nx][ny] = type;
                        queue.add(new int[]{nx, ny});
                    }
                }
            }
        }

        // Fill any remaining tiles with GRASS
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (biomeMap[x][y] == null) biomeMap[x][y] = Type.GRASS;
                tiles.add(new Tile(x, y, biomeMap[x][y]));
            }
        }
    }

    public Tile getTile(int x, int y) {
        for (Tile tile : tiles) {
            if (tile.getX() == x && tile.getY() == y) {
                return tile;
            }
        }
        return null; // Tile not found
    }
}
