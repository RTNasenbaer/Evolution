#!/usr/bin/env python3
"""
Advanced Statistical Analysis for Evolution Simulation
Performs deep statistical analysis including hypothesis testing and predictive modeling
"""

import os
# Suppress Qt Wayland warnings on Linux
os.environ['QT_QPA_PLATFORM'] = 'xcb'

import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path
from scipy import stats
from sklearn.ensemble import RandomForestRegressor
from sklearn.preprocessing import StandardScaler
import warnings
warnings.filterwarnings('ignore')

plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")


class StatisticalAnalyzer:
    """Advanced statistical analysis for evolution data"""
    
    def __init__(self, data_dir: str = "../data"):
        self.data_dir = Path(data_dir)
        self.entity_data = None
        self.batch_data = None
    
    def load_data(self, entity_file: str = None, batch_file: str = None):
        """Load data files"""
        print("Loading data files...")
        
        # Auto-detect files
        if entity_file is None:
            entity_files = list(self.data_dir.glob("entity_details_*.csv"))
            if entity_files:
                entity_file = str(max(entity_files, key=lambda p: p.stat().st_mtime))
        
        if batch_file is None:
            batch_files = list(self.data_dir.glob("*batch_results*.csv"))
            if batch_files:
                batch_file = str(max(batch_files, key=lambda p: p.stat().st_mtime))
        
        if entity_file and Path(entity_file).exists():
            self.entity_data = pd.read_csv(entity_file)
            print(f"✓ Loaded entity data: {len(self.entity_data)} records")
        
        if batch_file and Path(batch_file).exists():
            self.batch_data = pd.read_csv(batch_file)
            print(f"✓ Loaded batch data: {len(self.batch_data)} simulations")
    
    def trait_significance_testing(self, output_file: str = "statistical_tests.txt"):
        """Perform hypothesis testing on trait-survival relationships"""
        if self.entity_data is None:
            return
        
        print("\n📊 Running statistical significance tests...")
        
        # Prepare survival metrics
        entity_stats = self.entity_data.groupby('EntityID').agg({
            'Age': 'max',
            'Step': 'count',
            'Speed': 'mean',
            'Mass': 'mean',
            'EnergyEfficiency': 'mean',
            'SightRange': 'mean',
            'MetabolismRate': 'mean',
            'ReproductionThreshold': 'mean'
        }).reset_index()
        
        entity_stats.rename(columns={'Step': 'StepsSurvived'}, inplace=True)
        
        traits = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 
                 'MetabolismRate', 'ReproductionThreshold']
        
        results = []
        
        for trait in traits:
            # Pearson correlation
            pearson_r, pearson_p = stats.pearsonr(entity_stats[trait], 
                                                  entity_stats['StepsSurvived'])
            
            # Spearman correlation (non-parametric)
            spearman_r, spearman_p = stats.spearmanr(entity_stats[trait], 
                                                     entity_stats['StepsSurvived'])
            
            # Split into high/low groups
            median_val = entity_stats[trait].median()
            high_group = entity_stats[entity_stats[trait] >= median_val]['StepsSurvived']
            low_group = entity_stats[entity_stats[trait] < median_val]['StepsSurvived']
            
            # T-test
            t_stat, t_p = stats.ttest_ind(high_group, low_group)
            
            # Mann-Whitney U test (non-parametric)
            u_stat, u_p = stats.mannwhitneyu(high_group, low_group, alternative='two-sided')
            
            results.append({
                'Trait': trait,
                'Pearson_r': pearson_r,
                'Pearson_p': pearson_p,
                'Spearman_r': spearman_r,
                'Spearman_p': spearman_p,
                'T_statistic': t_stat,
                'T_p_value': t_p,
                'U_statistic': u_stat,
                'U_p_value': u_p,
                'High_mean': high_group.mean(),
                'Low_mean': low_group.mean(),
                'Effect_size': (high_group.mean() - low_group.mean()) / entity_stats['StepsSurvived'].std()
            })
        
        results_df = pd.DataFrame(results)
        
        # Write report
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("="*80 + "\n")
            f.write(" STATISTICAL SIGNIFICANCE TESTING\n")
            f.write(" Trait-Survival Relationship Analysis\n")
            f.write("="*80 + "\n\n")
            
            f.write("METHODOLOGY\n")
            f.write("-" * 40 + "\n")
            f.write("Tests performed for each trait vs survival (steps survived):\n")
            f.write("  1. Pearson correlation (parametric)\n")
            f.write("  2. Spearman correlation (non-parametric)\n")
            f.write("  3. Independent t-test (high vs low trait values)\n")
            f.write("  4. Mann-Whitney U test (non-parametric alternative)\n\n")
            f.write("Significance level: α = 0.05\n")
            f.write("Effect size: Cohen's d\n\n")
            
            f.write("="*80 + "\n")
            f.write(" RESULTS\n")
            f.write("="*80 + "\n\n")
            
            for _, row in results_df.iterrows():
                f.write(f"{row['Trait']}\n")
                f.write("-" * 40 + "\n")
                
                f.write(f"Pearson Correlation:\n")
                f.write(f"  r = {row['Pearson_r']:.4f}, p = {row['Pearson_p']:.4e} ")
                f.write(f"{'***' if row['Pearson_p'] < 0.001 else '**' if row['Pearson_p'] < 0.01 else '*' if row['Pearson_p'] < 0.05 else 'ns'}\n")
                
                f.write(f"Spearman Correlation:\n")
                f.write(f"  ρ = {row['Spearman_r']:.4f}, p = {row['Spearman_p']:.4e} ")
                f.write(f"{'***' if row['Spearman_p'] < 0.001 else '**' if row['Spearman_p'] < 0.01 else '*' if row['Spearman_p'] < 0.05 else 'ns'}\n")
                
                f.write(f"\nGroup Comparison (High vs Low {row['Trait']}):\n")
                f.write(f"  High group mean: {row['High_mean']:.2f} steps\n")
                f.write(f"  Low group mean:  {row['Low_mean']:.2f} steps\n")
                f.write(f"  Difference:      {row['High_mean'] - row['Low_mean']:.2f} steps\n")
                f.write(f"  Effect size (d): {row['Effect_size']:.4f} ")
                
                abs_effect = abs(row['Effect_size'])
                if abs_effect < 0.2:
                    f.write("(negligible)\n")
                elif abs_effect < 0.5:
                    f.write("(small)\n")
                elif abs_effect < 0.8:
                    f.write("(medium)\n")
                else:
                    f.write("(large)\n")
                
                f.write(f"\n  T-test: t = {row['T_statistic']:.4f}, p = {row['T_p_value']:.4e} ")
                f.write(f"{'***' if row['T_p_value'] < 0.001 else '**' if row['T_p_value'] < 0.01 else '*' if row['T_p_value'] < 0.05 else 'ns'}\n")
                
                f.write(f"  Mann-Whitney U: U = {row['U_statistic']:.0f}, p = {row['U_p_value']:.4e} ")
                f.write(f"{'***' if row['U_p_value'] < 0.001 else '**' if row['U_p_value'] < 0.01 else '*' if row['U_p_value'] < 0.05 else 'ns'}\n")
                
                f.write("\n")
            
            f.write("="*80 + "\n")
            f.write(" LEGEND\n")
            f.write("="*80 + "\n")
            f.write("*** p < 0.001 (highly significant)\n")
            f.write("**  p < 0.01  (very significant)\n")
            f.write("*   p < 0.05  (significant)\n")
            f.write("ns           (not significant)\n\n")
            
            f.write("Effect size interpretation (Cohen's d):\n")
            f.write("  < 0.2  : Negligible\n")
            f.write("  0.2-0.5: Small\n")
            f.write("  0.5-0.8: Medium\n")
            f.write("  > 0.8  : Large\n")
        
        print(f"✓ Saved: {output_file}")
        
        return results_df
    
    def feature_importance_analysis(self, output_file: str = "feature_importance.png"):
        """Use Random Forest to determine feature importance for survival"""
        if self.entity_data is None:
            return
        
        print("\n🌲 Analyzing feature importance with Random Forest...")
        
        # Prepare data
        entity_stats = self.entity_data.groupby('EntityID').agg({
            'Age': 'max',
            'Step': 'count',
            'Speed': 'mean',
            'Mass': 'mean',
            'EnergyEfficiency': 'mean',
            'SightRange': 'mean',
            'MetabolismRate': 'mean',
            'ReproductionThreshold': 'mean',
            'Energy': 'mean'
        }).reset_index()
        
        entity_stats.rename(columns={'Step': 'StepsSurvived'}, inplace=True)
        
        # Features and target
        features = ['Speed', 'Mass', 'EnergyEfficiency', 'SightRange', 
                   'MetabolismRate', 'ReproductionThreshold', 'Energy']
        X = entity_stats[features]
        y = entity_stats['StepsSurvived']
        
        # Standardize features
        scaler = StandardScaler()
        X_scaled = scaler.fit_transform(X)
        
        # Train Random Forest
        rf = RandomForestRegressor(n_estimators=100, random_state=42, max_depth=10)
        rf.fit(X_scaled, y)
        
        # Get feature importance
        importance = pd.DataFrame({
            'Feature': features,
            'Importance': rf.feature_importances_
        }).sort_values('Importance', ascending=False)
        
        # Visualization
        fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(16, 6))
        fig.suptitle('Feature Importance Analysis\nRandom Forest Regression', 
                     fontsize=16, fontweight='bold')
        
        # Bar plot
        colors = plt.cm.RdYlGn(np.linspace(0.3, 0.9, len(importance)))
        bars = ax1.barh(range(len(importance)), importance['Importance'])
        ax1.set_yticks(range(len(importance)))
        ax1.set_yticklabels(importance['Feature'])
        ax1.set_xlabel('Importance Score', fontsize=11)
        ax1.set_title('Feature Importance for Survival Prediction', fontweight='bold')
        ax1.grid(True, axis='x', alpha=0.3)
        
        for bar, color in zip(bars, colors):
            bar.set_color(color)
        
        # Add values on bars
        for i, (idx, row) in enumerate(importance.iterrows()):
            ax1.text(row['Importance'], i, f"  {row['Importance']:.3f}", 
                    va='center', fontsize=10)
        
        # Cumulative importance
        importance_sorted = importance.sort_values('Importance', ascending=False)
        cumulative = importance_sorted['Importance'].cumsum() / importance_sorted['Importance'].sum()
        
        ax2.plot(range(1, len(cumulative)+1), cumulative, 'o-', linewidth=2, markersize=8)
        ax2.axhline(y=0.8, color='r', linestyle='--', label='80% threshold')
        ax2.set_xlabel('Number of Features', fontsize=11)
        ax2.set_ylabel('Cumulative Importance', fontsize=11)
        ax2.set_title('Cumulative Feature Importance', fontweight='bold')
        ax2.grid(True, alpha=0.3)
        ax2.legend()
        ax2.set_ylim([0, 1.05])
        
        # Add feature names
        for i, (idx, row) in enumerate(importance_sorted.iterrows()):
            if cumulative.iloc[i] <= 0.85:  # Label up to 80% cumulative
                ax2.annotate(row['Feature'], 
                           (i+1, cumulative.iloc[i]), 
                           textcoords="offset points", 
                           xytext=(0,10), 
                           ha='center',
                           fontsize=9)
        
        plt.tight_layout()
        plt.savefig(output_file, dpi=300, bbox_inches='tight')
        print(f"✓ Saved: {output_file}")
        plt.close()
        
        return importance
    
    def biome_statistical_comparison(self, output_file: str = "biome_statistics.txt"):
        """Statistical comparison of performance across biomes"""
        if self.entity_data is None:
            return
        
        print("\n🌍 Running biome statistical comparison...")
        
        biomes = self.entity_data['BiomeType'].unique()
        
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("="*80 + "\n")
            f.write(" BIOME STATISTICAL COMPARISON\n")
            f.write(" ANOVA and Post-hoc Testing\n")
            f.write("="*80 + "\n\n")
            
            metrics = ['Age', 'Energy', 'Speed', 'EnergyEfficiency']
            
            for metric in metrics:
                f.write(f"\n{metric} COMPARISON\n")
                f.write("-" * 40 + "\n\n")
                
                # Group data by biome
                groups = [self.entity_data[self.entity_data['BiomeType'] == biome][metric].dropna() 
                         for biome in biomes]
                
                # ANOVA
                f_stat, p_value = stats.f_oneway(*groups)
                
                f.write(f"One-way ANOVA:\n")
                f.write(f"  F-statistic = {f_stat:.4f}\n")
                f.write(f"  p-value = {p_value:.4e} ")
                f.write(f"{'***' if p_value < 0.001 else '**' if p_value < 0.01 else '*' if p_value < 0.05 else 'ns'}\n\n")
                
                if p_value < 0.05:
                    f.write("Significant difference detected. Biome means:\n\n")
                    for biome in biomes:
                        data = self.entity_data[self.entity_data['BiomeType'] == biome][metric]
                        f.write(f"  {biome:15s}: μ = {data.mean():.3f}, σ = {data.std():.3f}, n = {len(data)}\n")
                else:
                    f.write("No significant difference between biomes.\n")
                
                f.write("\n")
            
            f.write("="*80 + "\n")
        
        print(f"✓ Saved: {output_file}")
    
    def run_complete_analysis(self, output_dir: str = "output/statistical_analysis"):
        """Run all statistical analyses"""
        print("\n" + "="*80)
        print(" ADVANCED STATISTICAL ANALYSIS")
        print("="*80)
        
        output_path = Path(output_dir)
        output_path.mkdir(parents=True, exist_ok=True)
        print(f"\n📁 Output directory: {output_path.absolute()}")
        
        self.trait_significance_testing(str(output_path / "01_statistical_tests.txt"))
        self.feature_importance_analysis(str(output_path / "02_feature_importance.png"))
        self.biome_statistical_comparison(str(output_path / "03_biome_statistics.txt"))
        
        print("\n" + "="*80)
        print("✅ Statistical analysis complete!")
        print("="*80 + "\n")


def main():
    import sys
    
    data_dir = sys.argv[1] if len(sys.argv) > 1 else "../data"
    output_dir = sys.argv[2] if len(sys.argv) > 2 else "output/statistical_analysis"
    
    analyzer = StatisticalAnalyzer(data_dir)
    analyzer.load_data()
    
    if analyzer.entity_data is None and analyzer.batch_data is None:
        print("\n⚠️  ERROR: No data files found!")
        print("\nUsage: python statistical_analysis.py [data_dir] [output_dir]")
        sys.exit(1)
    
    analyzer.run_complete_analysis(output_dir)


if __name__ == "__main__":
    main()
