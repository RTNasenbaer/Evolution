package entities;

import world.World; // Import World to access WIDTH and HEIGHT

public class Entity {

    private int x;
    private int y;
    private double energy;
    private double speed;

    public Entity(int x, int y, double energy, double speed) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.speed = speed;
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

    public void moveTo(int x, int y) {
        // Ensure the entity stays within the world boundaries
        this.x = Math.max(0, Math.min(World.WIDTH - 1, x));
        this.y = Math.max(0, Math.min(World.HEIGHT - 1, y));
    }

    public void moveBy(int dx, int dy) {
        // Ensure the entity stays within the world boundaries
        this.x = Math.max(0, Math.min(World.WIDTH - 1, this.x + dx));
        this.y = Math.max(0, Math.min(World.HEIGHT - 1, this.y + dy));
    }
}
