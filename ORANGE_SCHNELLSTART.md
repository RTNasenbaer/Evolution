# Orange Data Mining - Schnellstart

## 🚀 In 5 Minuten zur Korrelationsanalyse

### Schritt 1: Daten sind bereit!

Die CSV-Dateien wurden bereits erstellt im Ordner `ergebnisse/`:

- ✅ `orange_daten_komplett.csv` - 49.702 Organismen, alle Details
- ✅ `orange_daten_numerisch.csv` - Nur Features für Korrelation
- ✅ `orange_daten_sample.csv` - 1.000 Zeilen zum Testen
- ✅ `orange_daten_aggregiert.csv` - Aggregierte Statistiken

### Schritt 2: Orange installieren

**Option A - Mit pip:**

```powershell
pip install orange3
python -m Orange.canvas
```

**Option B - Standalone App:**
Download von: https://orangedatamining.com/download/

### Schritt 3: Basis-Workflow in Orange

1. **Orange öffnen**
2. **Widgets hinzufügen** (per Drag & Drop):

   - `Data` → `File`
   - `Data` → `Select Columns`
   - `Visualize` → `Correlations`
   - `Visualize` → `Scatter Plot`

3. **Verbinden** (Widgets mit Linien verbinden):

   ```
   File → Select Columns → Correlations → Scatter Plot
   ```

4. **Konfigurieren**:
   - **File**: Laden Sie `ergebnisse/orange_daten_numerisch.csv`
   - **Select Columns**:
     - Features: Anpassung, Effizienz, Mobilität, Energie, Alter
     - Meta: Umgebung, Kategorie
   - **Correlations**: Pearson auswählen
   - **Scatter Plot**:
     - X-Achse = Energie
     - Y-Achse = Alter
     - Color = Umgebung

### Schritt 4: Ergebnisse ablesen

**In Correlations Widget:**

- Schauen Sie auf die Spalte "Alter"
- **Energie ↔ Alter: +0.448** ⭐ Moderate positive Korrelation
- **Mobilität ↔ Alter: -0.193** ➡️ Schwache negative Korrelation

**Im Scatter Plot:**

- Punkte mit hoher Energie → hohes Alter
- Verschiedene Farben = verschiedene Biome
- Günstige Biome (Wald, Grasland) = oben rechts (hohe Energie, hohes Alter)
- Extreme Biome (Vulkan, Wüste) = unten links (niedrige Energie, niedriges Alter)

---

## 📊 Wichtigste Erkenntnisse aus den Daten

### Korrelationen mit Alter (bereits berechnet):

| Merkmal       | Korrelation | Interpretation                                                       |
| ------------- | ----------- | -------------------------------------------------------------------- |
| **Energie**   | **+0.448**  | ⭐ **Moderate positive Korrelation** - Wichtigster Überlebensfaktor! |
| **Mobilität** | -0.193      | ➡️ Schwache negative Korrelation - Hohe Mobilität = kürzeres Leben   |
| **Anpassung** | -0.027      | ➡️ Sehr schwache negative Korrelation - Fast kein Zusammenhang       |
| **Effizienz** | -0.010      | ➡️ Keine Korrelation - Unabhängig vom Alter                          |

### Was bedeutet das?

1. **Energie ist der Schlüssel zum Überleben**

   - Organismen mit hoher Energie leben ~4x länger
   - In günstigen Biomen (Wald, Grasland) ist hohe Energie möglich
   - In extremen Biomen (Vulkan, Wüste) ist Energie dauerhaft niedrig

2. **Hohe Mobilität verkürzt die Lebensspanne**

   - Bewegung kostet Energie
   - In kargen Biomen müssen Organismen mobil sein, sterben aber früher

3. **Anpassung und Effizienz korrelieren NICHT mit Alter**
   - Diese Merkmale sind wichtig zum Überleben in extremen Biomen
   - Aber selbst perfekt angepasste Organismen leben dort kurz

---

## 🎯 Erweiterte Analysen

### Biom-spezifische Korrelationen

**Workflow:**

```
File → Select Rows (Filter: Umgebung = "WÜSTE") → Correlations
```

Testen Sie verschiedene Biome:

- WÜSTE
- VULKAN
- WALD
- GRASLAND
- OZEAN

**Hypothese:**
Korrelationen sind in extremen Biomen anders als in günstigen!

### Kategorien-Vergleich

**Workflow:**

```
File → Select Rows (Filter: Kategorie = "same") → Correlations
```

Vergleichen Sie:

- same (gleiche Bedingungen)
- seed (zufällige Seeds)
- population (variable Population)
- steps (schrittweise)

---

## 📁 Weitere Ressourcen

- **Detaillierte Anleitung**: Siehe `ORANGE_ANLEITUNG.md`
- **Workflow-Diagramme**: Siehe `ergebnisse/orange_workflows_visuell.png`
- **Korrelations-Guide**: Siehe `ergebnisse/korrelations_interpretations_guide.png`
- **Statistische Daten**: Siehe `ergebnisse/statistische_zusammenfassung.csv`

---

## ✅ Checkliste

- [ ] Orange installiert
- [ ] `orange_daten_numerisch.csv` geladen
- [ ] Basis-Workflow erstellt (File → Select Columns → Correlations → Scatter Plot)
- [ ] Korrelation Energie ↔ Alter gefunden (+0.448)
- [ ] Scatter Plot mit Biom-Färbung erstellt
- [ ] Workflow gespeichert (.ows Datei)
- [ ] Screenshots/Grafiken exportiert

---

**Viel Erfolg! Die Daten sind bereit für Ihre explorative Analyse! 🍊**
