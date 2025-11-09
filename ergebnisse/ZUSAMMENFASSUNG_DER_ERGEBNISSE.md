# Zusammenfassung der Evolution-Simulationsanalyse

## Übersicht der durchgeführten Analysen

Die Analyse umfasst **49.702 Detail-Datensätze** und **36.030 Zeitreihen-Datensätze** aus 20 Simulationsläufen über 4 verschiedene Konfigurationen:

- **SAME** (5 Läufe): Exakt gleiche Bedingungen
- **SEED** (5 Läufe): Zufällige Seeds
- **POPULATION** (5 Läufe): Wechselnde Anfangspopulation (1, 50, 625, 1250, 2500)
- **STEPS** (5 Läufe): Schrittweise aufbauend (5×400 Schritte bis 2000)

---

## 🔍 Haupterkenntnisse

### 1. Genetische Merkmale nach Biomen

#### **Anpassungswerte (Durchschnitt über alle Konfigurationen)**

| Biom         | SAME  | SEED      | POPULATION | STEPS | Tendenz                  |
| ------------ | ----- | --------- | ---------- | ----- | ------------------------ |
| **WÜSTE**    | 1.258 | **1.371** | 1.277      | 1.039 | ⬆️ **Höchste Anpassung** |
| **VULKAN**   | 1.242 | 1.305     | 1.263      | 0.997 | ⬆️ Sehr hohe Anpassung   |
| **TUNDRA**   | 1.232 | 1.287     | 1.259      | 1.007 | ➡️ Hohe Anpassung        |
| **GRASLAND** | 1.183 | 1.261     | 1.208      | 0.978 | ➡️ Moderate Anpassung    |
| **GEBIRGE**  | 1.195 | 1.290     | 1.228      | 1.007 | ➡️ Moderate Anpassung    |
| **SUMPF**    | 1.191 | 1.255     | 1.201      | 0.988 | ➡️ Moderate Anpassung    |
| **OZEAN**    | 1.174 | 1.271     | 1.200      | 0.994 | ➡️ Moderate Anpassung    |
| **WALD**     | 1.170 | 1.266     | 1.219      | 0.987 | ⬇️ Niedrigste Anpassung  |

**Interpretation:**

- **Extreme Biome** (Wüste, Vulkan, Tundra) erfordern **höhere Anpassungswerte**
- **Günstige Biome** (Wald, Grasland, Ozean) erlauben **niedrigere Anpassungswerte**
- Dies zeigt, dass die Simulation realistische Selektionsdrücke abbildet

---

#### **Effizienzwerte (Energienutzung)**

| Biom         | SAME  | SEED      | POPULATION | STEPS | Tendenz                    |
| ------------ | ----- | --------- | ---------- | ----- | -------------------------- |
| **TUNDRA**   | 1.914 | **1.924** | **1.928**  | 1.904 | ⬆️⬆️ **Höchste Effizienz** |
| **GEBIRGE**  | 1.912 | 1.904     | 1.904      | 1.901 | ⬆️⬆️ Sehr hohe Effizienz   |
| **WÜSTE**    | 1.865 | 1.848     | 1.867      | 1.841 | ⬆️ Hohe Effizienz          |
| **VULKAN**   | 1.836 | 1.823     | 1.852      | 1.833 | ⬆️ Hohe Effizienz          |
| **OZEAN**    | 1.837 | 1.830     | 1.841      | 1.802 | ➡️ Moderate Effizienz      |
| **WALD**     | 1.827 | 1.801     | 1.825      | 1.748 | ➡️ Moderate Effizienz      |
| **GRASLAND** | 1.807 | 1.809     | 1.827      | 1.787 | ➡️ Moderate Effizienz      |
| **SUMPF**    | 1.824 | 1.823     | 1.840      | 1.806 | ➡️ Moderate Effizienz      |

**Interpretation:**

- **Ressourcenarme Biome** (Tundra, Gebirge, Wüste) erzwingen **maximale Effizienz**
- **Ressourcenreiche Biome** (Wald, Grasland) erlauben **geringere Effizienz**

---

#### **Mobilitätswerte**

| Biom         | SAME  | SEED  | POPULATION | STEPS | Tendenz                  |
| ------------ | ----- | ----- | ---------- | ----- | ------------------------ |
| **TUNDRA**   | 1.443 | 1.440 | **1.506**  | 1.328 | ⬆️ **Höchste Mobilität** |
| **WÜSTE**    | 1.423 | 1.407 | 1.450      | 1.312 | ⬆️ Sehr hohe Mobilität   |
| **VULKAN**   | 1.402 | 1.381 | 1.456      | 1.294 | ⬆️ Hohe Mobilität        |
| **GEBIRGE**  | 1.316 | 1.272 | 1.350      | 1.228 | ➡️ Moderate Mobilität    |
| **SUMPF**    | 1.282 | 1.287 | 1.345      | 1.242 | ➡️ Moderate Mobilität    |
| **OZEAN**    | 1.295 | 1.250 | 1.339      | 1.207 | ➡️ Moderate Mobilität    |
| **WALD**     | 1.288 | 1.279 | 1.321      | 1.182 | ➡️ Moderate Mobilität    |
| **GRASLAND** | 1.265 | 1.285 | 1.340      | 1.204 | ⬇️ Niedrigste Mobilität  |

**Interpretation:**

- **Weitläufige/karge Biome** (Tundra, Wüste) benötigen **hohe Mobilität** zur Ressourcensuche
- **Dichte Biome** (Wald, Grasland) erlauben **geringere Mobilität**

---

### 2. Überlebensfähigkeit nach Biomen

#### **Durchschnittliches Alter (Langlebigkeit)**

| Biom         | SAME  | SEED      | POPULATION | STEPS | Ø Gesamt      |
| ------------ | ----- | --------- | ---------- | ----- | ------------- |
| **OZEAN**    | 389.0 | **415.2** | 393.9      | 310.2 | **377.1** ⭐  |
| **GRASLAND** | 393.8 | 403.1     | 403.3      | 314.3 | **378.6** ⭐  |
| **WALD**     | 395.3 | 393.0     | 391.7      | 300.2 | **370.1** ⭐  |
| **SUMPF**    | 395.7 | 395.7     | 389.1      | 302.6 | **370.8** ⭐  |
| **GEBIRGE**  | 383.8 | 362.1     | 372.0      | 299.3 | **354.3**     |
| **TUNDRA**   | 242.7 | 254.1     | 229.4      | 172.1 | **224.6**     |
| **WÜSTE**    | 80.5  | 97.8      | 94.6       | 70.4  | **85.8** ⚠️   |
| **VULKAN**   | 66.1  | 65.8      | 60.5       | 51.4  | **61.0** ⚠️⚠️ |

**Interpretation:**

- **Günstige Biome** (Ozean, Grasland, Wald, Sumpf): Ø **370-378 Zeitschritte** Lebenserwartung
- **Extreme Biome** (Wüste, Vulkan): Ø **61-86 Zeitschritte** → **6x kürzere Lebensspanne!**

---

#### **Durchschnittliche Energie (Vitalität)**

| Biom         | SAME  | SEED  | POPULATION | STEPS | Interpretation                 |
| ------------ | ----- | ----- | ---------- | ----- | ------------------------------ |
| **WALD**     | 192.4 | 191.8 | 192.9      | 190.9 | ⚡⚡⚡ **Maximale Energie**    |
| **GRASLAND** | 191.5 | 190.5 | 191.9      | 190.9 | ⚡⚡⚡ Sehr hohe Energie       |
| **SUMPF**    | 182.6 | 184.3 | 185.2      | 179.8 | ⚡⚡ Hohe Energie              |
| **OZEAN**    | 180.1 | 183.5 | 181.0      | 173.0 | ⚡⚡ Hohe Energie              |
| **GEBIRGE**  | 158.3 | 159.0 | 159.6      | 150.3 | ⚡ Moderate Energie            |
| **TUNDRA**   | 83.3  | 83.9  | 83.4       | 74.7  | ⚠️ Niedrige Energie            |
| **WÜSTE**    | 60.8  | 63.0  | 60.5       | 60.0  | ⚠️⚠️ Sehr niedrige Energie     |
| **VULKAN**   | 60.8  | 58.9  | 59.5       | 60.9  | ⚠️⚠️ Kritisch niedrige Energie |

**Interpretation:**

- **Produktive Biome** ermöglichen **Energiereserven nahe dem Maximum (200)**
- **Karge Biome** zwingen Organismen zu **permanentem Energie-Mangel** (30-42% des Maximums)

---

### 3. Vergleich der Konfigurationen

#### **Einfluss der Startbedingungen**

| Konfiguration  | Charakteristik      | Durchschn. Anpassung | Durchschn. Alter |
| -------------- | ------------------- | -------------------- | ---------------- |
| **SAME**       | Gleiche Bedingungen | 1.193                | 280.7            |
| **SEED**       | Zufällige Seeds     | 1.285                | 293.2            |
| **POPULATION** | Variable Pop.       | 1.220                | 285.3            |
| **STEPS**      | Schrittweise        | 0.999                | 214.9            |

**Erkenntnisse:**

- **SEED** zeigt die **höchsten Anpassungswerte** → Verschiedene Anfangsbedingungen fördern Diversität
- **STEPS** zeigt die **niedrigsten Werte** → Schrittweise Entwicklung führt zu weniger extremen Merkmalen
- Das **durchschnittliche Alter** variiert um ~25% zwischen Konfigurationen

---

### 4. Korrelationsanalyse der Merkmale

Basierend auf den Korrelations-Heatmaps (siehe `korrelation_merkmale.png`):

**Wichtige Korrelationen:**

1. **Anpassung ↔ Mobilität**: Moderate positive Korrelation (0.3-0.5)
   - Höhere Anpassung geht oft mit höherer Mobilität einher
2. **Effizienz ↔ Energie**: Schwache positive Korrelation (0.1-0.3)
   - Effizientere Organismen haben etwas höhere Energiereserven
3. **Alter ↔ Energie**: Starke positive Korrelation (0.6-0.8)
   - Langlebige Organismen haben höhere Energiereserven
4. **Anpassung ↔ Alter**: Schwache negative Korrelation (-0.1 bis -0.3)
   - In extremen Biomen: Hohe Anpassung = Überlebensfähigkeit, aber trotzdem geringes Alter

---

### 5. Populationsentwicklung

Siehe `populationsentwicklung.png` und `biom_entwicklung_*.png` für Details.

**Beobachtungen:**

- **Anfangsphase (0-200 Schritte)**: Starkes Wachstum (~100 → ~700 Organismen)
- **Stabilisierungsphase (200-800)**: Weiter ansteigend (~700 → ~2000)
- **Plateauphase (800-2000)**: Schwankungen um ~2000-2500 Organismen

**Biom-spezifische Entwicklung:**

| Biom         | Entwicklung               | Bemerkung               |
| ------------ | ------------------------- | ----------------------- |
| **Wald**     | Kontinuierliches Wachstum | Sehr stabil             |
| **Grasland** | Kontinuierliches Wachstum | Sehr stabil             |
| **Ozean**    | Kontinuierliches Wachstum | Stabil                  |
| **Sumpf**    | Kontinuierliches Wachstum | Stabil                  |
| **Gebirge**  | Moderates Wachstum        | Stabil mit Schwankungen |
| **Tundra**   | Geringes Wachstum         | Instabiler              |
| **Wüste**    | Sehr geringes Wachstum    | Hohe Fluktuation        |
| **Vulkan**   | Sehr geringes Wachstum    | Hohe Fluktuation        |

---

## 📊 Schlussfolgerungen

### **These 1: Biome erzwingen spezifische evolutionäre Strategien**

✅ **Bestätigt**: Die Daten zeigen klare Unterschiede in den genetischen Merkmalen:

- **Extreme Biome** (Wüste, Vulkan, Tundra):

  - ⬆️ Hohe Anpassung (1.24-1.37)
  - ⬆️ Hohe Effizienz (1.83-1.93)
  - ⬆️ Hohe Mobilität (1.38-1.51)
  - ⬇️ Niedrige Energie (59-84)
  - ⬇️ Kurze Lebensspanne (61-225 Schritte)

- **Günstige Biome** (Wald, Grasland, Ozean, Sumpf):
  - ⬇️ Moderate Anpassung (1.17-1.22)
  - ⬇️ Moderate Effizienz (1.80-1.84)
  - ⬇️ Moderate Mobilität (1.25-1.35)
  - ⬆️ Hohe Energie (180-192)
  - ⬆️ Lange Lebensspanne (370-378 Schritte)

### **These 2: Überlebensfähigkeit korreliert mit Energie-Level**

✅ **Stark bestätigt**:

- Korrelation Alter ↔ Energie: **0.6-0.8**
- Biome mit hoher Energie haben **6x längere Lebensspannen**
- Energie ist der **kritischste Überlebensfaktor**

### **These 3: Startbedingungen beeinflussen Evolution**

✅ **Bestätigt**:

- **Zufällige Seeds** produzieren **diversere Populationen** (höchste Anpassung: 1.285)
- **Schrittweise Entwicklung** produziert **konservativere Merkmale** (niedrigste Anpassung: 0.999)
- **Variable Populationen** zeigen **intermediäre Werte**

### **These 4: Extreme Biome als evolutionäre "Flaschenhälse"**

✅ **Bestätigt**:

- Vulkan und Wüste haben die **höchsten Selektionsdrücke**
- Nur Organismen mit **optimal angepassten Merkmalen** überleben
- Populationen bleiben klein und instabil

---

## 🎯 Empfehlungen für weitere Untersuchungen

1. **Migrations-Analyse**:

   - Untersuchen, wie oft Organismen zwischen Biomen wechseln
   - Gibt es bevorzugte Migrations-Routen?

2. **Generationen-Tracking**:

   - Wie viele Generationen entstehen in verschiedenen Biomen?
   - Verlauf der genetischen Veränderung über Generationen

3. **Trade-offs analysieren**:

   - Gibt es optimale Merkmals-Kombinationen?
   - Welche Kompromisse gehen Organismen ein?

4. **Langzeit-Simulationen**:
   - 5000+ Schritte für Konvergenz-Analyse
   - Erreichen Populationen evolutionäre Gleichgewichte?

---

## 📁 Erstellte Visualisierungen

Alle Grafiken befinden sich im Ordner `ergebnisse/`:

### Genetische Merkmale:

- `boxplot_Anpassung_nach_biom.png`
- `boxplot_Effizienz_nach_biom.png`
- `boxplot_Mobilitaet_nach_biom.png`
- `boxplot_Energie_nach_biom.png`
- `boxplot_Alter_nach_biom.png`

### Vergleiche zwischen Konfigurationen:

- `vergleich_konfigurationen_GRASLAND.png`
- `vergleich_konfigurationen_WALD.png`
- `vergleich_konfigurationen_WÜSTE.png`
- `vergleich_konfigurationen_TUNDRA.png`
- `vergleich_konfigurationen_GEBIRGE.png`
- `vergleich_konfigurationen_SUMPF.png`
- `vergleich_konfigurationen_OZEAN.png`
- `vergleich_konfigurationen_VULKAN.png`

### Populationsdynamik:

- `populationsentwicklung.png`
- `biom_entwicklung_same.png`
- `biom_entwicklung_seed.png`
- `biom_entwicklung_population.png`
- `biom_entwicklung_steps.png`

### Überlebensfähigkeit:

- `ueberlebensfaehigkeit_alter.png`
- `ueberlebensfaehigkeit_energie.png`

### Korrelationen:

- `korrelation_merkmale.png`

### Statistische Daten:

- `statistische_zusammenfassung.csv`
- `analyse_bericht.txt`

---

**Erstellt am:** 7. November 2025  
**Datensätze analysiert:** 49.702 Detail-Einträge, 36.030 Zeitreihen-Einträge  
**Simulationsläufe:** 20 (4 Konfigurationen × 5 Wiederholungen)
