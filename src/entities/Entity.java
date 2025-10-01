package entities;

import world.Tile;
import world.World; // Import World to access WIDTH and SIZE

public class Entity {

    private int x;
    private int y;
    private double energy;
    private double speed;
    private double mass;
    
    // Genetic traits
    private double energyEfficiency; // How efficiently the entity uses energy for movement
    private double reproductionThreshold; // Energy threshold for reproduction
    private double sightRange; // Detection range for food
    private double metabolismRate; // Energy consumption per tick
    private int maxLifespan; // Maximum age in ticks
    private int age; // Current age in ticks
    
    // Constants for optimization
    private static final double MUTATION_RATE = 0.1;
    private static final double MUTATION_STRENGTH = 0.1;
    private static final double ENERGY_FORMULA_CONSTANT = 0.5;

    public Entity(int x, int y, double energy, double speed, double mass) {
        this(x, y, energy, speed, mass, 1.0, 50.0, World.SIZE * 0.1, 0.1, 1000, 0);
    }
    
    public Entity(int x, int y, double energy, double speed, double mass, 
                 double energyEfficiency, double reproductionThreshold, double sightRange,
                 double metabolismRate, int maxLifespan, int age) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.speed = speed;
        this.mass = mass;
        this.energyEfficiency = energyEfficiency;
        this.reproductionThreshold = reproductionThreshold;
        this.sightRange = sightRange;
        this.metabolismRate = metabolismRate;
        this.maxLifespan = maxLifespan;
        this.age = age;
    }

    // Example: Adjust mass to make movement more expensive
    public static Entity createDefaultEntity(int x, int y) {
        return new Entity(x, y, 10, 1, 2); // Increased mass from 1 to 2
    }

    public void moveRandomly(World world) {
        while (true) {
            double angle = Math.random() * 2 * Math.PI;
            int dx = (int) Math.round(speed * Math.cos(angle));
            int dy = (int) Math.round(speed * Math.sin(angle));
            int newX = this.x + dx;
            int newY = this.y + dy;

            // Check if the new position is within boundaries
            if (newX >= 0 && newX < World.SIZE && newY >= 0 && newY < World.SIZE) {
                Tile targetTile = world.getTile(newX, newY);
                world.Type biomeType = targetTile != null ? targetTile.getType() : null;
                moveBy(dx, dy, biomeType);
                break;
            }
        }
    }

    public void moveDirected(World world) {
        int closestX = -1;
        int closestY = -1;
        double minDist = Double.MAX_VALUE;
        
        // Optimize search by only checking within sight range
        int searchRadius = (int) Math.ceil(sightRange);
        int startX = Math.max(0, this.x - searchRadius);
        int endX = Math.min(World.SIZE - 1, this.x + searchRadius);
        int startY = Math.max(0, this.y - searchRadius);
        int endY = Math.min(World.SIZE - 1, this.y + searchRadius);

        // Search for the nearest food tile within the range
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                Tile tile = world.getTile(x, y);
                if (tile != null && tile.hasFood()) {
                    double dist = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
                    if (dist < minDist && dist <= sightRange) {
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
                Tile targetTile = world.getTile(newX, newY);
                world.Type biomeType = targetTile != null ? targetTile.getType() : null;
                moveBy(dx, dy, biomeType);
            }
        } else {
            // No food found within range, move randomly
            moveRandomly(world);
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
    
    public double getEnergyEfficiency() {
        return energyEfficiency;
    }
    
    public double getReproductionThreshold() {
        return reproductionThreshold;
    }
    
    public double getSightRange() {
        return sightRange;
    }
    
    public double getMetabolismRate() {
        return metabolismRate;
    }
    
    public int getMaxLifespan() {
        return maxLifespan;
    }
    
    public int getAge() {
        return age;
    }

    public void moveTo(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        double energyCost = (ENERGY_FORMULA_CONSTANT * mass * speed * speed * distance) / energyEfficiency;
        if (energy >= energyCost) {
            this.energy -= energyCost;
            // Ensure the entity stays within the world boundaries
            this.x = Math.max(0, Math.min(World.SIZE - 1, x));
            this.y = Math.max(0, Math.min(World.SIZE - 1, y));
        }
        // else: not enough energy to move
    }

    public void moveBy(int dx, int dy, world.Type biomeType) {
        double distance = Math.sqrt(dx * dx + dy * dy);
        double movementModifier = biomeType != null ? biomeType.getMovementModifier() : 1.0;
        double adjustedSpeed = speed * movementModifier;
        double energyCost = (ENERGY_FORMULA_CONSTANT * mass * adjustedSpeed * adjustedSpeed * distance) / energyEfficiency;
        if (energy >= energyCost) {
            this.energy -= energyCost;
            // Ensure the entity stays within the world boundaries
            this.x = Math.max(0, Math.min(World.SIZE - 1, this.x + dx));
            this.y = Math.max(0, Math.min(World.SIZE - 1, this.y + dy));
        }
        // else: not enough energy to move
    }
    
    // Overloaded method for backward compatibility
    public void moveBy(int dx, int dy) {
        moveBy(dx, dy, null);
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
        return energy > 1.0 && age < maxLifespan; // Entity is alive if energy > 1.0 and not too old
    }

    public void reproduce(World world) {
        if (this.energy >= reproductionThreshold) {
            // Reduce energy by half for reproduction
            this.energy /= 2;

            // Create a new entity with the same attributes but at a nearby position
            int dx = (Math.random() > 0.5 ? 1 : -1);
            int dy = (Math.random() > 0.5 ? 1 : -1);
            int newX = Math.max(0, Math.min(World.SIZE - 1, this.x + dx));
            int newY = Math.max(0, Math.min(World.SIZE - 1, this.y + dy));

            // Create offspring with mutations
            Entity offspring = createOffspring(newX, newY);
            world.addEntity(offspring);
        }
    }
    
    private Entity createOffspring(int x, int y) {
        // Use class constants for mutation parameters
        
        // Mutate traits with small random variations
        double newSpeed = mutate(this.speed, MUTATION_RATE, MUTATION_STRENGTH, 0.1, 3.0);
        double newMass = mutate(this.mass, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 5.0);
        double newEnergyEfficiency = mutate(this.energyEfficiency, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 2.0);
        double newReproductionThreshold = mutate(this.reproductionThreshold, MUTATION_RATE, MUTATION_STRENGTH, 20.0, 100.0);
        double newSightRange = mutate(this.sightRange, MUTATION_RATE, MUTATION_STRENGTH, 1.0, World.SIZE * 0.3);
        double newMetabolismRate = mutate(this.metabolismRate, MUTATION_RATE, MUTATION_STRENGTH, 0.05, 0.5);
        int newMaxLifespan = (int) mutate(this.maxLifespan, MUTATION_RATE, MUTATION_STRENGTH, 500, 2000);
        
        return new Entity(x, y, this.energy, newSpeed, newMass, 
                         newEnergyEfficiency, newReproductionThreshold, newSightRange,
                         newMetabolismRate, newMaxLifespan, 0);
    }
    
    private double mutate(double value, double mutationRate, double mutationStrength, double min, double max) {
        if (Math.random() < mutationRate) {
            double change = (Math.random() - 0.5) * 2 * mutationStrength * value;
            value += change;
        }
        return Math.max(min, Math.min(max, value));
    }

    public void update(World world, long tickspeed, int gridSize) {
        // Age the entity
        age++;
        
        // Apply metabolism (energy loss over time) with environmental stress
        Tile currentTile = world.getTile(this.x, this.y);
        double environmentalStress = currentTile != null ? currentTile.getType().getEnvironmentalStress(this) : 1.0;
        energy -= metabolismRate * environmentalStress;
        
        // Move the entity
        moveDirected(world);

        // Eat food if available (reuse currentTile variable)
        if (currentTile != null) {
            eat(currentTile, tickspeed, gridSize);
        }

        System.out.println("Entity at (" + this.x + ", " + this.y + ") has " + String.format("%.1f", this.energy) + " energy, age " + age + ".");

        // Check for reproduction
        if (this.energy >= reproductionThreshold) {
            System.out.println("Entity at (" + this.x + ", " + this.y + ") is reproducing.");
            reproduce(world);
        }

        // Check if the entity is still alive
        if (!isAlive()) {
            if (age >= maxLifespan) {
                System.out.println("Entity at (" + this.x + ", " + this.y + ") has died of old age.");
            } else {
                System.out.println("Entity at (" + this.x + ", " + this.y + ") has died of starvation.");
            }
            world.removeEntity(this);
        }
    }
}
