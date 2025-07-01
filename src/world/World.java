package world;

import entities.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class World {

    private CopyOnWriteArrayList<Tile> tiles;
    private CopyOnWriteArrayList<Entity> entities;
    public static final int SIZE = 50;

    public World() {
        tiles = new CopyOnWriteArrayList<>();
        entities = new CopyOnWriteArrayList<>();
        generateWorld();
    }

    public World(CopyOnWriteArrayList<Tile> tiles, CopyOnWriteArrayList<Entity> entities) {
        this.tiles = tiles;
        this.entities = entities;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    private void generateWorld() {
        Type[][] biomeMap = new Type[SIZE][SIZE];
        int biomeSeeds = 3; // Number of seeds per biome

        // Seed biomes
        for (Type type : Type.values()) {
            for (int i = 0; i < biomeSeeds; i++) {
                int sx = (int) (Math.random() * SIZE);
                int sy = (int) (Math.random() * SIZE);
                biomeMap[sx][sy] = type;
            }
        }

        // Expand biomes using BFS
        Queue<int[]> queue = new LinkedList<>();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (biomeMap[x][y] != null) {
                    queue.add(new int[] { x, y });
                }
            }
        }

        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0], y = pos[1];
            Type type = biomeMap[x][y];
            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (nx >= 0 && nx < SIZE && ny >= 0 && ny < SIZE && biomeMap[nx][ny] == null) {
                    if (Math.random() < 0.6) { // Controls biome "spread"
                        biomeMap[nx][ny] = type;
                        queue.add(new int[] { nx, ny });
                    }
                }
            }
        }

        // Fill any remaining tiles with GRASS
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (biomeMap[x][y] == null)
                    biomeMap[x][y] = Type.GRASS;
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

    public Entity getEntity(int x, int y) {
        for (Entity entity : entities) {
            if (entity.getX() == x && entity.getY() == y) {
                return entity;
            }
        }
        return null; // Entity not found
    }

    public int getSize() {
        return SIZE;
    }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }

    public CopyOnWriteArrayList<Tile> getTiles() {
        return tiles;
    }

    public Map<Type, Integer> countEntitiesInAllBiomes() {
        // Map to store biome type to set of tiles in that biome
        Map<Type, Set<Tile>> biomeTiles = new HashMap<>();
        for (Tile tile : tiles) {
            biomeTiles.computeIfAbsent(tile.getType(), k -> new HashSet<>()).add(tile);
        }

        // Map to store biome type to entity count
        Map<Type, Integer> biomeEntityCounts = new HashMap<>();
        for (Type type : biomeTiles.keySet()) {
            biomeEntityCounts.put(type, 0);
        }

        // Count entities in each biome
        for (Entity entity : entities) {
            Tile entityTile = getTile(entity.getX(), entity.getY());
            if (entityTile != null) {
                Type type = entityTile.getType();
                biomeEntityCounts.put(type, biomeEntityCounts.get(type) + 1);
            }
        }

        return biomeEntityCounts;
    }
}
