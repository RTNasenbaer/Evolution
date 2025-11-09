# Schnellreferenz - Zellulärer Automat

## 🚀 Schnellstart

```cmd
# Kompilieren
kompiliere.bat

# Starten
start.bat

# Oder direkt:
java -cp build Hauptprogramm
```

---

## 📋 Befehle

### Simulation Setup

```
Gitter-Größe: 50          # 50×50 Zellen
Seed: 12345               # Für Reproduzierbarkeit
Start-Organismen: 150     # Initial Population
```

### Hauptmenü

```
1 → Simulation ausführen
2 → Gitter visualisieren
3 → Statistik anzeigen
4 → CSV exportieren
5 → Organismen hinzufügen
h → Hilfe
q → Beenden
```

---

## 🔬 CA-Eigenschaften

```
├─ Diskrete Zeit: t = 0, 1, 2, ...
├─ 2D-Gitter: 50×50 Zellen (konfigurierbar)
├─ Nachbarschaft: Moore (8 Zellen)
├─ Randbedingungen: Periodisch (Torus)
└─ Zustand: (Umgebung, Organismus?)
```

---

## 🧬 Merkmale

```java
Anpassungsfähigkeit  0.5 ────────────── 2.0
                    schlecht     gut bei extremer Temp/Feuchtigkeit

Effizienz            0.5 ────────────── 2.0
                    hungrig      satt bei wenig Nahrung

Mobilität            0.5 ────────────── 2.0
                    sesshaft     wandert häufig
```

---

## 🌍 Umgebungen

| ID  | Name     | Temp  | Feucht | Nahrung | Gut für...                 |
| --- | -------- | ----- | ------ | ------- | -------------------------- |
| 0   | GRASLAND | 20°C  | 50%    | 0.8     | Generalisten               |
| 1   | WALD     | 15°C  | 80%    | 1.0     | Hohe Effizienz             |
| 2   | WÜSTE    | 40°C  | 10%    | 0.2     | Hohe Anpassung + Effizienz |
| 3   | TUNDRA   | -15°C | 40%    | 0.3     | Hohe Anpassung + Effizienz |
| 4   | GEBIRGE  | -5°C  | 30%    | 0.5     | Hohe Anpassung             |
| 5   | SUMPF    | 25°C  | 90%    | 0.6     | Hohe Anpassung             |
| 6   | OZEAN    | 10°C  | 100%   | 0.7     | Hohe Anpassung + Mobilität |
| 7   | VULKAN   | 60°C  | 5%     | 0.1     | ALLE Merkmale hoch         |

---

## 📊 CSV-Ausgaben

### Zeitreihe

```csv
Zeitschritt,GesamtOrganismen,DurchschnAnpassung,...,GRASLAND,WALD,...
0,150,1.25,...,18,22,...
10,142,1.31,...,20,25,...
```

### Detail

```csv
Zeitschritt,X,Y,Umgebung,Anpassung,Effizienz,Mobilität,Energie,Alter
100,23,45,WÜSTE,1.82,1.45,0.98,78.3,45
```

---

## 🐍 Python-Analyse

```bash
python analysiere_ca.py
```

**Ausgabe:**

- Populationsdynamik
- Merkmalsentwicklung
- Umgebungsverteilung
- Visualisierungen (PNG)

---

## 🎯 Forschungsfragen

✓ Welche Merkmale in welchen Umgebungen?  
✓ Räumliche Konzentration?  
✓ Evolutionäre Trends?  
✓ Nachbarschaftseffekte?  
✓ Migrationsmuster?

---

## 🔧 Anpassungen

### Gittergröße ändern

```java
// In Hauptprogramm.java beim Start eingeben
Gitter-Größe: 100  // Für 100×100
```

### Populations-Parameter

```java
// In Organismus.java
private static final double START_ENERGIE = 100.0;
private static final double MUTATIONS_RATE = 0.2;
```

### Umgebungs-Balance

```java
// In Umgebung.java
WÜSTE(..., 40.0, 10.0, 0.2, ...)
      // Temp, Feucht, Nahrung
```

---

## ⚡ Tipps

**Schnelle Analyse:**

1. Kleine Gittergröße (30×30)
2. Wenig Organismen (50)
3. Kurze Simulation (50 Schritte)

**Tiefe Analyse:**

1. Große Gittergröße (100×100)
2. Viele Organismen (300+)
3. Lange Simulation (500+ Schritte)
4. Export + Python-Visualisierung

**Reproduzierbarkeit:**

- Gleicher Seed = gleiche Umgebungsverteilung
- Mutations-Rate = Zufallsvariabilität bleibt

---

## 📚 Dokumentation

- `README_DE.md` - Ausführliche Anleitung
- `NEUGESTALTUNG.md` - Projekt-Übersicht
- `VERGLEICH.md` - Alt vs. Neu
- `cellularAutomata.txt` - CA-Theorie

---

## ❓ Häufige Fragen

**Q: Population stirbt aus?**  
A: Zu wenig Start-Organismen oder zu extreme Umgebungen. Erhöhen Sie Start-Population.

**Q: Keine Veränderung der Merkmale?**  
A: Zu kurze Simulation. Evolution braucht Zeit (100+ Schritte).

**Q: Alle in WALD/GRASLAND?**  
A: Normal! Das sind die besten Umgebungen. Zeigt funktionierende Selektion.

**Q: CSV-Dateien wo?**  
A: Im `data/` Ordner nach Export.

**Q: Python-Script funktioniert nicht?**  
A: `pip install pandas matplotlib numpy` ausführen.

---

## 🏆 Erfolgreiche Simulation

Zeichen für funktionierende Evolution:

✅ Population stabilisiert sich (nicht komplett aussterbend)  
✅ Merkmale verändern sich über Zeit  
✅ Konzentration in günstigen Umgebungen  
✅ Durchschnittsalter steigt  
✅ Durchschnittsenergie > 50

---

**Viel Erfolg mit der Analyse!** 🔬
