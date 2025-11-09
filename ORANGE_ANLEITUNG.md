# Auswertung der Evolution-Daten mit Orange Data Mining

## Installation von Orange

1. **Orange installieren:**

   ```powershell
   pip install orange3
   ```

2. **Orange starten:**

   ```powershell
   python -m Orange.canvas
   ```

   Oder als Standalone-Anwendung von [https://orangedatamining.com/download/](https://orangedatamining.com/download/) herunterladen.

---

## Workflow für Korrelationsanalyse

### Schritt 1: Daten vorbereiten

Die CSV-Dateien sind bereits im richtigen Format. Sie können wählen:

**Option A: Detail-Daten (Einzelne Organismen)**

- Dateien: `data/same/ca_detail_*.csv`, `data/seed/ca_detail_*.csv`, etc.
- Enthält: Zeitschritt, X, Y, Umgebung, Anpassung, Effizienz, Mobilitaet, Energie, Alter, Lebendig

**Option B: Aggregierte Daten**

- Datei: `ergebnisse/statistische_zusammenfassung.csv`
- Enthält: Mittelwerte und Statistiken pro Kategorie und Biom

Ich empfehle, eine **kombinierte CSV-Datei** zu erstellen für einfachere Analyse.

---

## Erstellen einer kombinierten Datei für Orange

Ich erstelle Ihnen ein Skript, das die Daten für Orange optimiert:

```python
# siehe: erstelle_orange_daten.py (wird gleich erstellt)
```

---

## Orange Workflow: Korrelationsanalyse mit Alter

### 1. **Canvas öffnen und Widgets hinzufügen**

Erstellen Sie folgenden Workflow (von links nach rechts):

```
[File] → [Select Columns] → [Correlations] → [Data Table]
   ↓                              ↓
[Data Table]              [Scatter Plot]
                                  ↓
                          [Save Data]
```

### 2. **Schritt-für-Schritt Anleitung**

#### **Widget 1: File (Daten laden)**

1. Widget "File" aus der Toolbox ziehen
2. Klicken Sie auf das Widget
3. "Browse" → Wählen Sie `ergebnisse/orange_daten_komplett.csv` (wird erstellt)
4. Orange erkennt automatisch die Spaltentypen

#### **Widget 2: Select Columns (Spalten auswählen)**

1. Widget "Select Columns" hinzufügen
2. Verbinden Sie "File" → "Select Columns"
3. Im Select Columns Widget:
   - **Features** (Merkmale für Analyse):
     - Anpassung
     - Effizienz
     - Mobilitaet
     - Energie
     - Alter
   - **Meta Attributes** (zusätzliche Infos):
     - Umgebung
     - Kategorie
     - Zeitschritt
     - X, Y

#### **Widget 3: Correlations (Korrelationen berechnen)**

1. Widget "Correlations" hinzufügen
2. Verbinden Sie "Select Columns" → "Correlations"
3. Im Correlations Widget:
   - **Correlation measure**: Pearson (für lineare Korrelationen)
   - Alternative: Spearman (für monotone Zusammenhänge)
4. Die **Korrelationsmatrix** wird angezeigt
5. **Wichtig**: Schauen Sie auf die Zeile/Spalte "Alter"!

**Interpretation der Werte:**

- **+1.0**: Perfekte positive Korrelation (beide steigen zusammen)
- **+0.7 bis +1.0**: Starke positive Korrelation
- **+0.3 bis +0.7**: Moderate positive Korrelation
- **0.0**: Keine Korrelation
- **-0.3 bis -0.7**: Moderate negative Korrelation
- **-0.7 bis -1.0**: Starke negative Korrelation
- **-1.0**: Perfekte negative Korrelation (einer steigt, anderer fällt)

#### **Widget 4: Scatter Plot (Visualisierung)**

1. Widget "Scatter Plot" hinzufügen
2. Verbinden Sie "Correlations" → "Scatter Plot"
3. Im Scatter Plot:

   - **X-Achse**: Energie (oder anderes Merkmal)
   - **Y-Achse**: Alter
   - **Color**: Umgebung (um Biome zu unterscheiden)
   - **Shape**: Kategorie (um Konfigurationen zu unterscheiden)

4. **Trendlinie hinzufügen**:
   - Klicken Sie auf das Zahnrad-Symbol (Settings)
   - Aktivieren Sie "Show regression line"

#### **Widget 5: Data Table (Daten inspizieren)**

1. Widget "Data Table" hinzufügen
2. Verbinden Sie "Correlations" → "Data Table"
3. Hier sehen Sie die Rohdaten und können einzelne Punkte untersuchen

---

## Erweiterte Analysen

### **A) Korrelationen nach Biomen getrennt**

```
[File] → [Select Rows] → [Correlations]
              ↓
         (Filter: Umgebung = "WÜSTE")
```

1. Widget "Select Rows" hinzufügen
2. Filtern Sie nach einem Biom, z.B. `Umgebung is WÜSTE`
3. Führen Sie Korrelationsanalyse nur für dieses Biom durch
4. Wiederholen Sie für andere Biome

### **B) Heatmap der Korrelationen**

```
[File] → [Correlations] → [Distance Matrix] → [Distance Map]
```

1. "Distance Matrix" Widget hinzufügen
2. Verbinden Sie "Correlations" → "Distance Matrix"
3. "Distance Map" Widget hinzufügen
4. Sie erhalten eine visuelle Heatmap

### **C) Hierarchisches Clustering**

```
[File] → [Correlations] → [Distance Matrix] → [Hierarchical Clustering]
```

Dies zeigt, welche Merkmale ähnlich korreliert sind.

---

## Spezifische Analysen für Ihre Forschungsfragen

### **1. Korrelation: Merkmale ↔ Alter**

**Hypothese**: Energie korreliert stark positiv mit Alter

**Orange Workflow:**

```
[File] → [Select Columns] → [Correlations]
                                  ↓
                            [Scatter Plot: X=Energie, Y=Alter, Color=Umgebung]
```

**Erwartete Ergebnisse** (basierend auf Python-Analyse):

- **Energie ↔ Alter**: r ≈ +0.6 bis +0.8 (stark positiv)
- **Effizienz ↔ Alter**: r ≈ -0.1 bis +0.2 (schwach)
- **Anpassung ↔ Alter**: r ≈ -0.1 bis -0.3 (schwach negativ)
- **Mobilität ↔ Alter**: r ≈ -0.05 bis +0.1 (sehr schwach)

### **2. Biom-spezifische Korrelationen**

**Frage**: Sind Korrelationen in extremen Biomen anders als in günstigen?

**Orange Workflow:**

```
[File] → [Select Rows: Umgebung="VULKAN"] → [Correlations] → [Data Table]
[File] → [Select Rows: Umgebung="WALD"] → [Correlations] → [Data Table]
```

Vergleichen Sie die Korrelationsmatrizen!

### **3. Kategorien-Vergleich**

**Frage**: Unterscheiden sich Korrelationen zwischen SAME, SEED, POPULATION, STEPS?

**Orange Workflow:**

```
[File] → [Select Rows: Kategorie="same"] → [Correlations]
[File] → [Select Rows: Kategorie="seed"] → [Correlations]
```

---

## Tipps für die Arbeit mit Orange

### **Daten filtern:**

- **Select Rows** Widget für Bedingungen wie:
  - `Umgebung is WÜSTE`
  - `Alter > 200`
  - `Energie < 100`
  - `Lebendig is true`

### **Visualisierungen speichern:**

1. Klicken Sie im Scatter Plot auf "File" → "Save Graph"
2. Wählen Sie Format: PNG, SVG, PDF

### **Workflows speichern:**

- Orange-Workflows (.ows Dateien) können gespeichert werden
- "File" → "Save" im Hauptmenü

### **Interaktive Exploration:**

- Klicken Sie Punkte im Scatter Plot an
- Sie werden in anderen verbundenen Widgets hervorgehoben
- Nützlich um Ausreißer zu identifizieren

---

## Beispiel-Workflows zum Speichern

Ich erstelle Ihnen vorgefertigte Orange-Workflows (falls Orange installiert ist):

### **Workflow 1: Basis-Korrelationsanalyse**

- Datei: `orange_workflows/korrelation_grundlagen.ows`
- Inhalt: File → Select Columns → Correlations → Scatter Plot

### **Workflow 2: Biom-Vergleich**

- Datei: `orange_workflows/biom_vergleich.ows`
- Inhalt: Parallele Analysen für alle 8 Biome

### **Workflow 3: Alter-Prädiktion**

- Datei: `orange_workflows/alter_vorhersage.ows`
- Inhalt: File → Tree → Predictions
- Nutzt Decision Tree um Alter vorherzusagen

---

## Häufige Fragen

### **Q: Welche Korrelationsmaße soll ich verwenden?**

- **Pearson**: Für lineare Zusammenhänge (Standard)
- **Spearman**: Für monotone, nicht-lineare Zusammenhänge
- **Kendall**: Für Rangkorrelationen bei kleineren Datensätzen

**Empfehlung**: Starten Sie mit **Pearson**, testen Sie dann **Spearman** zum Vergleich.

### **Q: Wie interpretiere ich p-Werte?**

Orange zeigt oft p-Werte für Korrelationen:

- **p < 0.001**: Sehr starke Evidenz (hochsignifikant)
- **p < 0.01**: Starke Evidenz (sehr signifikant)
- **p < 0.05**: Moderate Evidenz (signifikant)
- **p ≥ 0.05**: Keine signifikante Korrelation

Bei Ihren **~50.000 Datensätzen** werden fast alle Korrelationen signifikant sein!

### **Q: Wie gehe ich mit kategorialen Variablen um?**

- **Umgebung** und **Kategorie** sind kategorial
- Orange kann diese automatisch in numerische Werte konvertieren
- Besser: Filtern Sie nach Kategorien und analysieren Sie getrennt

---

## Erweiterte statistische Analysen

### **Regression Analysis:**

```
[File] → [Linear Regression] → [Predictions] → [Scatter Plot]
```

- Vorhersage von "Alter" basierend auf anderen Merkmalen
- Zeigt, welche Merkmale die besten Prädiktoren sind

### **Feature Importance:**

```
[File] → [Rank] → [Data Table]
```

- Berechnet, welche Features am wichtigsten für "Alter" sind
- Nutzt verschiedene Scoring-Methoden (Info Gain, Gain Ratio, Gini)

### **PCA (Principal Component Analysis):**

```
[File] → [PCA] → [Scatter Plot]
```

- Reduziert Dimensionen
- Zeigt Hauptkomponenten der Variation

---

## Checkliste für Ihre Analyse

- [ ] Orange installiert
- [ ] Daten mit `erstelle_orange_daten.py` vorbereitet
- [ ] CSV-Datei in Orange geladen
- [ ] Korrelationsmatrix erstellt
- [ ] Korrelation Energie ↔ Alter überprüft (sollte stark sein!)
- [ ] Scatter Plots für wichtige Beziehungen erstellt
- [ ] Biom-spezifische Analysen durchgeführt
- [ ] Ergebnisse exportiert/gespeichert

---

## Nächste Schritte

1. **Führen Sie `erstelle_orange_daten.py` aus** (wird gleich erstellt)
2. **Öffnen Sie Orange**
3. **Laden Sie `ergebnisse/orange_daten_komplett.csv`**
4. **Erstellen Sie den Basis-Workflow**
5. **Explorieren Sie die Daten interaktiv!**

Orange ist perfekt für **explorative Datenanalyse** - experimentieren Sie mit verschiedenen Visualisierungen und finden Sie interessante Muster in Ihren Simulationsdaten!

---

**Viel Erfolg bei der Analyse! 🍊📊**
