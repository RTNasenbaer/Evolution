# Quick Balance Verification Test

## How to Test the Balance Changes

### Test 1: Basic Survival Check (Terminal Mode)

1. **Compile the project**:

   ```powershell
   # Use VS Code Task: Ctrl+Shift+B → "🏗️ Compile All Java Files"
   # OR manually:
   cd d:\Workspace\Evolution
   javac -cp ".;lib/gson-2.10.1.jar" -d build src/**/*.java
   ```

2. **Run Terminal Mode**:

   ```powershell
   # Use VS Code Task: "⚡ Run Terminal Simulation"
   # OR manually:
   java -cp "build;lib/gson-2.10.1.jar" Main
   ```

3. **Quick Test Commands**:

   ```
   > (Leave seed empty for random world)
   > /spawn 25 25
   > /spawn 25 26
   > /spawn 26 25
   > /run 100
   > /show
   > /stats
   ```

4. **Expected Results**:
   - ✓ Most entities should survive 100+ steps
   - ✓ Population should grow (reproductions occurring)
   - ✓ Entities visible in GRASS and FOREST biomes
   - ✓ Step counter shows ~100+ steps

### Test 2: Energy Budget Verification

**Manual Calculation**:

Starting entity (default traits):

- Energy: **50**
- Speed: **1.0**
- Mass: **2.0**
- Efficiency: **1.0**
- Metabolism: **0.05**

**One Move Cost**:

```
Cost = 0.1 * mass * speed² * distance / efficiency * movementModifier
Cost = 0.1 * 2.0 * 1.0² * 1.0 / 1.0 * 1.0
Cost = 0.2 energy per tile
```

**Metabolism Cost (per step)**:

```
Cost = metabolismRate * environmentalStress
Cost = 0.05 * 1.0 (in GRASS)
Cost = 0.05 energy per step
```

**Food Gain (GRASS)**:

```
Gain = 8 energy per food tile
```

**Survival Calculation**:

```
Moves before starvation: 50 / 0.2 = 250 moves (if no food)
Steps before starvation: 50 / 0.05 = 1000 steps (if stationary)
Food needed per 100 steps: (100 * 0.05 + 20 * 0.2) / 8 = ~1 food tile
```

✓ **VIABLE**: Entity can survive 100+ steps easily with minimal food

### Test 3: Reproduction Check

**Reproduction Threshold**: 40 energy

**Energy Gain Needed**:

- Starting: 50 energy
- Already above threshold! ✓
- Can reproduce immediately if threshold check allows

**After First Reproduction**:

- Parent: 50 / 2 = 25 energy
- Offspring: 25 energy (inherits half)
- Parent needs: 40 - 25 = 15 more energy
- Food tiles needed: 15 / 8 = ~2 GRASS tiles

✓ **ACHIEVABLE**: Reproduction cycle is viable

### Test 4: Biome Comparison Test

**Setup**:

```
> (Create world)
> /spawn 10 10    # In a GRASS area
> /spawn 40 40    # In a different biome
> /run 200
> /stats
```

**Expected Observations**:

- More entities in GRASS/FOREST areas
- Fewer entities in DESERT/MOUNTAIN areas
- Clear habitat preference emerging

### Test 5: Long-Term Evolution (GUI Mode)

1. **Run GUI**:

   ```powershell
   # Use VS Code Task: "🚀 Run Evolution Simulation (GUI)"
   ```

2. **Setup**:

   - Use default or random world seed
   - Let initial entity spawn

3. **Run Test**:

   - Click "Run 1000 Steps"
   - Watch population graph
   - Click "Statistics" to see distribution

4. **Expected Results**:
   - ✓ Population should grow initially
   - ✓ Population should stabilize (carrying capacity)
   - ✓ Graph shows growth curve, not immediate extinction
   - ✓ Multiple biomes have entities
   - ✓ FOREST/GRASS have highest counts

### Test 6: Batch Simulation Test (GUI)

1. **In GUI**:

   - Number of runs: **5**
   - Steps per run: **500**
   - Click "Run Batch"

2. **Check Output**:

   - Open generated CSV: `gui_batch_results_[timestamp].csv`
   - Look at `FinalEntityCount` column

3. **Expected Results**:
   - ✓ Final count > 0 for most runs (not extinct)
   - ✓ Max count > 1 (reproduction occurred)
   - ✓ Average age > 50 (entities living long enough)
   - ✓ Variation in results (stochastic outcomes)

### Test 7: Export and Analysis

1. **Enable Entity Export** (Terminal):

   ```
   > /export details
   > /run 200
   > /export details
   ```

2. **Check CSV File**:

   - Open `entity_details_[timestamp].csv`
   - Should have rows for multiple steps
   - Check columns: Energy, Age, Speed, Mass, etc.

3. **Quick Analysis in Excel/Python**:

   ```python
   import pandas as pd
   data = pd.read_csv('entity_details_*.csv')

   print("Average survival:", data.groupby('EntityID')['Age'].max().mean())
   print("Reproduction count:", data['EntityID'].nunique())
   print("Biome distribution:", data['BiomeType'].value_counts())
   ```

4. **Expected Results**:
   - ✓ Multiple entities (reproduction happened)
   - ✓ Average survival > 50 steps
   - ✓ Energy levels fluctuate but don't crash to zero instantly
   - ✓ Entities present in multiple biome types

---

## Success Criteria Summary

The balance is correct if:

✓ **Survival**: Most entities live 100+ steps  
✓ **Reproduction**: Multiple generations occur  
✓ **Growth**: Population increases from 1 to 5-20+  
✓ **Stability**: Population doesn't crash to zero  
✓ **Diversity**: Entities in multiple biomes  
✓ **Variation**: Different traits in population  
✓ **Selection**: Biome preference visible (FOREST > GRASS > others)

---

## If Balance Still Seems Off

### Too Easy (Population explodes to 100+)

- Reduce food energy by 20%: GRASS 8→6, FOREST 10→8
- Increase metabolism: 0.05→0.07
- Reduce starting energy: 50→40

### Too Hard (Everyone dies in 50 steps)

- Increase food energy by 20%: GRASS 8→10, FOREST 10→12
- Reduce metabolism: 0.05→0.03
- Increase starting energy: 50→60
- Reduce movement cost constant: 0.1→0.08

### No Trait Variation

- Increase mutation strength: 0.1→0.15
- Increase mutation rate: 0.1→0.2

### No Biome Preference

- Increase food energy differences between biomes
- Increase movement modifier differences
- Increase environmental stress in harsh biomes

---

## Quick Visual Check

**In Terminal Mode after 100 steps:**

```
Step 100 | Population: 5-15 entities
╔════════════════════════════════════════════╗
║ ●●●             ◆                          ║
║     ●        ◆    ◆                        ║
║               ●                            ║
║    ◆  ◆          ●                         ║
║             ●                              ║
╚════════════════════════════════════════════╝
```

Where:

- ● = Entities (should see 5-15)
- ◆ = Food (should be scattered)
- Colors = Different biomes (GRASS, FOREST most populated)

---

## Verification Complete ✓

If tests pass, the simulation is properly balanced for:

- **Trait analysis**: Different traits lead to different outcomes
- **Spatial ecology**: Biomes matter for survival
- **Evolution**: Populations evolve over generations
- **Scientific analysis**: Data export enables research

The system is ready for systematic experiments! 🧬
