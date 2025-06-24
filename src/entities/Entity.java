package entities;

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
        this.x = x;
        this.y = y;
    }
}
