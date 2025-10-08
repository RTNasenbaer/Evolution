# Evolution Simulation - Documentation Index

---

## ⛔ STOP! READ THIS FIRST (AI ASSISTANTS)

**THIS PROJECT HAS EXACTLY 11 MARKDOWN FILES. DO NOT CREATE MORE.**

If another AI instance created files like:

- ❌ `CHANGELOG_*.md`
- ❌ `QUICK_START_*.md`
- ❌ `IMPLEMENTATION_*.md`
- ❌ `TRAIT_SYSTEM_*.md`
- ❌ `MIGRATION_*.md`
- ❌ `REFACTORING_*.md`

**DELETE THEM IMMEDIATELY.** Then update this INDEX.

**To add content:** Update ARCHITECTURE.md, UI_GUIDE.md, or BALANCE_ADJUSTMENTS.md

**File count check:** Run `(Get-ChildItem -Recurse -Filter "*.md").Count` → Must = 11

---

## 📚 Complete Documentation Guide

Complete guide to all documentation for the Evolution cellular automata simulation.

---

## 🚀 Getting Started (Start Here!)

### [GETTING_STARTED.md](GETTING_STARTED.md)

**Purpose:** First-time setup, compilation, and running the simulation

**Contents:**

- Installation prerequisites (Java, JavaFX)
- Compilation instructions (compile.bat/sh)
- Running terminal, GUI, and World Builder modes
- Basic commands and controls
- Troubleshooting common issues

**Read this first if:** You've just cloned the repository

---

### [CROSS_PLATFORM.md](CROSS_PLATFORM.md)

**Purpose:** Platform-specific setup for Windows, macOS, and Linux

**Contents:**

- Windows-specific instructions (PowerShell, Command Prompt)
- macOS setup (homebrew, Java installation)
- Linux distribution guides (Ubuntu, Fedora, Arch)
- JavaFX platform differences
- Path separator differences (`;` vs `:`)

**Read this if:** You're setting up on a non-Windows platform

---

## 🎮 Using the Simulation

### [UI_GUIDE.md](UI_GUIDE.md)

**Purpose:** Complete interface guide for all three modes

**Contents:**

- **Terminal Mode:** All commands, display format, ANSI colors
- **GUI Mode:** Button descriptions, charts, zoom/pan, batch simulation
- **World Builder:** Biome painting, brush tools, seed export/import
- Interface comparisons and feature matrix

**Read this if:** You want to learn all available commands and features

---

### [SEED_SYSTEM.md](SEED_SYSTEM.md)

**Purpose:** Understanding and using world seeds for reproducibility

**Contents:**

- Seed format and structure
- Deterministic world generation
- Exporting seeds from GUI and World Builder
- Importing seed files (`.seed` format)
- Creating custom seeds
- Example seeds in `examples/` folder

**Read this if:** You want reproducible worlds or custom terrain designs

---

## 📊 Data Analysis & Research

### [ANALYSIS.md](ANALYSIS.md)

**Purpose:** Complete guide to Python analysis scripts

**Contents:**

- Setup instructions (Conda, pip, venv)
- Script descriptions:
  - `analyze_evolution.py` - Comprehensive single-run analysis
  - `batch_comparison.py` - Multi-seed comparison
  - `statistical_analysis.py` - Hypothesis testing
  - `interactive_analysis.ipynb` - Jupyter notebook
- Visualization examples
- Advanced usage (custom aggregations, survival prediction)
- Troubleshooting Python environment issues

**Read this if:** You want to analyze simulation data with Python

---

### [DATA_ANALYSIS.md](DATA_ANALYSIS.md)

**Purpose:** CSV export formats and data science techniques

**Contents:**

- CSV export formats (entity_details, biome_details, batch_results)
- CSVExporter utility usage
- Batch simulation setup (terminal and GUI)
- Analysis techniques (population dynamics, trait evolution, spatial analysis)
- Experimental design templates
- Statistical analysis examples
- Performance considerations for large datasets

**Read this if:** You're doing custom data analysis or research

---

## 🔧 Technical Documentation

### [ARCHITECTURE.md](ARCHITECTURE.md)

**Purpose:** Technical implementation details and class structure

**Contents:**

- Core packages (entities, world, ui, export)
- Class hierarchy and responsibilities
- Entity trait system implementation
- World generation algorithms
- Energy system mechanics
- CSV export architecture
- GUI component structure

**Read this if:** You're modifying the codebase or contributing

---

### [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md)

**Purpose:** Simulation parameter reference and tuning guide

**Contents:**

- Energy system values (food, movement costs, metabolism)
- Trait ranges and mutation rates
- Biome characteristics (food spawn rates, difficulty multipliers)
- Reproduction thresholds
- Balance rationale and testing results
- Parameter modification guide

**Read this if:** You're adjusting simulation balance or researching trait impacts

---

### [GIT_GUIDE.md](GIT_GUIDE.md)

**Purpose:** Git configuration and version control best practices

**Contents:**

- `.gitignore` and `.gitattributes` explanation
- What should/shouldn't be tracked
- Directory structure for Git
- Commit message guidelines
- Useful Git commands
- Branching strategies
- Troubleshooting common Git issues

**Read this if:** You're contributing to the project or managing version control

---

## 📁 Documentation Structure Summary

```
docs/
├── INDEX.md                    ← You are here
├── GETTING_STARTED.md          ← Setup and first run
├── CROSS_PLATFORM.md           ← Platform-specific guides
├── UI_GUIDE.md                 ← Interface reference
├── SEED_SYSTEM.md              ← World generation
├── ANALYSIS.md                 ← Python analysis tools ⭐
├── DATA_ANALYSIS.md            ← CSV formats & techniques
├── ARCHITECTURE.md             ← Technical implementation
├── BALANCE_ADJUSTMENTS.md      ← Parameter reference
└── GIT_GUIDE.md                ← Git & version control

../analysis/
└── README.md                   ← Analysis scripts quick start
```

---

## 🎯 Quick Reference by Task

### "I want to run the simulation for the first time"

→ [GETTING_STARTED.md](GETTING_STARTED.md)

### "I'm on macOS/Linux"

→ [CROSS_PLATFORM.md](CROSS_PLATFORM.md)

### "What do all these buttons do?"

→ [UI_GUIDE.md](UI_GUIDE.md)

### "How do I create a custom world?"

→ [SEED_SYSTEM.md](SEED_SYSTEM.md) + [UI_GUIDE.md](UI_GUIDE.md) (World Builder section)

### "I want to analyze my data with Python"

→ [ANALYSIS.md](ANALYSIS.md)

### "What do the CSV columns mean?"

→ [DATA_ANALYSIS.md](DATA_ANALYSIS.md)

### "I want to modify the code"

→ [ARCHITECTURE.md](ARCHITECTURE.md)

### "How do I change entity speed/energy costs?"

→ [BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md)

### "How do I use Git with this project?"

→ [GIT_GUIDE.md](GIT_GUIDE.md)

---

## 📖 Documentation Quality Standards

All documentation follows these principles:

✅ **Clear Purpose** - Each document has a specific focus  
✅ **No Duplication** - Information appears in one authoritative location  
✅ **Cross-References** - Related docs link to each other  
✅ **Examples** - Code samples and usage examples provided  
✅ **Up-to-Date** - Reflects current implementation

---

## 🔄 Documentation Maintenance

### Adding New Features

When adding features, update:

1. **ARCHITECTURE.md** - Technical implementation
2. **UI_GUIDE.md** - If UI changes
3. **BALANCE_ADJUSTMENTS.md** - If parameters change
4. **ANALYSIS.md** - If data export changes

### Deprecated Features

Mark deprecated sections with:

```markdown
⚠️ **DEPRECATED:** This feature will be removed in future versions.
Use [alternative] instead.
```

---

## 📝 Contributing to Documentation

Improvements welcome! When contributing:

1. **Check this INDEX** - Ensure your content belongs in the right file
2. **Avoid Duplication** - Reference existing docs rather than repeating
3. **Use Clear Headers** - Follow existing formatting
4. **Include Examples** - Show, don't just tell
5. **Test Instructions** - Verify commands actually work

---

## 🌟 Featured Documentation

### For Students & Researchers

- **[ANALYSIS.md](ANALYSIS.md)** - Publication-quality analysis pipeline
- **[DATA_ANALYSIS.md](DATA_ANALYSIS.md)** - Experimental design templates

### For Developers

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Complete codebase overview
- **[BALANCE_ADJUSTMENTS.md](BALANCE_ADJUSTMENTS.md)** - Parameter tuning

### For Educators

- **[GETTING_STARTED.md](GETTING_STARTED.md)** - Classroom setup guide
- **[UI_GUIDE.md](UI_GUIDE.md)** - Student-friendly interface reference

---

**Need help?** Start with [GETTING_STARTED.md](GETTING_STARTED.md) and use this index to find specific topics!

---

## ⚠️ DOCUMENTATION POLICY FOR AI ASSISTANTS

**This project maintains EXACTLY 11 markdown files. DO NOT create new documentation files.**

If you need to add content:

1. ✅ Update an existing file (ARCHITECTURE, UI_GUIDE, ANALYSIS, etc.)
2. ✅ Add a section to the appropriate document
3. ✅ Update this INDEX with links to new sections
4. ❌ DO NOT create: QUICK_START.md, SETUP_GUIDE.md, IMPLEMENTATION_SUMMARY.md, etc.

**Forbidden patterns:**

- Implementation summaries, continuation prompts, refactoring notes
- Setup variations (Windows/Conda/pip guides - use CROSS_PLATFORM or ANALYSIS)
- Git guides (not project-specific)
- Data format docs (use DATA_ANALYSIS.md)
- Feature-specific files (merge into appropriate existing doc)

**Current file count: 11 (1 root + 9 docs/ + 1 analysis/)**

When in doubt, update an existing file! 📖
