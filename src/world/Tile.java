package world;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Tile {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private int x;
    private int y;
    private Type type;
    private boolean food;

    public Tile(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.food = (Math.random()<=type.getFoodChance()) ? true : false; // Default value
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public void regenerateFoodAfterDelay(long tickspeed, int gridSize) {
        long baseDelay = 5000; // Base delay in milliseconds
        double gridFactor = (gridSize * gridSize) / (gridSize * 100.0); // Normalize grid size
        long adjustedDelay = (long) (baseDelay * (tickspeed / 200.0) * gridFactor); // Adjust delay
        scheduler.schedule(() -> setFood(true), adjustedDelay, TimeUnit.MILLISECONDS);
    }
}

