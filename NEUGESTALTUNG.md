# Projekt-Neugestaltung: Echter Zellulärer Automat

## ✅ Vollständige Neuimplementierung

Das Projekt wurde **komplett von Grund auf neu geschrieben** als echter zellulärer Automat nach der formalen CA-Definition aus `cellularAutomata.txt`.

---

## 🎯 Problemlösungen

### 1. **Trait-Redundanz** → GELÖST

**Vorher**: 4 ähnliche Traits (endurance, adaptation, mobility, efficiency)  
**Jetzt**: 3 klar unterschiedliche Merkmale:

- **Anpassungsfähigkeit**: Resistenz gegen Temperatur/Feuchtigkeit (0.5-2.0)
- **Effizienz**: Nahrungsnutzung & Metabolismus (0.5-2.0)
- **Mobilität**: Wanderungsfähigkeit zu Nachbarzellen (0.5-2.0)

### 2. **Energie-Bug** → GELÖST

**Vorher**: `if (energy < 1.0) energy = 1.0` verhinderte Tod  
**Jetzt**: Klare Todesmechanik - Energie ≤ 0 = Tod, keine künstliche Untergrenze

### 3. **Vulkan-Gathering** → GELÖST

**Vorher**: Zufällige Bewegung + falsche Terrain-Penalty führten zu Anhäufungen  
**Jetzt**: CA-basierte Migration nur zu Nachbarzellen, echte Umgebungsselektion

### 4. **Kein echter CA** → GELÖST

**Vorher**: Agent-basiertes Modell mit globaler Sichtweite  
**Jetzt**: Echter CA mit lokalen Nachbarschaftsregeln

### 5. **Keine Adaptation** → GELÖST

**Vorher**: "Reproduktion" mutierte Traits in-place, kein echter Selektionsdruck  
**Jetzt**: Echte Nachkommen, Tod = permanente Entfernung, natürliche Selektion

---

## 📐 CA-Eigenschaften (gemäß Definition)

### ✓ Diskrete Zeitschritte

```
t₀ → t₁ → t₂ → t₃ → ...
```

Jeder Zeitschritt ist abgeschlossen, wenn alle Zellen aktualisiert wurden.

### ✓ Zweidimensionales Gitter

```
x_ij für i=0...49, j=0...49
```

50×50 Zellen (konfigurierbar)

### ✓ Moore-Nachbarschaft

```
8 angrenzende Zellen:
[NW] [N] [NE]
[W]  [C] [E]
[SW] [S] [SE]
```

### ✓ Periodische Randbedingungen

Torus-Topologie: Linker Rand verbunden mit rechtem Rand, oberer mit unterem.
→ Kein fester Rahmen, simuliert unendliches Feld

### ✓ Zustandsmenge

```
Q = {Umgebung} × ({Organismus} ∪ {∅})
```

Jede Zelle hat:

- Umgebung (GRASLAND, WALD, WÜSTE, etc.)
- Optional: Organismus mit 3 Merkmalen

### ✓ Lokale Übergangsregeln

Neuer Zustand von Zelle (x,y) zum Zeitpunkt t+1 hängt ab von:

- Eigenem Zustand bei t
- Zuständen der 8 Moore-Nachbarn bei t

---

## 🔬 CA-Regeln

### Regel 1: Aktualisierung (Energiehaushalt)

```java
energie += nahrungsGewinn - metabolismus - umgebungsVerlust + sozialeAnpassung
```

**Faktoren:**

- **Nahrungsgewinn**: `umgebung.getNahrung() * 10.0 * effizienz`
- **Metabolismus**: `2.0 / effizienz`
- **Umgebungsverlust**: Abhängig von Temperatur/Feuchtigkeit & Anpassungsfähigkeit
- **Soziale Anpassung**:
  - < 2 Nachbarn: -1.0 (Isolation)
  - 2-6 Nachbarn: +1.0 (optimal)
  - > 6 Nachbarn: -2.0 (Überbevölkerung)

### Regel 2: Reproduktion

```
WENN energie ≥ 120 UND alter ≥ 5 UND alter < 100
DANN erstelle Nachkommen in leerer Nachbarzelle mit mutierten Merkmalen
```

**Mutation:** 20% Wahrscheinlichkeit pro Merkmal, ±30% Änderung

### Regel 3: Migration

```
WENN mobilität hoch UND energie ≥ 20
DANN Wahrscheinlichkeit (mobilität/4) zu leerer Nachbarzelle wandern
```

### Regel 4: Selektion (Tod)

```
WENN energie ≤ 0
DANN Organismus stirbt (Zelle wird leer)
```

**Wichtig:** Kein Respawning! Tod ist permanent.

---

## 📁 Dateistruktur

### Java-Klassen

```
src/
├── Hauptprogramm.java         # Interaktive Anwendung
├── ZellulärerAutomat.java     # CA-Engine
├── Gitter.java                # 2D-Gitter mit Moore-Nachbarschaft
├── Zelle.java                 # Einzelne Zelle
├── Organismus.java            # Organismus mit Merkmalen
└── Umgebung.java              # Biome (Enum)
```

### Build & Run

```
kompiliere.bat / kompiliere.sh  # Kompilierung
start.bat / start.sh            # Ausführung
```

### Analyse

```
analysiere_ca.py               # Python-Analyse-Skript
```

---

## 📊 Datenexport für Forschung

### CSV-Formate

**1. Zeitreihe** (`ca_zeitreihe_*.csv`)

```csv
Zeitschritt,GesamtOrganismen,DurchschnAnpassung,DurchschnEffizienz,DurchschnMobilität,DurchschnEnergie,DurchschnAlter,GRASLAND,WALD,WÜSTE,...
```

**2. Detail** (`ca_detail_*.csv`)

```csv
Zeitschritt,X,Y,Umgebung,Anpassung,Effizienz,Mobilität,Energie,Alter,Lebendig
```

### Forschungsfragen

1. **Räumliche Selektion**: Konzentrieren sich Organismen in bestimmten Umgebungen?
2. **Merkmals-Spezialisierung**: Welche Merkmale begünstigen welche Biome?
3. **Evolutionäre Dynamik**: Wie verändern sich Merkmalsdurchschnitte?
4. **Nachbarschaftseffekte**: Wie beeinflusst lokale Dichte das Überleben?
5. **Migrationsmuster**: Wandern mobile Organismen erfolgreicher?

---

## 🚀 Schnellstart

### 1. Kompilieren

```cmd
kompiliere.bat
```

### 2. Ausführen

```cmd
start.bat
```

### 3. Simulation durchführen

```
Gitter-Größe: 50
Seed: 12345
Anzahl Start-Organismen: 150

Menü:
1. Simulation ausführen
   Zeitschritte: 200
   Jeden Schritt anzeigen: n

3. Statistik anzeigen
4. Daten exportieren
   Auswahl: 3 (beide CSV-Dateien)
```

### 4. Daten analysieren

```cmd
python analysiere_ca.py
```

---

## 🎓 Wissenschaftliche Validität

### Warum ist dies jetzt ein CA?

| Kriterium            | Erfüllt? | Details                         |
| -------------------- | -------- | ------------------------------- |
| Diskrete Zeit        | ✅       | Zeitschritte t=0,1,2,...        |
| Gitterstruktur       | ✅       | 50×50 2D-Array                  |
| Endliche Zustände    | ✅       | 8 Umgebungen × (Organismus ∪ ∅) |
| Lokale Nachbarschaft | ✅       | Moore (8 Zellen)                |
| Periodische Ränder   | ✅       | Torus-Topologie                 |
| Übergangsregeln      | ✅       | Zustand abhängig von Nachbarn   |
| Synchrone Updates    | ✅       | Alle Zellen gleichzeitig        |

### Unterschied zum alten System

| Aspekt        | Alt                           | Neu                              |
| ------------- | ----------------------------- | -------------------------------- |
| Paradigma     | Agent-basiert                 | Zellulärer Automat               |
| Bewegung      | Global (12 Zellen Sichtweite) | Lokal (Moore-Nachbarn)           |
| Nachbarschaft | Keine echte Interaktion       | 8 Nachbarn beeinflussen Zustand  |
| Reproduktion  | Mutation in-place             | Echte Nachkommen in Nachbarzelle |
| Tod           | Respawn mit Defaults          | Permanent (Selektion)            |
| Komplexität   | Komplexe Physik               | Einfache CA-Regeln               |

---

## 📝 Verwendung

### Terminal-Menü

```
┌────────────────────────────────────────────────────────────────┐
│ HAUPTMENÜ                                                      │
├────────────────────────────────────────────────────────────────┤
│ 1. Simulation ausführen                                        │
│ 2. Gitter anzeigen                                             │
│ 3. Statistik anzeigen                                          │
│ 4. Daten exportieren                                           │
│ 5. Organismen platzieren                                       │
│ h. Hilfe                                                       │
│ q. Beenden                                                     │
└────────────────────────────────────────────────────────────────┘
```

### Beispiel-Ausgabe

```
T100: 142 Org | Fitness: A=1.45 E=1.32 M=1.18 | Ø-Energie=95.3 Ø-Alter=28.4
├─ Pro Umgebung:
│  WALD        :   35 (24.6%)
│  GRASLAND    :   28 (19.7%)
│  SUMPF       :   22 (15.5%)
│  OZEAN       :   18 (12.7%)
│  GEBIRGE     :   15 (10.6%)
│  TUNDRA      :   12 (8.5%)
│  WÜSTE       :    8 (5.6%)
│  VULKAN      :    4 (2.8%)
```

---

## ✨ Zusammenfassung

**Was wurde erreicht:**

✅ **Echter zellulärer Automat** nach formaler Definition  
✅ **Lokale Nachbarschaftsregeln** (Moore, 8 Zellen)  
✅ **Periodische Randbedingungen** (Torus)  
✅ **Klare Merkmale** (3 statt 4, eindeutige Funktionen)  
✅ **Echte Evolution** (Nachkommen, Mutation, Selektion)  
✅ **Keine Bugs** (kein Energie-Clamp, korrekter Tod)  
✅ **Wissenschaftlich nutzbar** (CSV-Export, Python-Analyse)  
✅ **Deutsche Bezeichnungen** (wie gewünscht)

**Bereit für Forschung zum Thema:**  
_"Analyse der Überlebensfähigkeit von Organismen durch Variation spezifischer Merkmale unter unterschiedlichen räumlichen Bedingungen mittels zellulärer Automaten."_
