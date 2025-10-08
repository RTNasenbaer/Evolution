# World Seed System

## Overview

The Evolution Simulation uses a seed-based world generation system that allows for:

- **Reproducible Worlds**: Same seed = same world layout
- **Procedural Generation**: Automatic biome placement
- **Custom Designs**: Manual world building with export/import
- **Compact Storage**: Efficient seed encoding

## WorldSeed Class

Located in `src/world/WorldSeed.java`, this class handles all seed operations.

### Core Methods

```java
// Generate world from seed
World generateWorld(String seed, int width, int height)

// Export world to seed string
String exportWorldToSeed(World world)

// Import seed and create world
World importSeedAndCreateWorld(String seed)

// Validate seed format
boolean isValidSeed(String seed)
```

## Seed Types

### 1. Procedural Seeds (Numeric)

**Format**: Integer number (e.g., `12345`)

**Generation Process**:

1. Seed initializes Random number generator
2. BFS (Breadth-First Search) algorithm places biomes
3. Each biome expands from random starting points
4. Result: Natural-looking biome clusters

**Example**:

```
Seed: 42
Result: Random but reproducible world with natural biome distribution
```

### 2. String Hash Seeds

**Format**: Any text string (e.g., `MyEvoWorld`)

**Generation Process**:

1. String converted to hash code
2. Hash used as numeric seed
3. Same procedural generation as numeric seeds

**Example**:

```
Seed: "Antarctica"
Result: Same world every time this string is used
```

### 3. Designed Seeds (Compressed)

**Format**: `EVO_<base64_compressed_data>`

**Structure**:

```
EVO_eyJ3aWR0aCI6MTAwLCJoZWlnaHQiOjEwMCwidGlsZXMiOlt7IngiOjAsInkiOjAsInQiOiJHIn0s...
     │   └─ Base64-encoded compressed JSON
     └─ Prefix indicating designed world
```

**JSON Structure** (before compression):

```json
{
  "width": 100,
  "height": 100,
  "tiles": [
    {"x": 0, "y": 0, "t": "G"},  // Grassland at (0,0)
    {"x": 1, "y": 0, "t": "W"},  // Water at (1,0)
    ...
  ]
}
```

**Compression Method**:

1. Build JSON with tile data
2. GZIP compression
3. Base64 encoding
4. Add "EVO\_" prefix

## Using Seeds

### In Terminal Mode (Main.java)

```bash
# Start simulation
java -cp "build;lib/gson-2.10.1.jar" Main

# When prompted, enter seed:
Enter world seed (or press Enter for random): 12345

# View current seed
> /seed

# Export current world as designed seed
> # Not yet implemented in terminal mode
```

### In GUI Mode (MainAppNew.java)

1. **At Startup**:

   - Dialog appears: "Enter world seed (leave empty for random)"
   - Enter seed string or numeric value
   - Click OK to generate world

2. **During Simulation**:

   - Seed displayed in bottom-left corner
   - Cannot change seed without restarting

3. **Export Current World**:
   - Feature planned but not yet in main app
   - Use World Builder for designed world export

### In World Builder (WorldBuilderNew.java)

1. **Import Seed**:

   - Click "Import Seed"
   - Enter seed string (procedural or designed)
   - World loads instantly

2. **Design World**:

   - Use brush tools to paint biomes
   - Create custom patterns

3. **Export Seed**:
   - Click "Export Seed"
   - Copyable dialog appears with compressed seed
   - Share seed with others

## Seed Format Details

### Tile Type Encoding

Single-character codes for compactness:

| Type | Code | Full Name |
| ---- | ---- | --------- |
| G    | G    | GRASSLAND |
| F    | F    | FOREST    |
| W    | W    | WATER     |
| D    | D    | DESERT    |
| M    | M    | MOUNTAIN  |
| S    | S    | SNOW      |

### Compression Algorithm

**Purpose**: Reduce seed string length for easier sharing

**Steps**:

1. Create JSON array of tiles
2. Only include tiles (empty world = minimal seed)
3. Compress with GZIP
4. Encode with Base64
5. Prepend "EVO\_"

**Example Compression**:

```
Original JSON: 50,000 characters
After GZIP: 5,000 bytes
After Base64: 6,667 characters
Final Seed: "EVO_<6667 chars>"
```

## Procedural Generation Algorithm

### BFS-Based Biome Placement

```
1. Initialize all tiles as empty
2. Select N random starting positions (N = number of biomes)
3. Assign each position a biome type
4. For each starting position:
   - Add to queue
   - While queue not empty:
     - Pop tile from queue
     - For each adjacent tile:
       - If empty and random check passes:
         - Set to same biome type
         - Add to queue
5. Fill remaining empty tiles with default type
```

### Parameters

- **Biome Diversity**: How many different biomes to generate
- **Expansion Rate**: Probability of biome spreading to adjacent tiles
- **Cluster Size**: How large each biome region becomes

### Seed Consistency

The same seed always produces:

- Same starting positions (deterministic random)
- Same expansion order (queue order preserved)
- Same final layout

## Advanced Usage

### Custom Seed Generation

You can create your own procedural seeds by choosing numbers:

```
1-1000: Small variations
1000-100000: Moderate variations
100000+: Large variations
```

**Tip**: Use memorable numbers like birthdays or significant dates.

### Hybrid Approach

1. Start with procedural seed: `12345`
2. Load in World Builder
3. Make manual modifications
4. Export as designed seed
5. Share both seeds (original + modified)

### Seed Collections

Create libraries of interesting seeds:

```
seeds/
  grassland_heavy_42.txt
  desert_oasis_7890.txt
  mountain_range_15432.txt
  archipelago_96541.txt
```

## Seed Import from File (Planned Feature)

Future implementation will support:

- Load `.seed` files from disk
- Batch seed testing
- Seed library management

```java
// Planned API
WorldSeed.importFromFile("examples/interesting_world.seed")
```

## Best Practices

### For Reproducible Experiments

1. **Document Seeds**: Always record seed used for experiments
2. **Version Control**: Store seeds alongside data files
3. **Seed Naming**: Use descriptive names (e.g., `high_mountains_42`)

### For Sharing

1. **Use Designed Seeds for Precision**: Share exact world layouts
2. **Use Procedural Seeds for Variety**: Let others explore variations
3. **Include Screenshots**: Visual reference helps

### For Performance

1. **Procedural Seeds**: Fastest to generate
2. **Designed Seeds**: Slightly slower due to decompression
3. **Large Worlds**: Designed seeds may be very long

## Troubleshooting

### "Invalid Seed Format" Error

- Check seed string for typos
- Ensure EVO\_ prefix present for designed seeds
- Verify Base64 encoding is intact

### "Seed Decompression Failed"

- Seed may be corrupted
- Try re-copying seed string
- Check for missing characters at end

### World Doesn't Match Expected

- Ensure exact same seed string (case-sensitive for hash seeds)
- Check world dimensions match (100x100 vs 50x50)
- Verify same application version (algorithm changes affect generation)

## Examples

### Example 1: Desert World

```
Seed: 77777
Description: Predominantly desert with small oases
Use Case: Testing entity survival in harsh environments
```

### Example 2: Archipelago

```
Seed: 88888
Description: Many small islands separated by water
Use Case: Testing entity movement and isolation
```

### Example 3: Custom Design

```
Seed: EVO_eJyNVMtuwjAQ...
Description: Checkerboard pattern of grassland and forest
Use Case: Testing biome preference behaviors
```

## Related Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - World generation implementation
- [GETTING_STARTED.md](GETTING_STARTED.md) - How to use seeds in practice
- [DATA_ANALYSIS.md](DATA_ANALYSIS.md) - Using seeds for experiments
- [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md) - Simulation balance configuration
