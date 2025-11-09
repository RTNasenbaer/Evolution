#!/usr/bin/env python3
"""
Analyse-Skript für Zellulärer Automat Daten

Analysiert die exportierten CSV-Dateien und erstellt Visualisierungen
zur Überlebensfähigkeit von Organismen unter räumlichen Bedingungen.
"""

import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import glob
import sys
import os

def finde_neueste_zeitreihe():
    """Findet die neueste Zeitreihen-CSV Datei"""
    dateien = glob.glob('data/ca_zeitreihe_*.csv')
    if not dateien:
        print("❌ Keine Zeitreihen-CSV gefunden!")
        print("Bitte führen Sie zuerst die Simulation aus und exportieren Sie die Daten.")
        sys.exit(1)
    return max(dateien, key=os.path.getctime)

def finde_neueste_details():
    """Findet die neueste Detail-CSV Datei"""
    dateien = glob.glob('data/ca_detail_*.csv')
    if not dateien:
        return None
    return max(dateien, key=os.path.getctime)

def analysiere_zeitreihe(datei):
    """Analysiert Zeitreihen-Daten"""
    print(f"\n📊 Analysiere: {datei}\n")
    
    df = pd.read_csv(datei, encoding='utf-8')
    
    # Grundlegende Statistik
    print("═" * 70)
    print("ZUSAMMENFASSUNG")
    print("═" * 70)
    print(f"Zeitschritte:           {df['Zeitschritt'].max()}")
    print(f"Start-Population:       {df['GesamtOrganismen'].iloc[0]}")
    print(f"End-Population:         {df['GesamtOrganismen'].iloc[-1]}")
    print(f"Überlebensrate:         {(df['GesamtOrganismen'].iloc[-1] / df['GesamtOrganismen'].iloc[0] * 100):.1f}%")
    print()
    
    # Merkmalsentwicklung
    print("MERKMALS-ENTWICKLUNG")
    print("─" * 70)
    finale_zeile = df.iloc[-1]
    print(f"Ø Anpassungsfähigkeit:  {finale_zeile['DurchschnAnpassung']:.3f}")
    print(f"Ø Effizienz:            {finale_zeile['DurchschnEffizienz']:.3f}")
    print(f"Ø Mobilität:            {finale_zeile['DurchschnMobilitaet']:.3f}")
    print(f"Ø Energie:              {finale_zeile['DurchschnEnergie']:.2f}")
    print(f"Ø Alter:                {finale_zeile['DurchschnAlter']:.1f} Zeitschritte")
    print()
    
    # Umgebungsverteilung
    umgebungen = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
    print("FINALE VERTEILUNG NACH UMGEBUNG")
    print("─" * 70)
    gesamt = finale_zeile['GesamtOrganismen']
    
    verteilung = []
    for umgebung in umgebungen:
        if umgebung in finale_zeile:
            anzahl = finale_zeile[umgebung]
            prozent = (anzahl / gesamt * 100) if gesamt > 0 else 0
            verteilung.append((umgebung, anzahl, prozent))
    
    # Sortiert nach Anzahl
    verteilung.sort(key=lambda x: x[1], reverse=True)
    
    for umgebung, anzahl, prozent in verteilung:
        if anzahl > 0:
            balken = "█" * int(prozent / 2)
            print(f"{umgebung:12s}: {int(anzahl):4d} ({prozent:5.1f}%) {balken}")
    
    return df, umgebungen

def erstelle_visualisierungen(df, umgebungen):
    """Erstellt Visualisierungen der Daten"""
    print("\n📈 Erstelle Visualisierungen...\n")
    
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))
    fig.suptitle('Zellulärer Automat - Überlebensanalyse', fontsize=16, fontweight='bold')
    
    # 1. Populationsdynamik
    ax1 = axes[0, 0]
    ax1.plot(df['Zeitschritt'], df['GesamtOrganismen'], linewidth=2, color='#2ecc71')
    ax1.set_xlabel('Zeitschritt')
    ax1.set_ylabel('Anzahl Organismen')
    ax1.set_title('Populationsdynamik')
    ax1.grid(True, alpha=0.3)
    
    # 2. Merkmalsentwicklung
    ax2 = axes[0, 1]
    ax2.plot(df['Zeitschritt'], df['DurchschnAnpassung'], label='Anpassungsfähigkeit', linewidth=2)
    ax2.plot(df['Zeitschritt'], df['DurchschnEffizienz'], label='Effizienz', linewidth=2)
    ax2.plot(df['Zeitschritt'], df['DurchschnMobilitaet'], label='Mobilität', linewidth=2)
    ax2.set_xlabel('Zeitschritt')
    ax2.set_ylabel('Durchschnittlicher Merkmalswert')
    ax2.set_title('Merkmals-Evolution')
    ax2.legend()
    ax2.grid(True, alpha=0.3)
    
    # 3. Umgebungsverteilung (finale)
    ax3 = axes[1, 0]
    finale_zeile = df.iloc[-1]
    werte = [finale_zeile[u] if u in finale_zeile else 0 for u in umgebungen]
    farben = ['#90EE90', '#228B22', '#FFD700', '#E0FFFF', '#808080', '#556B2F', '#0077BE', '#8B0000']
    ax3.bar(range(len(umgebungen)), werte, color=farben, edgecolor='black', linewidth=1.5)
    ax3.set_xticks(range(len(umgebungen)))
    ax3.set_xticklabels(umgebungen, rotation=45, ha='right')
    ax3.set_ylabel('Anzahl Organismen')
    ax3.set_title('Finale Verteilung nach Umgebung')
    ax3.grid(True, alpha=0.3, axis='y')
    
    # 4. Umgebungsverteilung über Zeit (gestapelt)
    ax4 = axes[1, 1]
    zeitschritte = df['Zeitschritt']
    
    # Nur Umgebungen mit Daten
    vorhandene_umgebungen = [u for u in umgebungen if u in df.columns]
    
    # Stapeldiagramm
    bottom = np.zeros(len(df))
    for i, umgebung in enumerate(vorhandene_umgebungen):
        werte = df[umgebung].values
        ax4.fill_between(zeitschritte, bottom, bottom + werte, 
                         label=umgebung, alpha=0.7, color=farben[i])
        bottom += werte
    
    ax4.set_xlabel('Zeitschritt')
    ax4.set_ylabel('Anzahl Organismen')
    ax4.set_title('Umgebungsverteilung über Zeit')
    ax4.legend(loc='upper right', fontsize=8)
    ax4.grid(True, alpha=0.3)
    
    plt.tight_layout()
    
    # Speichern
    ausgabe = 'data/ca_analyse.png'
    plt.savefig(ausgabe, dpi=300, bbox_inches='tight')
    print(f"✅ Visualisierung gespeichert: {ausgabe}")
    
    plt.show()

def analysiere_details(datei):
    """Analysiert Detail-CSV (einzelne Organismen)"""
    if not datei:
        return
    
    print(f"\n📋 Detail-Analyse: {datei}\n")
    
    df = pd.read_csv(datei, encoding='utf-8')
    
    print("MERKMALS-VERTEILUNG NACH UMGEBUNG")
    print("═" * 70)
    
    for umgebung in df['Umgebung'].unique():
        subset = df[df['Umgebung'] == umgebung]
        if len(subset) == 0:
            continue
        
        print(f"\n{umgebung} (n={len(subset)})")
        print("─" * 70)
        print(f"  Anpassungsfähigkeit: {subset['Anpassung'].mean():.3f} ± {subset['Anpassung'].std():.3f}")
        print(f"  Effizienz:           {subset['Effizienz'].mean():.3f} ± {subset['Effizienz'].std():.3f}")
        print(f"  Mobilität:           {subset['Mobilitaet'].mean():.3f} ± {subset['Mobilitaet'].std():.3f}")
        print(f"  Energie:             {subset['Energie'].mean():.2f} ± {subset['Energie'].std():.2f}")
        print(f"  Alter:               {subset['Alter'].mean():.1f} ± {subset['Alter'].std():.1f}")

def hauptprogramm():
    """Hauptprogramm"""
    print("\n" + "═" * 70)
    print("  ZELLULÄRER AUTOMAT - DATENANALYSE")
    print("═" * 70)
    
    # Zeitreihen-Analyse
    zeitreihe_datei = finde_neueste_zeitreihe()
    df, umgebungen = analysiere_zeitreihe(zeitreihe_datei)
    
    # Detail-Analyse
    detail_datei = finde_neueste_details()
    if detail_datei:
        analysiere_details(detail_datei)
    
    # Visualisierungen
    print("\n" + "─" * 70)
    antwort = input("\nVisualisierungen erstellen? (j/n): ").strip().lower()
    if antwort.startswith('j'):
        erstelle_visualisierungen(df, umgebungen)
    
    print("\n✅ Analyse abgeschlossen!\n")

if __name__ == "__main__":
    hauptprogramm()
