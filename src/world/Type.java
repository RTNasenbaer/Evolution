package world;

public enum Type {
    GRASS("#00FF00", 0.1, 2), // Reduced from 10 to 2
    MOUNTAIN("#808080", 0.05, 1), // Reduced from 2 to 1
    FOREST("#228B22", 0.15, 5), // Reduced from 15 to 5
    DESERT("#FFD700", 0.02, 0.5); // Reduced from 1 to 0.5
    
    private String hex;
    private double foodChance;
    private double foodEnergy;
    Type(String hex, double foodChance, double foodEnergy) {
        this.hex = hex;
        this.foodChance = foodChance;
        this.foodEnergy = foodEnergy;
    }
    public String getHex() {
        return hex;
    }
    public double getFoodChance() {
        return foodChance;
    }
    public double getFoodEnergy() {
        return foodEnergy;
    }
}
