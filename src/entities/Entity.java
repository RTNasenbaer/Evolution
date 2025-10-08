package entities;

import world.Tile;
import world.World; // Import World to access WIDTH and SIZE

public class Entity {

    private int x;
    private int y;
    private double energy;
    private int age; // Current age in ticks

    // Genetic traits (4 core traits for terrain adaptation)
    private double endurance; // 0.5-2.0: Affects movement cost and stamina (lower = less movement cost)
    private double adaptation; // 0.5-2.0: Resistance to harsh climates (higher = better in extreme temps/humidity)
    private double mobility; // 0.5-2.0: Base movement speed (higher = faster, but costs more energy)
    private double efficiency; // 0.5-2.0: Energy consumption rate (higher = lower metabolism, reproduces earlier)

    // Constants for optimization
    private static final double MUTATION_RATE = 0.15; // Increased to 15% for more variation
    private static final double MUTATION_STRENGTH = 0.15; // Stronger mutations for faster adaptation
    private static final double ENERGY_FORMULA_CONSTANT = 0.08; // Movement energy cost
    private static final double BASE_REPRODUCTION_THRESHOLD = 50.0; // Base energy for reproduction
    private static final double BASE_METABOLISM = 0.04; // Base energy loss per tick
    private static final double SIGHT_RANGE = 12.0; // Fixed sight range for all entities
    private static final int BASE_LIFESPAN = 800; // Fixed lifespan

    public Entity(int x, int y, double energy) {
        this(x, y, energy, 1.0, 1.0, 1.0, 1.0, 0);
    }

    public Entity(int x, int y, double energy, double endurance, double adaptation,
            double mobility, double efficiency, int age) {
        this.x = x;
        this.y = y;
        this.energy = energy;
        this.endurance = endurance;
        this.adaptation = adaptation;
        this.mobility = mobility;
        this.efficiency = efficiency;
        this.age = age;
    }

    // Create entity with default balanced traits
    public static Entity createDefaultEntity(int x, int y) {
        return new Entity(x, y, 60.0); // Starting energy: 60
    }

    public void moveRandomly(World world) {
        while (true) {
            double angle = Math.random() * 2 * Math.PI;
            // Mobility affects movement distance
            int dx = (int) Math.round(mobility * Math.cos(angle));
            int dy = (int) Math.round(mobility * Math.sin(angle));
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

        // Optimize search by only checking within fixed sight range
        int searchRadius = (int) Math.ceil(SIGHT_RANGE);
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
                    if (dist < minDist && dist <= SIGHT_RANGE) {
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
            int dx = (int) Math.round(mobility * Math.cos(angle));
            int dy = (int) Math.round(mobility * Math.sin(angle));
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

    public int getAge() {
        return age;
    }

    public double getEndurance() {
        return endurance;
    }

    public double getAdaptation() {
        return adaptation;
    }

    public double getMobility() {
        return mobility;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public double getReproductionThreshold() {
        return BASE_REPRODUCTION_THRESHOLD / efficiency; // Higher efficiency = reproduce sooner
    }

    public double getSightRange() {
        return SIGHT_RANGE;
    }

    public void moveTo(int x, int y) {
        double distance = Math.sqrt(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2));
        // Energy cost: mobility and endurance both matter
        // Higher mobility = faster but more energy
        // Higher endurance = more energy cost for moving
        double energyCost = ENERGY_FORMULA_CONSTANT * endurance * mobility * mobility * distance;
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

        // Terrain difficulty affects low-endurance entities more
        double terrainPenalty = movementModifier < 1.0 ? (2.0 - movementModifier) / endurance : 1.0;

        double energyCost = ENERGY_FORMULA_CONSTANT * endurance * mobility * mobility * distance * terrainPenalty;

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
        return energy > 1.0 && age < BASE_LIFESPAN; // Entity is alive if energy > 1.0 and not too old
    }

    public void reproduce(World world) {
        double threshold = getReproductionThreshold();
        if (this.energy >= threshold) {
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
        // Mutate the 4 core traits
        double newEndurance = mutate(this.endurance, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 2.0);
        double newAdaptation = mutate(this.adaptation, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 2.0);
        double newMobility = mutate(this.mobility, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 2.0);
        double newEfficiency = mutate(this.efficiency, MUTATION_RATE, MUTATION_STRENGTH, 0.5, 2.0);

        return new Entity(x, y, this.energy, newEndurance, newAdaptation,
                newMobility, newEfficiency, 0);
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

        // Metabolism: lower efficiency = higher energy consumption
        // Adaptation reduces environmental stress impact
        double metabolismRate = BASE_METABOLISM / efficiency;
        double adaptedStress = 1.0 + (environmentalStress - 1.0) / adaptation;
        energy -= metabolismRate * adaptedStress;

        // Move the entity
        moveDirected(world);

        // Eat food if available (reuse currentTile variable)
        if (currentTile != null) {
            eat(currentTile, tickspeed, gridSize);
        }

        // Check for reproduction
        double threshold = getReproductionThreshold();
        if (this.energy >= threshold) {
            reproduce(world);
        }

        // Check if the entity is still alive
        if (!isAlive()) {
            world.removeEntity(this);
        }
    }
}
