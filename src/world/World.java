package world;

import java.util.ArrayList;

public class World {

    private ArrayList<Tile> tiles;
    public static final int WIDTH = 10;
    public static final int HEIGHT = 10;

    public World() {
        tiles = new ArrayList<>();
        generateWorld();
    }

    private void generateWorld() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Type type = Type.GRASS; // Default type
                /*if (Math.random() < 0.1) {
                    type = Type.MOUNTAIN;
                } else if (Math.random() < 0.2) {
                    type = Type.FOREST;
                } else if (Math.random() < 0.3) {
                    type = Type.DESERT;
                }*/
                tiles.add(new Tile(x, y, type));
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
