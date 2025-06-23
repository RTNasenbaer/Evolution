package world;

public enum Type {
    GRASS("#00FF00", 0.1),
    MOUNTAIN("#808080", 0.05),
    FOREST("#228B22", 0.15),
    DESERT("#FFD700", 0.02),;
    
    private String hex;
    private double foodChance;
    Type(String hex, double foodChance) {
        this.hex = hex;
        this.foodChance = foodChance;
    }
    public String getHex() {
        return hex;
    }
    public double getFoodChance() {
        return foodChance;
    }
}
