package world;

import entities.Entity;

public enum Type {
    GRASS("#00FF00", 0.12, 8, 20, 50, 100, 1.0), // Moderate temperature, high humidity, sea level - GOOD habitat
    MOUNTAIN("#808080", 0.06, 3, -5, 30, 500, 0.75), // Cold, low humidity, high elevation, harsh movement
    FOREST("#228B22", 0.15, 10, 15, 80, 200, 0.95), // Mild, very humid, elevated - EXCELLENT habitat
    DESERT("#FFD700", 0.03, 2, 40, 10, 50, 0.8), // Hot, dry, low elevation, difficult movement - HARSH
    TUNDRA("#E0FFFF", 0.04, 4, -15, 40, 300, 0.7), // Very cold, moderate humidity, elevated - CHALLENGING
    SWAMP("#556B2F", 0.12, 7, 25, 90, 50, 0.6), // Warm, very humid, low elevation, very slow movement
    OCEAN("#0077BE", 0.08, 6, 10, 100, -50, 0.4), // Cool, maximum humidity, below sea level - MODERATE
    VOLCANIC("#8B0000", 0.01, 1, 60, 5, 800, 0.5); // Extremely hot, very dry, very high elevation - EXTREME

    private String hex;
    private double foodChance;
    private double foodEnergy;
    private double temperature; // Temperature in Celsius
    private double humidity; // Humidity percentage (0-100)
    private double elevation; // Elevation in meters
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
        // Higher stress = more energy consumption
        // Entity's adaptation trait will reduce this stress
        double stress = 1.0;

        // Temperature stress - extreme temps are harsh
        double tempDeviation = Math.abs(temperature - 20); // 20°C is ideal
        if (tempDeviation > 15) {
            stress += tempDeviation / 100.0;
        }

        // Humidity stress - both too dry and too wet are challenging
        double humidityDeviation = Math.abs(humidity - 50); // 50% is ideal
        if (humidityDeviation > 30) {
            stress += humidityDeviation / 150.0;
        }

        // Elevation stress - high altitude is challenging
        if (elevation > 300) {
            stress += elevation / 1500.0;
        } else if (elevation < 0) {
            stress += Math.abs(elevation) / 1000.0; // underwater penalty
        }

        // Return stress multiplier (1.0 = normal, higher = harsher)
        return Math.max(1.0, Math.min(3.0, stress)); // Range: 1.0x to 3.0x
    }
}
