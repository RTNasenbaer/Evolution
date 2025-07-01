package entities;

import world.Tile;
import world.World; // Import World to access WIDTH and HEIGHT

public class Entity {

    private int x;
    private int y;
    private double energy;
    private double speed;
    private double mass;

    public Entity(int x, int y, double energy, double speed, double mass) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.speed = speed;
        this.mass = mass;
    }

    public void moveRandomly() {
        while (true) {
            double angle = Math.random() * 2 * Math.PI;
            int dx = (int) Math.round(speed * Math.cos(angle));
            int dy = (int) Math.round(speed * Math.sin(angle));
            int newX = this.x + dx;
            int newY = this.y + dy;

            // Check if the new position is within boundaries
            if (newX >= 0 && newX < World.WIDTH && newY >= 0 && newY < World.HEIGHT) {
                moveBy(dx, dy);
                break;
            }
        }
    }

    public void moveDirected(World world) {
        int closestX = -1;
        int closestY = -1;
        double minDist = Double.MAX_VALUE;
        // Search for the nearest food tile
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                Tile tile = world.getTile(x, y);
                if (tile != null && tile.hasFood()) {
                    double dist = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
                    if (dist < minDist) {
                        minDist = dist;
                        closestX = x;
                        closestY = y;
                    }
                }
            }
        }
        // If food was found, move in the direction of the food by 'speed' units
        if (closestX != -1 && closestY != -1) {
            double angle = Math.atan2(closestY - this.y, closestX - this.x);
            int dx = (int) Math.round(speed * Math.cos(angle));
            int dy = (int) Math.round(speed * Math.sin(angle));
            int newX = this.x + dx;
            int newY = this.y + dy;
            // Check if the new position is within boundaries
            if (newX >= 0 && newX < World.WIDTH && newY >= 0 && newY < World.HEIGHT) {
                moveBy(dx, dy);
            }
            // else: can't move out of bounds
        }
        // else: no food found, do nothing
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getEnergy() {
        return energy;
    }

    public double getSpeed() {
        return speed;
    }

    public double getMass() {
        return mass;
    }

    public void moveTo(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        double energyCost = 0.5 * mass * speed * speed * distance;
        if (energy >= energyCost) {
            this.energy -= energyCost;
            // Ensure the entity stays within the world boundaries
            this.x = Math.max(0, Math.min(World.WIDTH - 1, x));
            this.y = Math.max(0, Math.min(World.HEIGHT - 1, y));
        }
        // else: not enough energy to move
    }

    public void moveBy(int dx, int dy) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        double energyCost = 0.5 * mass * speed * speed * distance;
        if (energy >= energyCost) {
            this.energy -= energyCost;
            // Ensure the entity stays within the world boundaries
            this.x = Math.max(0, Math.min(World.WIDTH - 1, this.x + dx));
            this.y = Math.max(0, Math.min(World.HEIGHT - 1, this.y + dy));
        }
        // else: not enough energy to move
    }

    public void eat(Tile tile) {
        if (tile.hasFood()) {
            double energyGain = tile.getType().getFoodEnergy();
            this.energy += energyGain;
            tile.setFood(false);
        }
    }

    public boolean isAlive() {
        return energy > 0;
    }
}
