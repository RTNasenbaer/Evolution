package world;

import entities.Entity;

public enum Type {
    GRASS("#00FF00", 0.1, 2, 20, 50, 100, 1.0),           // Moderate temperature, high humidity, sea level
    MOUNTAIN("#808080", 0.05, 1, -5, 30, 500, 0.8),        // Cold, low humidity, high elevation, harsh movement
    FOREST("#228B22", 0.15, 5, 15, 80, 200, 1.1),          // Mild, very humid, elevated, good for hiding
    DESERT("#FFD700", 0.02, 0.5, 40, 10, 50, 0.7),         // Hot, dry, low elevation, difficult movement
    TUNDRA("#E0FFFF", 0.03, 1.5, -15, 40, 300, 0.6),       // Very cold, moderate humidity, elevated
    SWAMP("#556B2F", 0.12, 3, 25, 90, 50, 0.5),            // Warm, very humid, low elevation, very slow movement
    OCEAN("#0077BE", 0.08, 4, 10, 100, -50, 0.3),          // Cool, maximum humidity, below sea level
    VOLCANIC("#8B0000", 0.01, 0.2, 60, 5, 800, 0.4);       // Extremely hot, very dry, very high elevation
    
    private String hex;
    private double foodChance;
    private double foodEnergy;
    private double temperature;      // Temperature in Celsius
    private double humidity;         // Humidity percentage (0-100)
    private double elevation;        // Elevation in meters
    private double movementModifier; // Movement speed modifier (1.0 = normal)
    
    Type(String hex, double foodChance, double foodEnergy, double temperature, 
         double humidity, double elevation, double movementModifier) {
        this.hex = hex;
        this.foodChance = foodChance;
        this.foodEnergy = foodEnergy;
        this.temperature = temperature;
        this.humidity = humidity;
        this.elevation = elevation;
        this.movementModifier = movementModifier;
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
    
    public double getTemperature() {
        return temperature;
    }
    
    public double getHumidity() {
        return humidity;
    }
    
    public double getElevation() {
        return elevation;
    }
    
    public double getMovementModifier() {
        return movementModifier;
    }
    
    public double getEnvironmentalStress(Entity entity) {
        // Calculate environmental stress based on biome conditions
        // This affects entity metabolism and survival
        double stress = 1.0;
        
        // Temperature stress
        if (temperature < -10 || temperature > 35) {
            stress += Math.abs(temperature) / 100.0;
        }
        
        // Humidity stress (both too dry and too wet are stressful)
        if (humidity < 20 || humidity > 85) {
            stress += Math.abs(50 - humidity) / 100.0;
        }
        
        // Elevation stress (high altitude is challenging)
        if (elevation > 400) {
            stress += elevation / 1000.0;
        }
        
        return Math.max(0.5, Math.min(3.0, stress)); // Clamp between 0.5x and 3.0x normal metabolism
    }
}
