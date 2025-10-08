#!/usr/bin/env python3
"""
Batch Simulation Comparison - Multi-Seed Analysis
Compares multiple simulation runs to identify successful trait combinations and seed patterns
"""

import os
# Suppress Qt Wayland warnings on Linux
os.environ['QT_QPA_PLATFORM'] = 'xcb'

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path
import warnings
from typing import Optional
warnings.filterwarnings('ignore')

plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")


class BatchComparator:
    """Analyze and compare multiple batch simulation runs"""
    
    def __init__(self, data_dir: str = "../data"):
        self.data_dir = Path(data_dir)
        self.batch_data = None
    
    def load_batch_data(self, batch_file: Optional[str] = None):
        """Load batch results CSV and aggregate entity-level data"""
        print("Loading batch simulation data...")
        
        if batch_file is None:
            batch_files = list(self.data_dir.glob("*batch*.csv"))
            if batch_files:
                batch_file = str(max(batch_files, key=lambda p: p.stat().st_mtime))
        
        if batch_file and Path(batch_file).exists():
            # Load raw entity-level data
            raw_data = pd.read_csv(batch_file)
            print(f"✓ Loaded: {len(raw_data)} entity records")
            
            # Aggregate to seed level for comparison
            # Keep simulation metadata (same for all entities in a run)
            meta_cols = ['FinalEntityCount', 'MaxEntityCount', 'StepsRun', 'ExecutionTime(ms)']
            metadata = raw_data.groupby('Seed')[meta_cols].first()
            
            # Calculate trait averages per seed
            trait_cols = ['Energy', 'Age', 'Endurance', 'Adaptation', 'Mobility', 'Efficiency']
            
            trait_avgs = raw_data.groupby('Seed')[trait_cols].mean()
            trait_avgs.columns = ['Avg' + col for col in trait_avgs.columns]
            
            # Combine metadata and trait averages
            self.batch_data = metadata.join(trait_avgs).reset_index()
            
            # Store raw data for detailed analysis
            self.raw_entity_data = raw_data
            
            print(f"✓ Aggregated into {len(self.batch_data)} simulation runs")
        else:
            print("✗ No batch results file found")
            return False
        
        return True
    
    def plot_seed_performance_comparison(self, output_file: str = "seed_performance.png"):
        """Compare performance across different seeds"""
        if self.batch_data is None:
            return
        
        print("\n🌱 Analyzing seed performance...")
        
        # Group by seed
        seed_stats = self.batch_data.groupby('Seed').agg({
            'FinalEntityCount': ['mean', 'std'],
            'MaxEntityCount': ['mean', 'max'],
            'StepsRun': 'mean',
            'ExecutionTime(ms)': 'mean'
        }).reset_index()
        
        seed_stats.columns = ['Seed', 'AvgFinalCount', 'StdFinalCount', 
                             'AvgMaxCount', 'PeakMaxCount', 'AvgSteps', 'AvgExecTime']
        
        # Sort by success metric (weighted combination)
        seed_stats['SuccessScore'] = (seed_stats['AvgFinalCount'] * 2 + 
                                      seed_stats['AvgMaxCount'] + 
                                      seed_stats['AvgSteps'] / 100)
        seed_stats = seed_stats.sort_values('SuccessScore', ascending=False)
        
        fig, axes = plt.subplots(2, 2, figsize=(16, 12))
        fig.suptitle('Seed Performance Comparison\nMulti-Run Analysis', 
                     fontsize=16, fontweight='bold')
        
        # 1. Success Score by Seed
        ax = axes[0, 0]
        top_seeds = seed_stats.head(20)
        bars = ax.barh(range(len(top_seeds)), top_seeds['SuccessScore'])
        ax.set_yticks(range(len(top_seeds)))
        ax.set_yticklabels([f"Seed {int(s)}" for s in top_seeds['Seed']])
        ax.set_xlabel('Success Score (composite metric)', fontsize=11)
        ax.set_title('Top 20 Seeds by Success Score', fontweight='bold')
        ax.grid(True, axis='x', alpha=0.3)
        
        # Color gradient
        colors = plt.cm.get_cmap('RdYlGn')(np.linspace(0.3, 0.9, len(bars)))
        for bar, color in zip(bars, colors):
            bar.set_color(color)
        
        # 2. Final vs Max Entity Count
        ax = axes[0, 1]
        scatter = ax.scatter(seed_stats['AvgMaxCount'], seed_stats['AvgFinalCount'],
                           s=seed_stats['AvgSteps']/5, alpha=0.5, c=seed_stats['SuccessScore'],
                           cmap='viridis', edgecolors='black', linewidth=0.5)
        ax.set_xlabel('Average Max Entity Count', fontsize=11)
        ax.set_ylabel('Average Final Entity Count', fontsize=11)
        ax.set_title('Population Dynamics (size = avg steps)', fontweight='bold')
        ax.grid(True, alpha=0.3)
        plt.colorbar(scatter, ax=ax, label='Success Score')
        
        # Add diagonal line (100% survival rate)
        max_val = max(seed_stats['AvgMaxCount'].max(), seed_stats['AvgFinalCount'].max())
        ax.plot([0, max_val], [0, max_val], 'r--', alpha=0.5, label='100% Survival')
        ax.legend()
        
        # 3. Steps Run Distribution
        ax = axes[1, 0]
        ax.hist(self.batch_data['StepsRun'], bins=30, edgecolor='black', alpha=0.7)
        ax.axvline(self.batch_data['StepsRun'].mean(), color='r', 
                   linestyle='--', linewidth=2, label=f'Mean: {self.batch_data["StepsRun"].mean():.1f}')
        ax.set_xlabel('Steps Run', fontsize=11)
        ax.set_ylabel('Frequency', fontsize=11)
        ax.set_title('Distribution of Simulation Durations', fontweight='bold')
        ax.grid(True, alpha=0.3)
        ax.legend()
        
        # 4. Extinction Analysis
        ax = axes[1, 1]
        extinction_rate = (self.batch_data['FinalEntityCount'] == 0).sum() / len(self.batch_data) * 100
        survival_rate = 100 - extinction_rate
        
        ax.pie([extinction_rate, survival_rate], 
               labels=['Extinct', 'Survived'],
               autopct='%1.1f%%',
               colors=['#ff6b6b', '#51cf66'],
               startangle=90,
               explode=(0.1, 0))
        ax.set_title(f'Extinction vs Survival Rate\n({len(self.batch_data)} simulations)', 
                    fontweight='bold')
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_trait_success_patterns(self, output_file: str = "trait_success_patterns.png"):
        """Analyze which trait combinations lead to success"""
        if self.batch_data is None:
            return
        
        print("\n🧬 Analyzing successful trait patterns...")
        
        # Classify simulations by outcome
        self.batch_data['Outcome'] = pd.cut(self.batch_data['FinalEntityCount'],
                                           bins=[-1, 0, 1, 5, 100],
                                           labels=['Extinct', 'Critical', 'Surviving', 'Thriving'])
        
        trait_cols = ['AvgEndurance', 'AvgAdaptation', 'AvgMobility', 'AvgEfficiency']
        
        # Check if columns exist (handle both old and new format)
        if not all(col in self.batch_data.columns for col in trait_cols):
            print("⚠ Warning: Expected trait columns not found. Skipping trait analysis.")
            return
        
        # Filter out rows with all zeros (extinct populations have no trait data)
        valid_data = self.batch_data[self.batch_data[trait_cols].sum(axis=1) > 0].copy()
        
        if len(valid_data) == 0:
            print("⚠ No valid trait data found (all simulations may have resulted in extinction)")
            return
        
        fig, axes = plt.subplots(2, 3, figsize=(18, 12))
        fig.suptitle('Trait Patterns by Simulation Outcome\nSuccessful vs Failed Populations', 
                     fontsize=16, fontweight='bold')
        
        for idx, trait in enumerate(trait_cols):
            ax = axes[idx // 3, idx % 3]
            
            # Violin plot by outcome
            valid_trait_data = valid_data[valid_data[trait] > 0]
            if len(valid_trait_data) > 0:
                sns.violinplot(data=valid_trait_data, x='Outcome', y=trait, ax=ax)
                ax.set_title(trait.replace('Avg', ''), fontweight='bold')
                ax.set_xlabel('Simulation Outcome', fontsize=10)
                ax.set_ylabel('Trait Value', fontsize=10)
                ax.grid(True, alpha=0.3, axis='y')
            else:
                ax.text(0.5, 0.5, 'No data available', 
                       ha='center', va='center', transform=ax.transAxes)
                ax.set_title(trait.replace('Avg', ''), fontweight='bold')
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def plot_biome_impact_on_success(self, output_file: str = "biome_impact.png"):
        """Analyze how initial biome distributions affect outcomes"""
        if self.batch_data is None:
            return
        
        print("\n🗺️  Analyzing biome impact on success...")
        
        biome_cols = [col for col in self.batch_data.columns if 'BiomeCount_' in col]
        
        if not biome_cols:
            print("⚠ No biome data found in batch results")
            return
        
        # Calculate biome proportions
        for col in biome_cols:
            total = self.batch_data[biome_cols].sum(axis=1)
            prop_col = col.replace('BiomeCount_', 'BiomeProp_')
            self.batch_data[prop_col] = self.batch_data[col] / total
        
        # Classify by success
        self.batch_data['Success'] = self.batch_data['FinalEntityCount'] > 0
        
        prop_cols = [col for col in self.batch_data.columns if 'BiomeProp_' in col]
        
        fig, axes = plt.subplots(2, 4, figsize=(20, 10))
        fig.suptitle('Biome Distribution Impact on Simulation Success\nEnvironmental Factors', 
                     fontsize=16, fontweight='bold')
        
        for idx, col in enumerate(prop_cols[:8]):  # Max 8 biomes
            row = idx // 4
            col_idx = idx % 4
            ax = axes[row, col_idx]
            
            biome_name = col.replace('BiomeProp_', '')
            
            # Box plot comparing successful vs failed
            data_to_plot = [
                self.batch_data[self.batch_data['Success'] == False][col].dropna(),
                self.batch_data[self.batch_data['Success'] == True][col].dropna()
            ]
            
            bp = ax.boxplot(data_to_plot, labels=['Failed', 'Succeeded'],
                           patch_artist=True)
            
            # Color boxes
            bp['boxes'][0].set_facecolor('#ff6b6b')
            bp['boxes'][1].set_facecolor('#51cf66')
            
            ax.set_title(biome_name, fontweight='bold')
            ax.set_ylabel('Proportion', fontsize=10)
            ax.grid(True, alpha=0.3, axis='y')
        
        # Hide extra subplots if fewer than 8 biomes
        for idx in range(len(prop_cols), 8):
            axes[idx // 4, idx % 4].axis('off')
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
    
    def generate_best_seeds_report(self, output_file: str = "best_seeds_report.txt", top_n: int = 10):
        """Generate report of best performing seeds"""
        if self.batch_data is None:
            return
        
        print("\n📊 Generating best seeds report...")
        
        # Calculate success metrics
        seed_analysis = self.batch_data.groupby('Seed').agg({
            'FinalEntityCount': ['mean', 'max', 'min'],
            'MaxEntityCount': ['mean', 'max'],
            'StepsRun': ['mean', 'max'],
            'Seed': 'count'
        }).reset_index()
        
        seed_analysis.columns = ['Seed', 'AvgFinal', 'MaxFinal', 'MinFinal',
                                'AvgMax', 'PeakMax', 'AvgSteps', 'MaxSteps', 'RunCount']
        
        # Success score
        seed_analysis['SuccessScore'] = (
            seed_analysis['AvgFinal'] * 3 +
            seed_analysis['AvgMax'] +
            seed_analysis['AvgSteps'] / 50
        )
        
        seed_analysis = seed_analysis.sort_values('SuccessScore', ascending=False)
        
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("="*80 + "\n")
            f.write(" BEST PERFORMING SEEDS REPORT\n")
            f.write(" Multi-Run Batch Analysis\n")
            f.write("="*80 + "\n\n")
            
            f.write(f"Total simulations analyzed: {len(self.batch_data)}\n")
            f.write(f"Unique seeds tested: {self.batch_data['Seed'].nunique()}\n")
            f.write(f"Average steps per run: {self.batch_data['StepsRun'].mean():.1f}\n\n")
            
            f.write("="*80 + "\n")
            f.write(f" TOP {top_n} PERFORMING SEEDS\n")
            f.write("="*80 + "\n\n")
            
            for rank, (i, row) in enumerate(seed_analysis.head(top_n).iterrows(), start=1):
                f.write(f"Rank #{rank}: Seed {int(row['Seed'])}\n")
                f.write("-" * 40 + "\n")
                f.write(f"Success Score:        {row['SuccessScore']:.2f}\n")
                f.write(f"Runs with this seed:  {int(row['RunCount'])}\n")
                f.write(f"Avg Final Count:      {row['AvgFinal']:.2f}\n")
                f.write(f"Max Final Count:      {int(row['MaxFinal'])}\n")
                f.write(f"Avg Max Count:        {row['AvgMax']:.2f}\n")
                f.write(f"Peak Max Count:       {int(row['PeakMax'])}\n")
                f.write(f"Avg Steps Survived:   {row['AvgSteps']:.1f}\n")
                f.write(f"Max Steps Survived:   {int(row['MaxSteps'])}\n")
                f.write("\n")
            
            f.write("="*80 + "\n")
            f.write(" GENERAL STATISTICS\n")
            f.write("="*80 + "\n\n")
            
            extinction_count = (self.batch_data['FinalEntityCount'] == 0).sum()
            survival_count = len(self.batch_data) - extinction_count
            
            f.write(f"Extinction rate:      {extinction_count}/{len(self.batch_data)} ")
            f.write(f"({extinction_count/len(self.batch_data)*100:.1f}%)\n")
            f.write(f"Survival rate:        {survival_count}/{len(self.batch_data)} ")
            f.write(f"({survival_count/len(self.batch_data)*100:.1f}%)\n\n")
            
            f.write(f"Average final population:     {self.batch_data['FinalEntityCount'].mean():.2f}\n")
            f.write(f"Median final population:      {self.batch_data['FinalEntityCount'].median():.2f}\n")
            f.write(f"Max final population:         {self.batch_data['FinalEntityCount'].max():.0f}\n\n")
            
            f.write(f"Average peak population:      {self.batch_data['MaxEntityCount'].mean():.2f}\n")
            f.write(f"Median peak population:       {self.batch_data['MaxEntityCount'].median():.2f}\n")
            f.write(f"Max peak population:          {self.batch_data['MaxEntityCount'].max():.0f}\n\n")
            
            f.write("="*80 + "\n")
        
        print(f"✓ Saved: {output_file}")
    
    def run_complete_analysis(self, output_dir: str = "output/batch_analysis"):
        """Run all batch comparison analyses"""
        print("\n" + "="*80)
        print(" BATCH SIMULATION ANALYSIS")
        print("="*80)
        
        output_path = Path(output_dir)
        output_path.mkdir(parents=True, exist_ok=True)
        print(f"\n📁 Output directory: {output_path.absolute()}")
        
        self.plot_seed_performance_comparison(str(output_path / "01_seed_performance.png"))
        self.plot_trait_success_patterns(str(output_path / "02_trait_success_patterns.png"))
        self.plot_biome_impact_on_success(str(output_path / "03_biome_impact.png"))
        self.generate_best_seeds_report(str(output_path / "00_best_seeds_report.txt"))
        
        print("\n" + "="*80)
        print("✅ Batch analysis complete!")
        print("="*80 + "\n")


def main():
    import sys
    
    data_dir = sys.argv[1] if len(sys.argv) > 1 else "../data"
    output_dir = sys.argv[2] if len(sys.argv) > 2 else "output/batch_analysis"
    
    comparator = BatchComparator(data_dir)
    
    if not comparator.load_batch_data():
        print("\n⚠️  ERROR: No batch results file found!")
        print("\nExpected files: *batch_results*.csv")
        print("\nUsage: python batch_comparison.py [data_dir] [output_dir]")
        sys.exit(1)
    
    comparator.run_complete_analysis(output_dir)


if __name__ == "__main__":
    main()
