#!/usr/bin/env python3
"""
Evolution Simulation Analysis - Comprehensive Analysis Script
Analyzes the survival ability of organisms by varying specific traits under different spatial conditions

This script processes CSV exports from the cellular automata evolution simulation and generates
beautiful, informative visualizations for understanding trait-survival relationships.
"""

import os
# Suppress Qt Wayland warnings on Linux
os.environ['QT_QPA_PLATFORM'] = 'xcb'

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path
from typing import Dict, List, Tuple
import warnings
warnings.filterwarnings('ignore')

# Set style
plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")

class EvolutionAnalyzer:
    """Comprehensive analyzer for evolution simulation data"""
    
    def __init__(self, data_dir: str = "../data"):
        """Initialize analyzer with data directory"""
        self.data_dir = Path(data_dir)
        self.entity_data = None
        self.biome_data = None
        self.batch_data = None
        
    def load_data(self, entity_file: str = None, biome_file: str = None, batch_file: str = None):
        """Load CSV data files"""
        print("Loading data files...")
        
        # Auto-detect files if not specified
        if entity_file is None:
            entity_files = list(self.data_dir.glob("entity_details_*.csv"))
            if entity_files:
                entity_file = str(max(entity_files, key=lambda p: p.stat().st_mtime))
        
        if biome_file is None:
            biome_files = list(self.data_dir.glob("biome_details_*.csv"))
            if biome_files:
                biome_file = str(max(biome_files, key=lambda p: p.stat().st_mtime))
        
        if batch_file is None:
            batch_files = list(self.data_dir.glob("*batch_results*.csv"))
            if batch_files:
                batch_file = str(max(batch_files, key=lambda p: p.stat().st_mtime))
        
        # Load entity details
        if entity_file and Path(entity_file).exists():
            self.entity_data = pd.read_csv(entity_file)
            print(f"✓ Loaded entity data: {len(self.entity_data)} records")
        else:
            print("✗ No entity details file found")
        
        # Load biome details
        if biome_file and Path(biome_file).exists():
            self.biome_data = pd.read_csv(biome_file)
            print(f"✓ Loaded biome data: {len(self.biome_data)} records")
        else:
            print("✗ No biome details file found")
        
        # Load batch results
        if batch_file and Path(batch_file).exists():
            self.batch_data = pd.read_csv(batch_file)
            print(f"✓ Loaded batch data: {len(self.batch_data)} simulations")
        else:
            print("✗ No batch results file found")
    
    def plot_trait_survival_correlation(self, output_file: str = "trait_survival.png"):
        """Analyze correlation between traits and survival"""
        if self.entity_data is None:
            print("⚠ Entity data not loaded. Skipping trait-survival analysis.")
            return
        
        print("\n📊 Analyzing trait-survival correlations...")
        
        # Calculate survival metrics per entity
        entity_stats = self.entity_data.groupby('EntityID').agg({
            'Age': 'max',
            'Energy': 'mean',
            'Speed': 'mean',
            'Mass': 'mean',
            'EnergyEfficiency': 'mean',
            'SightRange': 'mean',
            'MetabolismRate': 'mean',
            'ReproductionThreshold': 'mean',
            'MaxLifespan': 'max',
            'Step': 'count'  # Number of steps survived
        }).reset_index()
        
        entity_stats.rename(columns={'Step': 'StepsSurvived'}, inplace=True)
        
        # Calculate survival score (combination of age and steps)
        entity_stats['SurvivalScore'] = (entity_stats['Age'] / entity_stats['MaxLifespan']) * \
                                        np.log1p(entity_stats['StepsSurvived'])
        
        # Traits to analyze
        traits = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 
                 'MetabolismRate', 'ReproductionThreshold']
        
        fig, axes = plt.subplots(2, 3, figsize=(18, 12))
        fig.suptitle('Trait-Survival Correlation Analysis\nCellular Automata Evolution Simulation', 
                     fontsize=16, fontweight='bold')
        
        for idx, trait in enumerate(traits):
            ax = axes[idx // 3, idx % 3]
            
            # Scatter plot with regression line
            sns.regplot(data=entity_stats, x=trait, y='SurvivalScore', 
                       ax=ax, scatter_kws={'alpha': 0.3, 's': 20},
                       line_kws={'color': 'red', 'linewidth': 2})
            
            # Calculate correlation
            corr = entity_stats[trait].corr(entity_stats['SurvivalScore'])
            
            ax.set_title(f'{trait}\nCorrelation: {corr:.3f}', fontweight='bold')
            ax.set_xlabel(trait, fontsize=11)
            ax.set_ylabel('Survival Score', fontsize=11)
            ax.grid(True, alpha=0.3)
            
            # Add trend annotation
            trend = "↗ Positive" if corr > 0.1 else "↘ Negative" if corr < -0.1 else "→ Neutral"
            ax.text(0.05, 0.95, trend, transform=ax.transAxes, 
                   bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5),
                   verticalalignment='top')
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_biome_performance(self, output_file: str = "biome_performance.png"):
        """Analyze organism performance across different biomes"""
        if self.entity_data is None:
            print("⚠ Entity data not loaded. Skipping biome analysis.")
            return
        
        print("\n🌍 Analyzing biome-specific performance...")
        
        # Calculate average survival metrics per biome
        biome_stats = self.entity_data.groupby('BiomeType').agg({
            'Age': 'mean',
            'Energy': 'mean',
            'Speed': 'mean',
            'Mass': 'mean',
            'EnergyEfficiency': 'mean',
            'Step': 'count'
        }).reset_index()
        
        biome_stats.rename(columns={'Step': 'ObservationCount'}, inplace=True)
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Organism Performance Across Biomes\nSpatial Condition Analysis', 
                     fontsize=16, fontweight='bold')
        
        # 1. Average Age by Biome
        ax = axes[0, 0]
        biome_stats_sorted = biome_stats.sort_values('Age', ascending=False)
        bars = ax.barh(biome_stats_sorted['BiomeType'], biome_stats_sorted['Age'])
        ax.set_xlabel('Average Age (ticks)', fontsize=11)
        ax.set_title('Average Organism Age by Biome', fontweight='bold')
        ax.grid(True, axis='x', alpha=0.3)
        
        # Color bars by value
        colors = plt.cm.RdYlGn(np.linspace(0.3, 0.9, len(bars)))
        for bar, color in zip(bars, colors):
            bar.set_color(color)
        
        # 2. Average Energy by Biome
        ax = axes[0, 1]
        biome_stats_sorted = biome_stats.sort_values('Energy', ascending=False)
        bars = ax.barh(biome_stats_sorted['BiomeType'], biome_stats_sorted['Energy'])
        ax.set_xlabel('Average Energy', fontsize=11)
        ax.set_title('Average Organism Energy by Biome', fontweight='bold')
        ax.grid(True, axis='x', alpha=0.3)
        
        colors = plt.cm.plasma(np.linspace(0.2, 0.9, len(bars)))
        for bar, color in zip(bars, colors):
            bar.set_color(color)
        
        # 3. Observation Count (Population Density)
        ax = axes[1, 0]
        biome_stats_sorted = biome_stats.sort_values('ObservationCount', ascending=False)
        bars = ax.barh(biome_stats_sorted['BiomeType'], biome_stats_sorted['ObservationCount'])
        ax.set_xlabel('Total Observations', fontsize=11)
        ax.set_title('Population Density by Biome', fontweight='bold')
        ax.grid(True, axis='x', alpha=0.3)
        
        colors = plt.cm.viridis(np.linspace(0.2, 0.9, len(bars)))
        for bar, color in zip(bars, colors):
            bar.set_color(color)
        
        # 4. Trait Heatmap by Biome
        ax = axes[1, 1]
        trait_cols = ['Speed', 'Mass', 'EnergyEfficiency']
        heatmap_data = biome_stats.set_index('BiomeType')[trait_cols]
        
        # Normalize for heatmap
        heatmap_norm = (heatmap_data - heatmap_data.min()) / (heatmap_data.max() - heatmap_data.min())
        
        sns.heatmap(heatmap_norm.T, annot=heatmap_data.T, fmt='.2f', 
                   cmap='YlOrRd', ax=ax, cbar_kws={'label': 'Normalized Value'})
        ax.set_title('Average Trait Values by Biome', fontweight='bold')
        ax.set_ylabel('Trait', fontsize=11)
        ax.set_xlabel('Biome Type', fontsize=11)
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_trait_evolution_over_time(self, output_file: str = "trait_evolution.png"):
        """Analyze how traits evolve over simulation time"""
        if self.entity_data is None:
            print("⚠ Entity data not loaded. Skipping temporal analysis.")
            return
        
        print("\n⏱️  Analyzing trait evolution over time...")
        
        # Calculate average traits per step
        time_stats = self.entity_data.groupby('Step').agg({
            'Speed': 'mean',
            'Mass': 'mean',
            'EnergyEfficiency': 'mean',
            'SightRange': 'mean',
            'MetabolismRate': 'mean',
            'ReproductionThreshold': 'mean',
            'Energy': 'mean',
            'EntityID': 'count'  # Population size
        }).reset_index()
        
        time_stats.rename(columns={'EntityID': 'Population'}, inplace=True)
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Trait Evolution Over Simulation Time\nTemporal Dynamics Analysis', 
                     fontsize=16, fontweight='bold')
        
        # 1. Population and Average Energy
        ax1 = axes[0, 0]
        ax2 = ax1.twinx()
        
        line1 = ax1.plot(time_stats['Step'], time_stats['Population'], 
                        'b-', linewidth=2, label='Population', alpha=0.7)
        line2 = ax2.plot(time_stats['Step'], time_stats['Energy'], 
                        'r-', linewidth=2, label='Avg Energy', alpha=0.7)
        
        ax1.set_xlabel('Simulation Step', fontsize=11)
        ax1.set_ylabel('Population Count', color='b', fontsize=11)
        ax2.set_ylabel('Average Energy', color='r', fontsize=11)
        ax1.tick_params(axis='y', labelcolor='b')
        ax2.tick_params(axis='y', labelcolor='r')
        ax1.set_title('Population Dynamics & Energy Levels', fontweight='bold')
        ax1.grid(True, alpha=0.3)
        
        # Combined legend
        lines = line1 + line2
        labels = [l.get_label() for l in lines]
        ax1.legend(lines, labels, loc='upper left')
        
        # 2. Movement Traits Evolution
        ax = axes[0, 1]
        ax.plot(time_stats['Step'], time_stats['Speed'], label='Speed', linewidth=2)
        ax.plot(time_stats['Step'], time_stats['Mass'], label='Mass', linewidth=2)
        ax.set_xlabel('Simulation Step', fontsize=11)
        ax.set_ylabel('Trait Value', fontsize=11)
        ax.set_title('Movement Traits Evolution', fontweight='bold')
        ax.legend()
        ax.grid(True, alpha=0.3)
        
        # 3. Efficiency Traits Evolution
        ax = axes[1, 0]
        ax.plot(time_stats['Step'], time_stats['EnergyEfficiency'], 
               label='Energy Efficiency', linewidth=2)
        ax.plot(time_stats['Step'], time_stats['MetabolismRate'] * 100, 
               label='Metabolism Rate (×100)', linewidth=2)
        ax.set_xlabel('Simulation Step', fontsize=11)
        ax.set_ylabel('Trait Value', fontsize=11)
        ax.set_title('Efficiency Traits Evolution', fontweight='bold')
        ax.legend()
        ax.grid(True, alpha=0.3)
        
        # 4. Survival Traits Evolution
        ax = axes[1, 1]
        ax.plot(time_stats['Step'], time_stats['SightRange'], 
               label='Sight Range', linewidth=2)
        ax.plot(time_stats['Step'], time_stats['ReproductionThreshold'], 
               label='Reproduction Threshold', linewidth=2)
        ax.set_xlabel('Simulation Step', fontsize=11)
        ax.set_ylabel('Trait Value', fontsize=11)
        ax.set_title('Survival Strategy Traits Evolution', fontweight='bold')
        ax.legend()
        ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_trait_correlation_matrix(self, output_file: str = "trait_correlations.png"):
        """Create correlation matrix for all traits"""
        if self.entity_data is None:
            print("⚠ Entity data not loaded. Skipping correlation matrix.")
            return
        
        print("\n🔗 Analyzing trait correlations...")
        
        # Select numeric trait columns
        trait_cols = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 
                     'MetabolismRate', 'ReproductionThreshold', 'Energy', 'Age']
        
        # Calculate correlation matrix
        corr_matrix = self.entity_data[trait_cols].corr()
        
        fig, ax = plt.subplots(figsize=(12, 10))
        
        # Create heatmap
        sns.heatmap(corr_matrix, annot=True, fmt='.2f', cmap='coolwarm', 
                   center=0, square=True, linewidths=1, cbar_kws={"shrink": 0.8},
                   ax=ax, vmin=-1, vmax=1)
        
        ax.set_title('Trait Correlation Matrix\nInteraction Between Organism Traits', 
                    fontsize=16, fontweight='bold', pad=20)
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_spatial_distribution(self, output_file: str = "spatial_distribution.png"):
        """Visualize spatial distribution of organisms"""
        if self.entity_data is None:
            print("⚠ Entity data not loaded. Skipping spatial analysis.")
            return
        
        print("\n🗺️  Analyzing spatial distribution...")
        
        # Get last step data for spatial snapshot
        last_step = self.entity_data['Step'].max()
        snapshot = self.entity_data[self.entity_data['Step'] == last_step].copy()
        
        if len(snapshot) == 0:
            print("⚠ No data for final step. Skipping spatial distribution.")
            return
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 14))
        fig.suptitle(f'Spatial Distribution Analysis (Step {last_step})\nCellular Automata Grid', 
                     fontsize=16, fontweight='bold')
        
        # 1. Energy Distribution
        ax = axes[0, 0]
        scatter = ax.scatter(snapshot['X'], snapshot['Y'], 
                           c=snapshot['Energy'], cmap='plasma', 
                           s=100, alpha=0.6, edgecolors='black', linewidth=0.5)
        ax.set_xlabel('X Position', fontsize=11)
        ax.set_ylabel('Y Position', fontsize=11)
        ax.set_title('Energy Distribution Across Grid', fontweight='bold')
        ax.set_aspect('equal')
        plt.colorbar(scatter, ax=ax, label='Energy Level')
        ax.grid(True, alpha=0.2)
        
        # 2. Age Distribution
        ax = axes[0, 1]
        scatter = ax.scatter(snapshot['X'], snapshot['Y'], 
                           c=snapshot['Age'], cmap='viridis', 
                           s=100, alpha=0.6, edgecolors='black', linewidth=0.5)
        ax.set_xlabel('X Position', fontsize=11)
        ax.set_ylabel('Y Position', fontsize=11)
        ax.set_title('Age Distribution Across Grid', fontweight='bold')
        ax.set_aspect('equal')
        plt.colorbar(scatter, ax=ax, label='Age (ticks)')
        ax.grid(True, alpha=0.2)
        
        # 3. Biome Distribution
        ax = axes[1, 0]
        biome_colors = {biome: i for i, biome in enumerate(snapshot['BiomeType'].unique())}
        colors = [biome_colors[b] for b in snapshot['BiomeType']]
        scatter = ax.scatter(snapshot['X'], snapshot['Y'], 
                           c=colors, cmap='tab10', 
                           s=100, alpha=0.6, edgecolors='black', linewidth=0.5)
        ax.set_xlabel('X Position', fontsize=11)
        ax.set_ylabel('Y Position', fontsize=11)
        ax.set_title('Organism Distribution by Biome', fontweight='bold')
        ax.set_aspect('equal')
        
        # Create legend for biomes
        handles = [plt.Line2D([0], [0], marker='o', color='w', 
                             markerfacecolor=plt.cm.tab10(biome_colors[biome]/10), 
                             markersize=10, label=biome) 
                  for biome in biome_colors.keys()]
        ax.legend(handles=handles, loc='center left', bbox_to_anchor=(1, 0.5))
        ax.grid(True, alpha=0.2)
        
        # 4. Speed Distribution
        ax = axes[1, 1]
        scatter = ax.scatter(snapshot['X'], snapshot['Y'], 
                           c=snapshot['Speed'], cmap='coolwarm', 
                           s=100, alpha=0.6, edgecolors='black', linewidth=0.5)
        ax.set_xlabel('X Position', fontsize=11)
        ax.set_ylabel('Y Position', fontsize=11)
        ax.set_title('Speed Trait Distribution Across Grid', fontweight='bold')
        ax.set_aspect('equal')
        plt.colorbar(scatter, ax=ax, label='Speed')
        ax.grid(True, alpha=0.2)
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def generate_summary_report(self, output_file: str = "analysis_summary.txt"):
        """Generate text summary of analysis"""
        print("\n📄 Generating summary report...")
        
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("="*80 + "\n")
            f.write(" EVOLUTION SIMULATION ANALYSIS SUMMARY\n")
            f.write(" Cellular Automata - Trait-based Survival Analysis\n")
            f.write("="*80 + "\n\n")
            
            if self.entity_data is not None:
                f.write("ENTITY DATA ANALYSIS\n")
                f.write("-" * 40 + "\n")
                f.write(f"Total entity observations: {len(self.entity_data)}\n")
                f.write(f"Unique entities tracked: {self.entity_data['EntityID'].nunique()}\n")
                f.write(f"Simulation steps recorded: {self.entity_data['Step'].nunique()}\n")
                f.write(f"Step range: {self.entity_data['Step'].min()} - {self.entity_data['Step'].max()}\n\n")
                
                f.write("TRAIT STATISTICS\n")
                f.write("-" * 40 + "\n")
                traits = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 
                         'MetabolismRate', 'ReproductionThreshold', 'Energy', 'Age']
                for trait in traits:
                    f.write(f"\n{trait}:\n")
                    f.write(f"  Mean: {self.entity_data[trait].mean():.3f}\n")
                    f.write(f"  Std:  {self.entity_data[trait].std():.3f}\n")
                    f.write(f"  Min:  {self.entity_data[trait].min():.3f}\n")
                    f.write(f"  Max:  {self.entity_data[trait].max():.3f}\n")
                
                f.write("\n" + "="*80 + "\n\n")
                
                f.write("BIOME DISTRIBUTION\n")
                f.write("-" * 40 + "\n")
                biome_dist = self.entity_data['BiomeType'].value_counts()
                for biome, count in biome_dist.items():
                    pct = (count / len(self.entity_data)) * 100
                    f.write(f"{biome:15s}: {count:6d} observations ({pct:5.2f}%)\n")
            
            if self.biome_data is not None:
                f.write("\n" + "="*80 + "\n\n")
                f.write("BIOME ENVIRONMENT DATA\n")
                f.write("-" * 40 + "\n")
                f.write(f"Total biome snapshots: {len(self.biome_data)}\n")
                f.write(f"Steps recorded: {self.biome_data['Step'].nunique()}\n\n")
            
            if self.batch_data is not None:
                f.write("\n" + "="*80 + "\n\n")
                f.write("BATCH SIMULATION SUMMARY\n")
                f.write("-" * 40 + "\n")
                f.write(f"Total simulations: {len(self.batch_data)}\n")
                f.write(f"Average steps run: {self.batch_data['StepsRun'].mean():.1f}\n")
                f.write(f"Average max entity count: {self.batch_data['MaxEntityCount'].mean():.1f}\n")
                f.write(f"Average final entity count: {self.batch_data['FinalEntityCount'].mean():.1f}\n")
                f.write(f"Average execution time: {self.batch_data['ExecutionTime(ms)'].mean():.1f} ms\n")
            
            f.write("\n" + "="*80 + "\n")
            f.write("Analysis complete.\n")
        
        print(f"✓ Saved: {output_file}")
    
    def run_complete_analysis(self, output_dir: str = "output/analysis"):
        """Run all analyses and save outputs"""
        print("\n" + "="*80)
        print(" EVOLUTION SIMULATION - COMPREHENSIVE ANALYSIS")
        print("="*80)
        
        # Create output directory
        output_path = Path(output_dir)
        output_path.mkdir(parents=True, exist_ok=True)
        print(f"\n📁 Output directory: {output_path.absolute()}")
        
        # Run all analyses
        self.plot_trait_survival_correlation(str(output_path / "01_trait_survival.png"))
        self.plot_biome_performance(str(output_path / "02_biome_performance.png"))
        self.plot_trait_evolution_over_time(str(output_path / "03_trait_evolution.png"))
        self.plot_trait_correlation_matrix(str(output_path / "04_trait_correlations.png"))
        self.plot_spatial_distribution(str(output_path / "05_spatial_distribution.png"))
        self.generate_summary_report(str(output_path / "00_analysis_summary.txt"))
        
        print("\n" + "="*80)
        print("✅ Analysis complete! Check the output directory for results.")
        print("="*80 + "\n")


def main():
    """Main execution function"""
    import sys
    
    # Parse command line arguments
    data_dir = sys.argv[1] if len(sys.argv) > 1 else "../data"
    output_dir = sys.argv[2] if len(sys.argv) > 2 else "output/analysis"
    
    # Create analyzer
    analyzer = EvolutionAnalyzer(data_dir)
    
    # Load data
    analyzer.load_data()
    
    # Check if we have data
    if analyzer.entity_data is None and analyzer.biome_data is None and analyzer.batch_data is None:
        print("\n⚠️  ERROR: No data files found!")
        print("\nExpected files:")
        print("  - entity_details_*.csv")
        print("  - biome_details_*.csv")
        print("  - *batch_results*.csv")
        print("\nUsage: python analyze_evolution.py [data_dir] [output_dir]")
        sys.exit(1)
    
    # Run analysis
    analyzer.run_complete_analysis(output_dir)


if __name__ == "__main__":
    main()
