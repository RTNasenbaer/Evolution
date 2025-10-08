"""
Trait Analysis for New 4-Trait System
Analyzes how Endurance, Adaptation, Mobility, and Efficiency
correlate with survival in different biomes.
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pathlib import Path

# Set style
sns.set_style("whitegrid")
plt.rcParams['figure.figsize'] = (12, 8)

def load_data(filename='entity_details.csv'):
    """Load entity details CSV from data directory"""
    data_path = Path(__file__).parent.parent / 'data' / filename
    if not data_path.exists():
        print(f"Error: {filename} not found in data directory")
        print("Please run simulation with entity export enabled first.")
        return None
    
    df = pd.read_csv(data_path)
    print(f"Loaded {len(df)} entity records from {df['Step'].nunique()} steps")
    return df

def analyze_trait_biome_correlation(df):
    """Analyze which traits are most important in each biome"""
    print("\n" + "="*60)
    print("TRAIT-BIOME CORRELATION ANALYSIS")
    print("="*60)
    
    # Get survivors (final step)
    final_step = df['Step'].max()
    survivors = df[df['Step'] == final_step].copy()
    
    print(f"\nFinal Population: {len(survivors)} entities survived to step {final_step}")
    
    # Average traits by biome
    trait_cols = ['Endurance', 'Adaptation', 'Mobility', 'Efficiency']
    biome_traits = survivors.groupby('BiomeType')[trait_cols].agg(['mean', 'std', 'count'])
    
    print("\n--- Average Traits by Biome (Survivors) ---")
    print(biome_traits.round(3))
    
    # Plot heatmap
    fig, axes = plt.subplots(2, 2, figsize=(14, 10))
    fig.suptitle('Trait Distribution by Biome (Survivors)', fontsize=16, fontweight='bold')
    
    for idx, trait in enumerate(trait_cols):
        ax = axes[idx // 2, idx % 2]
        
        biome_mean = survivors.groupby('BiomeType')[trait].mean().sort_values(ascending=False)
        
        ax.barh(biome_mean.index, biome_mean.values, color=sns.color_palette("viridis", len(biome_mean)))
        ax.set_xlabel(f'{trait} Value', fontsize=12)
        ax.set_ylabel('Biome', fontsize=12)
        ax.set_title(f'{trait} by Biome', fontsize=14, fontweight='bold')
        ax.axvline(1.0, color='red', linestyle='--', alpha=0.5, label='Default (1.0)')
        ax.legend()
        ax.grid(axis='x', alpha=0.3)
    
    plt.tight_layout()
    plt.savefig('../output/trait_biome_correlation.png', dpi=300, bbox_inches='tight')
    print(f"\nSaved plot: output/trait_biome_correlation.png")
    plt.close()
    
    return biome_traits

def analyze_trait_survival(df):
    """Compare traits between survivors and casualties"""
    print("\n" + "="*60)
    print("SURVIVAL ANALYSIS")
    print("="*60)
    
    final_step = df['Step'].max()
    
    # Get entity IDs at start and end
    initial_entities = df[df['Step'] == 0]['EntityID'].unique()
    final_entities = df[df['Step'] == final_step]['EntityID'].unique()
    
    # Classify entities
    df_initial = df[df['Step'] == 0].copy()
    df_initial['Survived'] = df_initial['EntityID'].isin(final_entities)
    
    survivors = df_initial[df_initial['Survived']]
    casualties = df_initial[~df_initial['Survived']]
    
    print(f"\nInitial Population: {len(initial_entities)}")
    print(f"Survivors: {len(survivors)} ({len(survivors)/len(initial_entities)*100:.1f}%)")
    print(f"Casualties: {len(casualties)} ({len(casualties)/len(initial_entities)*100:.1f}%)")
    
    # Compare traits
    trait_cols = ['Endurance', 'Adaptation', 'Mobility', 'Efficiency']
    
    print("\n--- Trait Comparison: Survivors vs Casualties ---")
    comparison = pd.DataFrame({
        'Survivors': survivors[trait_cols].mean(),
        'Casualties': casualties[trait_cols].mean(),
        'Difference': survivors[trait_cols].mean() - casualties[trait_cols].mean()
    })
    print(comparison.round(3))
    
    # Statistical significance
    from scipy import stats
    print("\n--- T-Test Results (p-values) ---")
    for trait in trait_cols:
        t_stat, p_value = stats.ttest_ind(survivors[trait], casualties[trait])
        sig = "***" if p_value < 0.001 else "**" if p_value < 0.01 else "*" if p_value < 0.05 else "ns"
        print(f"{trait:12s}: p={p_value:.4f} {sig}")
    
    # Box plot comparison
    fig, axes = plt.subplots(2, 2, figsize=(14, 10))
    fig.suptitle('Trait Distribution: Survivors vs Casualties', fontsize=16, fontweight='bold')
    
    for idx, trait in enumerate(trait_cols):
        ax = axes[idx // 2, idx % 2]
        
        data_to_plot = [survivors[trait].dropna(), casualties[trait].dropna()]
        bp = ax.boxplot(data_to_plot, labels=['Survivors', 'Casualties'],
                        patch_artist=True, showmeans=True)
        
        bp['boxes'][0].set_facecolor('lightgreen')
        bp['boxes'][1].set_facecolor('lightcoral')
        
        ax.set_ylabel(f'{trait} Value', fontsize=12)
        ax.set_title(f'{trait}', fontsize=14, fontweight='bold')
        ax.axhline(1.0, color='blue', linestyle='--', alpha=0.3, label='Default')
        ax.grid(axis='y', alpha=0.3)
        ax.legend()
    
    plt.tight_layout()
    plt.savefig('../output/survival_traits.png', dpi=300, bbox_inches='tight')
    print(f"\nSaved plot: output/survival_traits.png")
    plt.close()
    
    return comparison

def analyze_population_by_quadrant(df):
    """Analyze population distribution across trait testing zones"""
    print("\n" + "="*60)
    print("QUADRANT ANALYSIS (Trait Testing Seed)")
    print("="*60)
    
    # Assuming world size = 50
    df = df.copy()
    df['Quadrant'] = 'CENTER'
    df.loc[(df['X'] < 25) & (df['Y'] < 25), 'Quadrant'] = 'NW-FOREST'
    df.loc[(df['X'] >= 25) & (df['Y'] < 25), 'Quadrant'] = 'NE-DESERT'
    df.loc[(df['X'] < 25) & (df['Y'] >= 25), 'Quadrant'] = 'SW-TUNDRA'
    df.loc[(df['X'] >= 25) & (df['Y'] >= 25), 'Quadrant'] = 'SE-MOUNTAIN'
    
    # Population over time by quadrant
    pop_time = df.groupby(['Step', 'Quadrant']).size().reset_index(name='Population')
    
    fig, ax = plt.subplots(figsize=(12, 6))
    
    for quadrant in pop_time['Quadrant'].unique():
        data = pop_time[pop_time['Quadrant'] == quadrant]
        ax.plot(data['Step'], data['Population'], marker='o', label=quadrant, linewidth=2)
    
    ax.set_xlabel('Simulation Step', fontsize=12)
    ax.set_ylabel('Population', fontsize=12)
    ax.set_title('Population by Quadrant Over Time', fontsize=14, fontweight='bold')
    ax.legend(loc='best', fontsize=10)
    ax.grid(alpha=0.3)
    
    plt.tight_layout()
    plt.savefig('../output/quadrant_population.png', dpi=300, bbox_inches='tight')
    print(f"\nSaved plot: output/quadrant_population.png")
    plt.close()
    
    # Final distribution
    final_step = df['Step'].max()
    final_dist = df[df['Step'] == final_step].groupby('Quadrant').size()
    
    print("\n--- Final Population by Quadrant ---")
    print(final_dist.sort_values(ascending=False))
    
    return final_dist

def analyze_trait_evolution(df):
    """Track how average traits change over time"""
    print("\n" + "="*60)
    print("TRAIT EVOLUTION OVER TIME")
    print("="*60)
    
    trait_cols = ['Endurance', 'Adaptation', 'Mobility', 'Efficiency']
    
    # Average traits per step
    trait_evolution = df.groupby('Step')[trait_cols].mean()
    
    fig, axes = plt.subplots(2, 2, figsize=(14, 10))
    fig.suptitle('Average Trait Values Over Time', fontsize=16, fontweight='bold')
    
    for idx, trait in enumerate(trait_cols):
        ax = axes[idx // 2, idx % 2]
        
        ax.plot(trait_evolution.index, trait_evolution[trait], 
                linewidth=2, color='blue', alpha=0.7)
        ax.axhline(1.0, color='red', linestyle='--', alpha=0.5, label='Default (1.0)')
        
        ax.set_xlabel('Simulation Step', fontsize=12)
        ax.set_ylabel(f'{trait} Value', fontsize=12)
        ax.set_title(f'{trait} Evolution', fontsize=14, fontweight='bold')
        ax.legend()
        ax.grid(alpha=0.3)
    
    plt.tight_layout()
    plt.savefig('../output/trait_evolution.png', dpi=300, bbox_inches='tight')
    print(f"\nSaved plot: output/trait_evolution.png")
    plt.close()
    
    # Show trend
    print("\n--- Trait Trend (Initial vs Final) ---")
    initial = trait_evolution.iloc[0]
    final = trait_evolution.iloc[-1]
    change = final - initial
    
    trend_df = pd.DataFrame({
        'Initial': initial,
        'Final': final,
        'Change': change,
        'Change%': (change / initial * 100)
    })
    print(trend_df.round(3))
    
    return trait_evolution

def main():
    """Run all analyses"""
    print("="*60)
    print("EVOLUTION SIMULATION - 4-TRAIT ANALYSIS")
    print("="*60)
    
    # Load data
    df = load_data()
    if df is None:
        return
    
    # Create output directory
    Path('../output').mkdir(exist_ok=True)
    
    # Run analyses
    try:
        biome_traits = analyze_trait_biome_correlation(df)
        survival_comparison = analyze_trait_survival(df)
        quadrant_dist = analyze_population_by_quadrant(df)
        trait_evolution = analyze_trait_evolution(df)
        
        print("\n" + "="*60)
        print("ANALYSIS COMPLETE")
        print("="*60)
        print("\nAll plots saved to output/ directory:")
        print("  - trait_biome_correlation.png")
        print("  - survival_traits.png")
        print("  - quadrant_population.png")
        print("  - trait_evolution.png")
        
    except Exception as e:
        print(f"\nError during analysis: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
