package world;

public enum Type {
    GRASS("#00FF00", 0.1, 10),
    MOUNTAIN("#808080", 0.05, 2),
    FOREST("#228B22", 0.15, 15),
    DESERT("#FFD700", 0.02, 1);
    
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
