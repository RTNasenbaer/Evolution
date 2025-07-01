package entities;

import world.Tile;
import world.World; // Import World to access WIDTH and SIZE

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
        this.mass = mass; // Adjusted mass
    }

    // Example: Adjust mass to make movement more expensive
    public static Entity createDefaultEntity(int x, int y) {
        return new Entity(x, y, 10, 1, 2); // Increased mass from 1 to 2
    }

    public void moveRandomly() {
        while (true) {
            double angle = Math.random() * 2 * Math.PI;
            int dx = (int) Math.round(speed * Math.cos(angle));
            int dy = (int) Math.round(speed * Math.sin(angle));
            int newX = this.x + dx;
            int newY = this.y + dy;

            // Check if the new position is within boundaries
            if (newX >= 0 && newX < World.SIZE && newY >= 0 && newY < World.SIZE) {
                moveBy(dx, dy);
                break;
            }
        }
    }

    public void moveDirected(World world) {
        int closestX = -1;
        int closestY = -1;
        double minDist = Double.MAX_VALUE;
        double rangeThreshold = World.SIZE * 0.1; // Set range as 30% of the world size

        // Search for the nearest food tile within the range
        for (int x = 0; x < World.SIZE; x++) {
            for (int y = 0; y < World.SIZE; y++) {
                Tile tile = world.getTile(x, y);
                if (tile != null && tile.hasFood()) {
                    double dist = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
                    if (dist < minDist && dist <= rangeThreshold) {
                        minDist = dist;
                        closestX = x;
                        closestY = y;
                    }
                }
            }
        }

        // If food was found within the range, move in its direction
        if (closestX != -1 && closestY != -1) {
            double angle = Math.atan2(closestY - this.y, closestX - this.x);
            int dx = (int) Math.round(speed * Math.cos(angle));
            int dy = (int) Math.round(speed * Math.sin(angle));
            int newX = this.x + dx;
            int newY = this.y + dy;

            // Check if the new position is within boundaries
            if (newX >= 0 && newX < World.SIZE && newY >= 0 && newY < World.SIZE) {
                moveBy(dx, dy);
            }
        } else {
            // No food found within range, move randomly
            moveRandomly();
        }
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
            this.x = Math.max(0, Math.min(World.SIZE - 1, x));
            this.y = Math.max(0, Math.min(World.SIZE - 1, y));
        }
        // else: not enough energy to move
    }

    public void moveBy(int dx, int dy) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        double energyCost = 0.5 * mass * speed * speed * distance;
        if (energy >= energyCost) {
            this.energy -= energyCost;
            // Ensure the entity stays within the world boundaries
            this.x = Math.max(0, Math.min(World.SIZE - 1, this.x + dx));
            this.y = Math.max(0, Math.min(World.SIZE - 1, this.y + dy));
        }
        // else: not enough energy to move
    }

    public void eat(Tile tile, long tickspeed, int gridSize) {
        if (tile.hasFood()) {
            double energyGain = tile.getType().getFoodEnergy();
            this.energy += energyGain;
            tile.setFood(false);
            tile.regenerateFoodAfterDelay(tickspeed, gridSize);
        }
    }

    public boolean isAlive() {
        return energy > 1.0; // Entity is alive if energy is greater than 1.0
    }

    public void reproduce(World world) {
        if (this.energy >= 50) {
            // Reduce energy by half for reproduction
            this.energy /= 2;

            // Create a new entity with the same attributes but at a nearby position
            int dx = (Math.random() > 0.5 ? 1 : -1);
            int dy = (Math.random() > 0.5 ? 1 : -1);
            int newX = Math.max(0, Math.min(World.SIZE - 1, this.x + dx));
            int newY = Math.max(0, Math.min(World.SIZE - 1, this.y + dy));

            // Add the new entity to the world
            world.addEntity(new Entity(newX, newY, this.energy, this.speed, this.mass));
        }
    }

    public void update(World world, long tickspeed, int gridSize) {
        // Move the entity
        moveDirected(world);

        // Eat food if available
        Tile currentTile = world.getTile(this.x, this.y);
        if (currentTile != null) {
            eat(currentTile, tickspeed, gridSize);
        }

        System.out.println("Entity at (" + this.x + ", " + this.y + ") has " + this.energy + " energy.");

        // Check for reproduction
        if (this.energy >= 50) {
            System.out.println("Entity at (" + this.x + ", " + this.y + ") is reproducing.");
            reproduce(world);
        }

        // Check if the entity is still alive
        if (!isAlive()) {
            System.out.println("Entity at (" + this.x + ", " + this.y + ") has died.");
            world.removeEntity(this);
        }
    }
}
