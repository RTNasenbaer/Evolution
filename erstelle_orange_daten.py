"""
Erstellt optimierte CSV-Dateien für die Analyse mit Orange Data Mining
"""

import pandas as pd
from pathlib import Path

def erstelle_orange_daten():
    """Kombiniert alle Detail-Daten in eine optimierte CSV für Orange"""
    
    print("=" * 80)
    print("ERSTELLE DATEN FÜR ORANGE DATA MINING")
    print("=" * 80 + "\n")
    
    DATA_DIR = Path('data')
    OUTPUT_DIR = Path('ergebnisse')
    OUTPUT_DIR.mkdir(exist_ok=True)
    
    kategorien = ['same', 'seed', 'population', 'steps']
    
    # Alle Detail-Daten sammeln
    alle_daten = []
    
    for kategorie in kategorien:
        print(f"Lade {kategorie}...")
        kategorie_pfad = DATA_DIR / kategorie
        detail_dateien = sorted(kategorie_pfad.glob('ca_detail_*.csv'))
        
        for datei in detail_dateien:
            df = pd.read_csv(datei)
            df['Kategorie'] = kategorie
            df['Datei'] = datei.stem
            alle_daten.append(df)
    
    # Kombinieren
    df_komplett = pd.concat(alle_daten, ignore_index=True)
    
    print(f"\nGesamtdatensätze: {len(df_komplett)}")
    
    # Nur lebendige Organismen (für Korrelationsanalyse relevanter)
    df_lebend = df_komplett[df_komplett['Lebendig'] == True].copy()
    
    print(f"Lebendige Organismen: {len(df_lebend)}")
    
    # Für Orange optimieren
    # 1. Kategoriale Variablen klarstellen
    df_lebend['Umgebung'] = df_lebend['Umgebung'].astype(str)
    df_lebend['Kategorie'] = df_lebend['Kategorie'].astype(str)
    
    # 2. Lebendig als 0/1
    df_lebend['Lebendig'] = df_lebend['Lebendig'].astype(int)
    
    # 3. Spalten umbenennen für bessere Lesbarkeit
    df_orange = df_lebend.rename(columns={
        'Mobilitaet': 'Mobilität'
    })
    
    # Speichern - Komplette Daten
    ausgabe_komplett = OUTPUT_DIR / 'orange_daten_komplett.csv'
    df_orange.to_csv(ausgabe_komplett, index=False)
    print(f"\n✓ Gespeichert: {ausgabe_komplett}")
    print(f"  Zeilen: {len(df_orange)}")
    print(f"  Spalten: {list(df_orange.columns)}")
    
    # Speichern - Nur numerische Features für Korrelation
    df_numerisch = df_orange[['Anpassung', 'Effizienz', 'Mobilität', 
                               'Energie', 'Alter', 'Umgebung', 'Kategorie']].copy()
    ausgabe_numerisch = OUTPUT_DIR / 'orange_daten_numerisch.csv'
    df_numerisch.to_csv(ausgabe_numerisch, index=False)
    print(f"\n✓ Gespeichert: {ausgabe_numerisch}")
    print(f"  Zeilen: {len(df_numerisch)}")
    print(f"  Spalten: {list(df_numerisch.columns)}")
    
    # Statistik pro Biom und Kategorie (aggregiert)
    print("\n" + "=" * 80)
    print("ERSTELLE AGGREGIERTE DATEN")
    print("=" * 80 + "\n")
    
    df_agg = df_orange.groupby(['Kategorie', 'Umgebung']).agg({
        'Anpassung': ['mean', 'std', 'min', 'max', 'count'],
        'Effizienz': ['mean', 'std', 'min', 'max'],
        'Mobilität': ['mean', 'std', 'min', 'max'],
        'Energie': ['mean', 'std', 'min', 'max'],
        'Alter': ['mean', 'std', 'min', 'max']
    }).reset_index()
    
    # Spalten flach machen
    df_agg.columns = ['_'.join(col).strip('_') for col in df_agg.columns.values]
    
    ausgabe_agg = OUTPUT_DIR / 'orange_daten_aggregiert.csv'
    df_agg.to_csv(ausgabe_agg, index=False)
    print(f"✓ Gespeichert: {ausgabe_agg}")
    print(f"  Zeilen: {len(df_agg)} (pro Kategorie-Biom-Kombination)")
    
    # Sample-Datei für schnelles Testen (1000 zufällige Zeilen)
    df_sample = df_orange.sample(min(1000, len(df_orange)), random_state=42)
    ausgabe_sample = OUTPUT_DIR / 'orange_daten_sample.csv'
    df_sample.to_csv(ausgabe_sample, index=False)
    print(f"\n✓ Gespeichert: {ausgabe_sample}")
    print(f"  Zeilen: {len(df_sample)} (Sample für schnelles Testen)")
    
    # Korrelations-Vorschau
    print("\n" + "=" * 80)
    print("KORRELATIONS-VORSCHAU (Gesamtdaten)")
    print("=" * 80 + "\n")
    
    korrelation = df_orange[['Anpassung', 'Effizienz', 'Mobilität', 'Energie', 'Alter']].corr()
    print(korrelation.round(3))
    
    print("\n" + "=" * 80)
    print("KORRELATIONEN MIT ALTER:")
    print("=" * 80 + "\n")
    alter_korr = korrelation['Alter'].sort_values(ascending=False)
    for merkmal, wert in alter_korr.items():
        if merkmal != 'Alter':
            interpretation = ""
            if abs(wert) >= 0.7:
                interpretation = "⭐⭐⭐ SEHR STARK"
            elif abs(wert) >= 0.5:
                interpretation = "⭐⭐ STARK"
            elif abs(wert) >= 0.3:
                interpretation = "⭐ MODERAT"
            else:
                interpretation = "➡️ SCHWACH"
            
            richtung = "positiv ↗️" if wert > 0 else "negativ ↘️"
            print(f"{merkmal:12} ↔ Alter: {wert:+.3f}  ({richtung})  {interpretation}")
    
    print("\n" + "=" * 80)
    print("BEREIT FÜR ORANGE!")
    print("=" * 80)
    print("\nDateien im Ordner 'ergebnisse/':")
    print("  1. orange_daten_komplett.csv    - Alle Daten (für vollständige Analyse)")
    print("  2. orange_daten_numerisch.csv   - Nur Features + Kategorien")
    print("  3. orange_daten_aggregiert.csv  - Aggregierte Statistiken")
    print("  4. orange_daten_sample.csv      - Sample für schnelles Testen")
    print("\nStarten Sie Orange und laden Sie eine dieser Dateien!")
    print("Siehe ORANGE_ANLEITUNG.md für detaillierte Schritte.\n")

if __name__ == "__main__":
    erstelle_orange_daten()
