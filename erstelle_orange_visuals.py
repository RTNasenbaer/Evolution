"""
Erstellt eine visuelle Anleitung für Orange-Workflows als PNG
"""

import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, FancyArrowPatch
import matplotlib.lines as mlines

def erstelle_workflow_diagramm():
    """Erstellt ein visuelles Diagramm der Orange-Workflows"""
    
    fig, axes = plt.subplots(3, 1, figsize=(16, 18))
    fig.suptitle('Orange Data Mining - Workflows für Evolution-Datenanalyse', 
                 fontsize=18, fontweight='bold', y=0.995)
    
    # Farben
    color_data = '#FFE6CC'      # Orange/Beige für Daten
    color_process = '#CCE5FF'   # Blau für Verarbeitung
    color_viz = '#CCFFCC'       # Grün für Visualisierung
    color_output = '#FFCCFF'    # Rosa für Output
    
    # === WORKFLOW 1: BASIS-KORRELATIONSANALYSE ===
    ax1 = axes[0]
    ax1.set_xlim(0, 10)
    ax1.set_ylim(0, 6)
    ax1.axis('off')
    ax1.set_title('Workflow 1: Basis-Korrelationsanalyse', 
                  fontsize=14, fontweight='bold', loc='left', pad=20)
    
    # Widgets
    widgets_1 = [
        (1, 3, 'File\n📁', color_data, 
         'Lade:\norange_daten_\nkomplett.csv'),
        (3, 3, 'Select\nColumns\n🎯', color_process,
         'Features:\nAnpassung\nEffizienz\nMobilität\nEnergie\nAlter'),
        (5.5, 3, 'Correlations\n📊', color_process,
         'Measure:\nPearson'),
        (8, 4.5, 'Data Table\n📋', color_output,
         'Korrelations-\nmatrix'),
        (8, 1.5, 'Scatter Plot\n📈', color_viz,
         'X: Energie\nY: Alter\nColor: Umgebung')
    ]
    
    for x, y, label, color, desc in widgets_1:
        # Widget Box
        box = FancyBboxPatch((x-0.6, y-0.6), 1.2, 1.2, 
                            boxstyle="round,pad=0.1", 
                            facecolor=color, edgecolor='black', linewidth=2)
        ax1.add_patch(box)
        ax1.text(x, y, label, ha='center', va='center', 
                fontsize=10, fontweight='bold')
        # Beschreibung
        ax1.text(x, y-1.2, desc, ha='center', va='top', 
                fontsize=7, style='italic')
    
    # Arrows
    arrow_props = dict(arrowstyle='->', lw=2, color='black')
    ax1.annotate('', xy=(2.4, 3), xytext=(1.6, 3), arrowprops=arrow_props)
    ax1.annotate('', xy=(4.9, 3), xytext=(3.6, 3), arrowprops=arrow_props)
    ax1.annotate('', xy=(7.4, 4.5), xytext=(6.1, 3.3), arrowprops=arrow_props)
    ax1.annotate('', xy=(7.4, 1.5), xytext=(6.1, 2.7), arrowprops=arrow_props)
    
    # Anleitung Text
    ax1.text(0.5, 5.5, '① Datei laden  ② Spalten wählen  ③ Korrelationen berechnen  ④ Visualisieren', 
            fontsize=11, bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))
    
    # === WORKFLOW 2: BIOM-SPEZIFISCHE ANALYSE ===
    ax2 = axes[1]
    ax2.set_xlim(0, 10)
    ax2.set_ylim(0, 6)
    ax2.axis('off')
    ax2.set_title('Workflow 2: Biom-spezifische Korrelationsanalyse', 
                  fontsize=14, fontweight='bold', loc='left', pad=20)
    
    # Widgets
    widgets_2 = [
        (1, 3, 'File\n📁', color_data, 'orange_daten_\nkomplett.csv'),
        (3, 4.5, 'Select Rows\n🔍', color_process, 'Filter:\nUmgebung\n= WÜSTE'),
        (3, 1.5, 'Select Rows\n🔍', color_process, 'Filter:\nUmgebung\n= WALD'),
        (5.5, 4.5, 'Correlations\n📊', color_process, 'Wüste\nKorrelationen'),
        (5.5, 1.5, 'Correlations\n📊', color_process, 'Wald\nKorrelationen'),
        (8, 4.5, 'Data Table\n📋', color_output, 'Wüste\nErgebnisse'),
        (8, 1.5, 'Data Table\n📋', color_output, 'Wald\nErgebnisse')
    ]
    
    for x, y, label, color, desc in widgets_2:
        box = FancyBboxPatch((x-0.6, y-0.6), 1.2, 1.2, 
                            boxstyle="round,pad=0.1", 
                            facecolor=color, edgecolor='black', linewidth=2)
        ax2.add_patch(box)
        ax2.text(x, y, label, ha='center', va='center', 
                fontsize=10, fontweight='bold')
        ax2.text(x, y-1.2, desc, ha='center', va='top', 
                fontsize=7, style='italic')
    
    # Arrows
    ax2.annotate('', xy=(2.4, 4.5), xytext=(1.6, 3.3), arrowprops=arrow_props)
    ax2.annotate('', xy=(2.4, 1.5), xytext=(1.6, 2.7), arrowprops=arrow_props)
    ax2.annotate('', xy=(4.9, 4.5), xytext=(3.6, 4.5), arrowprops=arrow_props)
    ax2.annotate('', xy=(4.9, 1.5), xytext=(3.6, 1.5), arrowprops=arrow_props)
    ax2.annotate('', xy=(7.4, 4.5), xytext=(6.1, 4.5), arrowprops=arrow_props)
    ax2.annotate('', xy=(7.4, 1.5), xytext=(6.1, 1.5), arrowprops=arrow_props)
    
    # Anleitung Text
    ax2.text(0.5, 5.5, '① Datei laden  ② Nach Biom filtern  ③ Separate Korrelationen  ④ Vergleichen!', 
            fontsize=11, bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))
    
    # === WORKFLOW 3: ERWEITERTE ANALYSE ===
    ax3 = axes[2]
    ax3.set_xlim(0, 10)
    ax3.set_ylim(0, 6)
    ax3.axis('off')
    ax3.set_title('Workflow 3: Erweiterte Analyse mit Regression & Feature Importance', 
                  fontsize=14, fontweight='bold', loc='left', pad=20)
    
    # Widgets
    widgets_3 = [
        (1, 3, 'File\n📁', color_data, 'orange_daten_\nnumerisch.csv'),
        (3, 4.5, 'Rank\n⭐', color_process, 'Feature\nImportance'),
        (3, 1.5, 'Linear\nRegression\n📐', color_process, 'Target:\nAlter'),
        (5.5, 4.5, 'Data Table\n📋', color_output, 'Wichtigste\nMerkmale'),
        (5.5, 1.5, 'Predictions\n🎯', color_viz, 'Vorhergesagtes\nvs. echtes Alter'),
        (8, 3, 'Scatter Plot\n📈', color_viz, 'Residuen-\nAnalyse')
    ]
    
    for x, y, label, color, desc in widgets_3:
        box = FancyBboxPatch((x-0.6, y-0.6), 1.2, 1.2, 
                            boxstyle="round,pad=0.1", 
                            facecolor=color, edgecolor='black', linewidth=2)
        ax3.add_patch(box)
        ax3.text(x, y, label, ha='center', va='center', 
                fontsize=10, fontweight='bold')
        ax3.text(x, y-1.2, desc, ha='center', va='top', 
                fontsize=7, style='italic')
    
    # Arrows
    ax3.annotate('', xy=(2.4, 4.5), xytext=(1.6, 3.3), arrowprops=arrow_props)
    ax3.annotate('', xy=(2.4, 1.5), xytext=(1.6, 2.7), arrowprops=arrow_props)
    ax3.annotate('', xy=(4.9, 4.5), xytext=(3.6, 4.5), arrowprops=arrow_props)
    ax3.annotate('', xy=(4.9, 1.5), xytext=(3.6, 1.5), arrowprops=arrow_props)
    ax3.annotate('', xy=(7.4, 3), xytext=(6.1, 1.8), arrowprops=arrow_props)
    
    # Anleitung Text
    ax3.text(0.5, 5.5, '① Datei laden  ② Feature Ranking & Regression  ③ Vorhersagekraft prüfen', 
            fontsize=11, bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))
    
    # Legende
    legend_elements = [
        mpatches.Patch(facecolor=color_data, edgecolor='black', label='Daten-Quelle'),
        mpatches.Patch(facecolor=color_process, edgecolor='black', label='Verarbeitung'),
        mpatches.Patch(facecolor=color_viz, edgecolor='black', label='Visualisierung'),
        mpatches.Patch(facecolor=color_output, edgecolor='black', label='Ausgabe')
    ]
    ax3.legend(handles=legend_elements, loc='lower right', 
              fontsize=10, framealpha=0.9)
    
    plt.tight_layout()
    plt.savefig('ergebnisse/orange_workflows_visuell.png', dpi=300, bbox_inches='tight')
    print("✓ Workflow-Diagramm gespeichert: ergebnisse/orange_workflows_visuell.png")
    plt.close()

def erstelle_korrelations_guide():
    """Erstellt einen visuellen Guide zur Interpretation von Korrelationen"""
    
    fig, ax = plt.subplots(figsize=(14, 10))
    fig.suptitle('Korrelationskoeffizienten: Interpretation und Beispiele', 
                 fontsize=16, fontweight='bold')
    
    ax.set_xlim(0, 10)
    ax.set_ylim(0, 12)
    ax.axis('off')
    
    # Korrelationsskala
    y_start = 10.5
    
    korrelationen = [
        ('+1.0 bis +0.7', 'Starke positive Korrelation', '#006400', 
         'Beide Variablen steigen stark zusammen\nBeispiel: Energie ↔ Alter in günstigen Biomen'),
        ('+0.7 bis +0.3', 'Moderate positive Korrelation', '#32CD32',
         'Beide Variablen steigen zusammen\nBeispiel: Effizienz ↔ Energie'),
        ('+0.3 bis 0.0', 'Schwache positive Korrelation', '#90EE90',
         'Leichter Zusammenhang\nBeispiel: Anpassung ↔ Effizienz'),
        ('0.0', 'Keine Korrelation', '#CCCCCC',
         'Kein linearer Zusammenhang\nVariablen unabhängig'),
        ('-0.3 bis 0.0', 'Schwache negative Korrelation', '#FFB6C1',
         'Leichter inverser Zusammenhang\nBeispiel: Anpassung ↔ Alter'),
        ('-0.7 bis -0.3', 'Moderate negative Korrelation', '#FF69B4',
         'Eine steigt, andere fällt\nBeispiel: Mobilität ↔ Alter'),
        ('-1.0 bis -0.7', 'Starke negative Korrelation', '#8B0000',
         'Beide Variablen gegenläufig\nSeltener in biologischen Daten')
    ]
    
    y = y_start
    for koeff, beschr, farbe, beispiel in korrelationen:
        # Farbbox
        box = FancyBboxPatch((0.5, y-0.6), 1.5, 0.5, 
                            boxstyle="round,pad=0.05", 
                            facecolor=farbe, edgecolor='black', linewidth=2)
        ax.add_patch(box)
        
        # Text
        ax.text(1.25, y-0.35, koeff, ha='center', va='center', 
               fontsize=10, fontweight='bold', color='white')
        ax.text(2.5, y-0.35, beschr, ha='left', va='center', 
               fontsize=11, fontweight='bold')
        ax.text(2.5, y-0.7, beispiel, ha='left', va='top', 
               fontsize=8, style='italic')
        
        y -= 1.5
    
    # Beispiel-Scatter-Plots
    import numpy as np
    
    beispiele_y = 2.5
    
    # Positive Korrelation
    ax_pos = fig.add_axes([0.15, 0.05, 0.2, 0.15])
    x = np.random.randn(100)
    y_pos = x + np.random.randn(100) * 0.3
    ax_pos.scatter(x, y_pos, alpha=0.6, color='#006400')
    ax_pos.set_title('Starke positive\nKorrelation (+0.8)', fontsize=9, fontweight='bold')
    ax_pos.set_xlabel('Variable 1', fontsize=8)
    ax_pos.set_ylabel('Variable 2', fontsize=8)
    ax_pos.grid(True, alpha=0.3)
    
    # Keine Korrelation
    ax_null = fig.add_axes([0.4, 0.05, 0.2, 0.15])
    x = np.random.randn(100)
    y_null = np.random.randn(100)
    ax_null.scatter(x, y_null, alpha=0.6, color='#CCCCCC')
    ax_null.set_title('Keine\nKorrelation (0.0)', fontsize=9, fontweight='bold')
    ax_null.set_xlabel('Variable 1', fontsize=8)
    ax_null.set_ylabel('Variable 2', fontsize=8)
    ax_null.grid(True, alpha=0.3)
    
    # Negative Korrelation
    ax_neg = fig.add_axes([0.65, 0.05, 0.2, 0.15])
    x = np.random.randn(100)
    y_neg = -x + np.random.randn(100) * 0.3
    ax_neg.scatter(x, y_neg, alpha=0.6, color='#8B0000')
    ax_neg.set_title('Starke negative\nKorrelation (-0.8)', fontsize=9, fontweight='bold')
    ax_neg.set_xlabel('Variable 1', fontsize=8)
    ax_neg.set_ylabel('Variable 2', fontsize=8)
    ax_neg.grid(True, alpha=0.3)
    
    plt.savefig('ergebnisse/korrelations_interpretations_guide.png', dpi=300, bbox_inches='tight')
    print("✓ Korrelations-Guide gespeichert: ergebnisse/korrelations_interpretations_guide.png")
    plt.close()

if __name__ == "__main__":
    print("\nErstelle visuelle Anleitungen...\n")
    erstelle_workflow_diagramm()
    erstelle_korrelations_guide()
    print("\n✓ Alle visuellen Guides erstellt!\n")
