"""
Analyse der Evolution-Simulationsdaten
Vergleicht genetische Merkmale und Überlebensfähigkeit in verschiedenen Biomen
"""

import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from pathlib import Path
import warnings
warnings.filterwarnings('ignore')

# Konfiguration
plt.style.use('seaborn-v0_8-darkgrid')
sns.set_palette("husl")
plt.rcParams['figure.figsize'] = (16, 10)
plt.rcParams['font.size'] = 10

# Datenverzeichnisse
DATA_DIR = Path('data')
CATEGORIES = ['same', 'seed', 'population', 'steps']

def lade_detail_daten(kategorie):
    """Lädt alle Detail-CSVs einer Kategorie"""
    kategorie_pfad = DATA_DIR / kategorie
    detail_dateien = sorted(kategorie_pfad.glob('ca_detail_*.csv'))
    
    daten = []
    for datei in detail_dateien:
        df = pd.read_csv(datei)
        df['Datei'] = datei.stem
        df['Kategorie'] = kategorie
        daten.append(df)
    
    if daten:
        return pd.concat(daten, ignore_index=True)
    return pd.DataFrame()

def lade_zeitreihen_daten(kategorie):
    """Lädt alle Zeitreihen-CSVs einer Kategorie"""
    kategorie_pfad = DATA_DIR / kategorie
    zeitreihen_dateien = sorted(kategorie_pfad.glob('ca_zeitreihe_*.csv'))
    
    daten = []
    for datei in zeitreihen_dateien:
        df = pd.read_csv(datei)
        df['Datei'] = datei.stem
        df['Kategorie'] = kategorie
        daten.append(df)
    
    if daten:
        return pd.concat(daten, ignore_index=True)
    return pd.DataFrame()

def erstelle_biom_boxplots(alle_detail_daten, ausgabe_pfad='ergebnisse'):
    """Erstellt Boxplots für genetische Merkmale pro Biom"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    # Nur lebendige Organismen
    lebendige = alle_detail_daten[alle_detail_daten['Lebendig'] == True].copy()
    
    biome = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
    merkmale = ['Anpassung', 'Effizienz', 'Mobilitaet', 'Energie', 'Alter']
    
    # Für jedes Merkmal einen Boxplot pro Biom erstellen
    for merkmal in merkmale:
        fig, axes = plt.subplots(2, 2, figsize=(18, 12))
        fig.suptitle(f'{merkmal} - Vergleich nach Biomen und Konfigurationen', 
                     fontsize=16, fontweight='bold', y=0.995)
        
        for idx, kategorie in enumerate(CATEGORIES):
            ax = axes[idx // 2, idx % 2]
            
            kategorie_daten = lebendige[lebendige['Kategorie'] == kategorie]
            
            # Daten für Boxplot vorbereiten
            plot_daten = []
            labels = []
            for biom in biome:
                biom_daten = kategorie_daten[kategorie_daten['Umgebung'] == biom][merkmal]
                if len(biom_daten) > 0:
                    plot_daten.append(biom_daten)
                    labels.append(f"{biom}\n(n={len(biom_daten)})")
            
            if plot_daten:
                bp = ax.boxplot(plot_daten, labels=labels, patch_artist=True,
                               showmeans=True, meanline=True)
                
                # Farben für Boxen
                colors = plt.cm.Set3(np.linspace(0, 1, len(plot_daten)))
                for patch, color in zip(bp['boxes'], colors):
                    patch.set_facecolor(color)
                    patch.set_alpha(0.7)
                
                ax.set_title(f'Kategorie: {kategorie.upper()}', fontweight='bold', fontsize=12)
                ax.set_ylabel(merkmal, fontsize=11)
                ax.set_xlabel('Biom', fontsize=11)
                ax.grid(True, alpha=0.3)
                ax.tick_params(axis='x', rotation=45, labelsize=9)
        
        plt.tight_layout()
        plt.savefig(f'{ausgabe_pfad}/boxplot_{merkmal}_nach_biom.png', dpi=300, bbox_inches='tight')
        plt.close()
        print(f"✓ Boxplot für {merkmal} erstellt")

def erstelle_vergleichende_boxplots(alle_detail_daten, ausgabe_pfad='ergebnisse'):
    """Erstellt vergleichende Boxplots: Alle Konfigurationen für jedes Biom"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    lebendige = alle_detail_daten[alle_detail_daten['Lebendig'] == True].copy()
    biome = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
    merkmale = ['Anpassung', 'Effizienz', 'Mobilitaet', 'Energie', 'Alter']
    
    for biom in biome:
        biom_daten = lebendige[lebendige['Umgebung'] == biom]
        
        if len(biom_daten) == 0:
            continue
        
        fig, axes = plt.subplots(2, 3, figsize=(18, 10))
        fig.suptitle(f'Biom: {biom} - Vergleich aller Konfigurationen', 
                     fontsize=16, fontweight='bold')
        
        for idx, merkmal in enumerate(merkmale):
            ax = axes[idx // 3, idx % 3]
            
            plot_daten = []
            labels = []
            for kategorie in CATEGORIES:
                kat_daten = biom_daten[biom_daten['Kategorie'] == kategorie][merkmal]
                if len(kat_daten) > 0:
                    plot_daten.append(kat_daten)
                    labels.append(f"{kategorie}\n(n={len(kat_daten)})")
            
            if plot_daten:
                bp = ax.boxplot(plot_daten, labels=labels, patch_artist=True,
                               showmeans=True, meanline=True)
                
                colors = ['#ff9999', '#66b3ff', '#99ff99', '#ffcc99']
                for patch, color in zip(bp['boxes'], colors[:len(plot_daten)]):
                    patch.set_facecolor(color)
                    patch.set_alpha(0.7)
                
                ax.set_title(merkmal, fontweight='bold')
                ax.set_ylabel('Wert', fontsize=10)
                ax.grid(True, alpha=0.3)
        
        # Letztes Subplot für Legende/Info
        ax = axes[1, 2]
        ax.axis('off')
        info_text = f"Biom: {biom}\n\n"
        for kategorie in CATEGORIES:
            n = len(biom_daten[biom_daten['Kategorie'] == kategorie])
            info_text += f"{kategorie}: {n} Organismen\n"
        ax.text(0.1, 0.5, info_text, fontsize=12, verticalalignment='center',
               bbox=dict(boxstyle='round', facecolor='wheat', alpha=0.5))
        
        plt.tight_layout()
        plt.savefig(f'{ausgabe_pfad}/vergleich_konfigurationen_{biom}.png', 
                   dpi=300, bbox_inches='tight')
        plt.close()
        print(f"✓ Vergleichsplot für {biom} erstellt")

def analysiere_ueberlebensraten(alle_zeitreihen, ausgabe_pfad='ergebnisse'):
    """Analysiert Populationsentwicklung und Überlebensraten"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    fig, axes = plt.subplots(2, 2, figsize=(18, 12))
    fig.suptitle('Populationsentwicklung - Vergleich der Konfigurationen', 
                 fontsize=16, fontweight='bold')
    
    for idx, kategorie in enumerate(CATEGORIES):
        ax = axes[idx // 2, idx % 2]
        
        kat_daten = alle_zeitreihen[alle_zeitreihen['Kategorie'] == kategorie]
        
        for datei in kat_daten['Datei'].unique():
            datei_daten = kat_daten[kat_daten['Datei'] == datei]
            ax.plot(datei_daten['Zeitschritt'], datei_daten['GesamtOrganismen'], 
                   alpha=0.7, linewidth=2)
        
        ax.set_title(f'Kategorie: {kategorie.upper()}', fontweight='bold', fontsize=12)
        ax.set_xlabel('Zeitschritt', fontsize=11)
        ax.set_ylabel('Anzahl Organismen', fontsize=11)
        ax.grid(True, alpha=0.3)
    
    plt.tight_layout()
    plt.savefig(f'{ausgabe_pfad}/populationsentwicklung.png', dpi=300, bbox_inches='tight')
    plt.close()
    print("✓ Populationsentwicklung visualisiert")
    
    # Biom-spezifische Entwicklung
    biome = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
    
    for kategorie in CATEGORIES:
        fig, axes = plt.subplots(2, 4, figsize=(20, 10))
        fig.suptitle(f'Biom-Populationen - Kategorie: {kategorie.upper()}', 
                     fontsize=16, fontweight='bold')
        
        kat_daten = alle_zeitreihen[alle_zeitreihen['Kategorie'] == kategorie]
        
        for idx, biom in enumerate(biome):
            ax = axes[idx // 4, idx % 4]
            
            if biom in kat_daten.columns:
                for datei in kat_daten['Datei'].unique():
                    datei_daten = kat_daten[kat_daten['Datei'] == datei]
                    ax.plot(datei_daten['Zeitschritt'], datei_daten[biom], 
                           alpha=0.7, linewidth=2)
                
                ax.set_title(biom, fontweight='bold')
                ax.set_xlabel('Zeitschritt', fontsize=9)
                ax.set_ylabel('Organismen', fontsize=9)
                ax.grid(True, alpha=0.3)
        
        plt.tight_layout()
        plt.savefig(f'{ausgabe_pfad}/biom_entwicklung_{kategorie}.png', 
                   dpi=300, bbox_inches='tight')
        plt.close()
        print(f"✓ Biom-Entwicklung für {kategorie} visualisiert")

def erstelle_statistische_zusammenfassung(alle_detail_daten, ausgabe_pfad='ergebnisse'):
    """Erstellt statistische Zusammenfassung als CSV und Textdatei"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    lebendige = alle_detail_daten[alle_detail_daten['Lebendig'] == True].copy()
    
    # Gruppierung nach Kategorie und Biom
    statistik = lebendige.groupby(['Kategorie', 'Umgebung']).agg({
        'Anpassung': ['mean', 'std', 'min', 'max', 'count'],
        'Effizienz': ['mean', 'std', 'min', 'max'],
        'Mobilitaet': ['mean', 'std', 'min', 'max'],
        'Energie': ['mean', 'std', 'min', 'max'],
        'Alter': ['mean', 'std', 'min', 'max']
    }).round(3)
    
    statistik.to_csv(f'{ausgabe_pfad}/statistische_zusammenfassung.csv')
    print("✓ Statistische Zusammenfassung als CSV gespeichert")
    
    # Textbericht
    with open(f'{ausgabe_pfad}/analyse_bericht.txt', 'w', encoding='utf-8') as f:
        f.write("=" * 80 + "\n")
        f.write("ANALYSE DER EVOLUTION-SIMULATION\n")
        f.write("=" * 80 + "\n\n")
        
        for kategorie in CATEGORIES:
            f.write(f"\n{'='*80}\n")
            f.write(f"KATEGORIE: {kategorie.upper()}\n")
            f.write(f"{'='*80}\n\n")
            
            kat_daten = lebendige[lebendige['Kategorie'] == kategorie]
            
            f.write(f"Gesamtanzahl lebendiger Organismen: {len(kat_daten)}\n\n")
            
            # Pro Biom
            biome = kat_daten['Umgebung'].unique()
            for biom in sorted(biome):
                biom_daten = kat_daten[kat_daten['Umgebung'] == biom]
                f.write(f"\n{biom}:\n")
                f.write(f"  Anzahl: {len(biom_daten)}\n")
                f.write(f"  Anpassung:  Ø {biom_daten['Anpassung'].mean():.3f} "
                       f"(±{biom_daten['Anpassung'].std():.3f})\n")
                f.write(f"  Effizienz:  Ø {biom_daten['Effizienz'].mean():.3f} "
                       f"(±{biom_daten['Effizienz'].std():.3f})\n")
                f.write(f"  Mobilität:  Ø {biom_daten['Mobilitaet'].mean():.3f} "
                       f"(±{biom_daten['Mobilitaet'].std():.3f})\n")
                f.write(f"  Energie:    Ø {biom_daten['Energie'].mean():.3f} "
                       f"(±{biom_daten['Energie'].std():.3f})\n")
                f.write(f"  Alter:      Ø {biom_daten['Alter'].mean():.3f} "
                       f"(±{biom_daten['Alter'].std():.3f})\n")
    
    print("✓ Textbericht erstellt")

def analysiere_ueberlebensfaehigkeit(alle_detail_daten, alle_zeitreihen, ausgabe_pfad='ergebnisse'):
    """Analysiert Überlebensfähigkeit in verschiedenen Biomen"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    lebendige = alle_detail_daten[alle_detail_daten['Lebendig'] == True].copy()
    biome = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
    
    # Durchschnittliches Alter pro Biom
    fig, axes = plt.subplots(2, 2, figsize=(16, 12))
    fig.suptitle('Durchschnittliches Alter (Überlebensfähigkeit) pro Biom', 
                 fontsize=16, fontweight='bold')
    
    for idx, kategorie in enumerate(CATEGORIES):
        ax = axes[idx // 2, idx % 2]
        
        kat_daten = lebendige[lebendige['Kategorie'] == kategorie]
        alter_pro_biom = []
        labels = []
        
        for biom in biome:
            biom_daten = kat_daten[kat_daten['Umgebung'] == biom]
            if len(biom_daten) > 0:
                alter_pro_biom.append(biom_daten['Alter'].values)
                labels.append(f"{biom}\n(Ø{biom_daten['Alter'].mean():.0f})")
        
        if alter_pro_biom:
            bp = ax.boxplot(alter_pro_biom, labels=labels, patch_artist=True)
            
            colors = plt.cm.Spectral(np.linspace(0, 1, len(alter_pro_biom)))
            for patch, color in zip(bp['boxes'], colors):
                patch.set_facecolor(color)
                patch.set_alpha(0.7)
            
            ax.set_title(f'Kategorie: {kategorie.upper()}', fontweight='bold', fontsize=12)
            ax.set_ylabel('Alter', fontsize=11)
            ax.set_xlabel('Biom', fontsize=11)
            ax.grid(True, alpha=0.3)
            ax.tick_params(axis='x', rotation=45, labelsize=9)
    
    plt.tight_layout()
    plt.savefig(f'{ausgabe_pfad}/ueberlebensfaehigkeit_alter.png', dpi=300, bbox_inches='tight')
    plt.close()
    print("✓ Überlebensfähigkeits-Analyse (Alter) erstellt")
    
    # Energie-Level als Indikator für Überlebensfähigkeit
    fig, axes = plt.subplots(2, 2, figsize=(16, 12))
    fig.suptitle('Durchschnittliche Energie (Vitalität) pro Biom', 
                 fontsize=16, fontweight='bold')
    
    for idx, kategorie in enumerate(CATEGORIES):
        ax = axes[idx // 2, idx % 2]
        
        kat_daten = lebendige[lebendige['Kategorie'] == kategorie]
        energie_pro_biom = []
        labels = []
        
        for biom in biome:
            biom_daten = kat_daten[kat_daten['Umgebung'] == biom]
            if len(biom_daten) > 0:
                energie_pro_biom.append(biom_daten['Energie'].values)
                labels.append(f"{biom}\n(Ø{biom_daten['Energie'].mean():.0f})")
        
        if energie_pro_biom:
            bp = ax.boxplot(energie_pro_biom, labels=labels, patch_artist=True)
            
            colors = plt.cm.RdYlGn(np.linspace(0.2, 0.9, len(energie_pro_biom)))
            for patch, color in zip(bp['boxes'], colors):
                patch.set_facecolor(color)
                patch.set_alpha(0.7)
            
            ax.set_title(f'Kategorie: {kategorie.upper()}', fontweight='bold', fontsize=12)
            ax.set_ylabel('Energie', fontsize=11)
            ax.set_xlabel('Biom', fontsize=11)
            ax.grid(True, alpha=0.3)
            ax.tick_params(axis='x', rotation=45, labelsize=9)
    
    plt.tight_layout()
    plt.savefig(f'{ausgabe_pfad}/ueberlebensfaehigkeit_energie.png', dpi=300, bbox_inches='tight')
    plt.close()
    print("✓ Überlebensfähigkeits-Analyse (Energie) erstellt")

def erstelle_heatmap_analyse(alle_detail_daten, ausgabe_pfad='ergebnisse'):
    """Erstellt Heatmaps für Merkmals-Korrelationen"""
    Path(ausgabe_pfad).mkdir(exist_ok=True)
    
    lebendige = alle_detail_daten[alle_detail_daten['Lebendig'] == True].copy()
    merkmale = ['Anpassung', 'Effizienz', 'Mobilitaet', 'Energie', 'Alter']
    
    fig, axes = plt.subplots(2, 2, figsize=(16, 14))
    fig.suptitle('Korrelationsanalyse der genetischen Merkmale', 
                 fontsize=16, fontweight='bold')
    
    for idx, kategorie in enumerate(CATEGORIES):
        ax = axes[idx // 2, idx % 2]
        
        kat_daten = lebendige[lebendige['Kategorie'] == kategorie][merkmale]
        korrelation = kat_daten.corr()
        
        sns.heatmap(korrelation, annot=True, fmt='.2f', cmap='coolwarm', 
                   center=0, square=True, ax=ax, cbar_kws={'shrink': 0.8})
        ax.set_title(f'Kategorie: {kategorie.upper()}', fontweight='bold', fontsize=12)
    
    plt.tight_layout()
    plt.savefig(f'{ausgabe_pfad}/korrelation_merkmale.png', dpi=300, bbox_inches='tight')
    plt.close()
    print("✓ Korrelations-Heatmaps erstellt")

def hauptanalyse():
    """Hauptfunktion für die komplette Analyse"""
    print("\n" + "="*80)
    print("EVOLUTION-SIMULATIONSANALYSE")
    print("="*80 + "\n")
    
    print("Lade Daten...")
    
    # Alle Daten laden
    alle_detail_daten = []
    alle_zeitreihen = []
    
    for kategorie in CATEGORIES:
        print(f"  → {kategorie}...")
        detail = lade_detail_daten(kategorie)
        zeitreihe = lade_zeitreihen_daten(kategorie)
        
        if not detail.empty:
            alle_detail_daten.append(detail)
        if not zeitreihe.empty:
            alle_zeitreihen.append(zeitreihe)
    
    alle_detail_daten = pd.concat(alle_detail_daten, ignore_index=True)
    alle_zeitreihen = pd.concat(alle_zeitreihen, ignore_index=True)
    
    print(f"\n✓ {len(alle_detail_daten)} Detail-Datensätze geladen")
    print(f"✓ {len(alle_zeitreihen)} Zeitreihen-Datensätze geladen\n")
    
    print("Erstelle Analysen...\n")
    
    # Analysen durchführen
    erstelle_biom_boxplots(alle_detail_daten)
    erstelle_vergleichende_boxplots(alle_detail_daten)
    analysiere_ueberlebensraten(alle_zeitreihen)
    analysiere_ueberlebensfaehigkeit(alle_detail_daten, alle_zeitreihen)
    erstelle_heatmap_analyse(alle_detail_daten)
    erstelle_statistische_zusammenfassung(alle_detail_daten)
    
    print("\n" + "="*80)
    print("ANALYSE ABGESCHLOSSEN!")
    print("="*80)
    print("\nAlle Ergebnisse wurden im Ordner 'ergebnisse/' gespeichert:")
    print("  • Boxplots für genetische Merkmale nach Biomen")
    print("  • Vergleichende Analysen zwischen Konfigurationen")
    print("  • Populationsentwicklungen")
    print("  • Überlebensfähigkeits-Analysen")
    print("  • Korrelations-Heatmaps")
    print("  • Statistische Zusammenfassungen (CSV + Text)")
    print("\n")

if __name__ == "__main__":
    hauptanalyse()
