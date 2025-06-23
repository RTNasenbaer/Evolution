package world;

public class Tile {

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
}

