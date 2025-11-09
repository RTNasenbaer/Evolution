# Zellulärer Automat - Überlebensanalyse von Organismen

**Thema:** Analyse der Überlebensfähigkeit von Organismen durch Variation spezifischer Merkmale unter unterschiedlichen räumlichen Bedingungen mittels zellulärer Automaten.

## Übersicht

Dies ist eine vollständige Neuimplementierung als **echter zellulärer Automat** für wissenschaftliche Analysen.

### CA-Eigenschaften

- **Diskrete Zeitschritte**: `t₀ → t₁ → t₂ → ...`
- **Zweidimensionales Gitter**: `m × n` Zellen
- **Moore-Nachbarschaft**: 8 angrenzende Zellen
- **Periodische Randbedingungen**: Torus-Topologie (keine festen Ränder)
- **Zustandsmenge**: `Q = {Umgebung × (Organismus ∪ ∅)}`

### Zelluläre Automaten Regeln

Ein Zeitschritt `t_k` ist vollendet, wenn alle Zellzustände aktualisiert wurden. Der neue Zustand einer Zelle zum Zeitpunkt `t_(k+1)` entsteht aus den Zuständen der Zelle selbst und ihrer Moore-Nachbarschaft `N` zum Zeitpunkt `t_k`.

**CA-Regeln:**

1. **Aktualisierung**: Organismus verliert/gewinnt Energie basierend auf:

   - Umgebung (Temperatur, Feuchtigkeit, Nahrung)
   - Anzahl lebender Nachbarn (soziale Interaktion)
   - Eigenen Merkmalen (Anpassungsfähigkeit, Effizienz)

2. **Reproduktion**: Organismus mit genug Energie reproduziert in leere Nachbarzelle

   - Nachkomme erhält mutierte Merkmale
   - Energie wird aufgeteilt

3. **Migration**: Organismus kann zu leerer Nachbarzelle wandern

   - Wahrscheinlichkeit abhängig von Mobilität
   - Nur möglich bei ausreichend Energie

4. **Selektion**: Organismus stirbt bei Energiemangel
   - Zelle wird leer
   - Natürliche Selektion

## Struktur

### Klassen

```
Hauptprogramm.java         - Interaktive Terminal-Anwendung
ZellulärerAutomat.java     - CA-Hauptlogik und Zeitschritte
Gitter.java                - Zweidimensionales Zellgitter
Zelle.java                 - Einzelne Zelle (Umgebung + Organismus)
Organismus.java            - Organismus mit Merkmalen
Umgebung.java              - Räumliche Bedingungen (Biome)
```

### Merkmale der Organismen

Jeder Organismus hat 3 genetische Merkmale (Werte: 0.5 - 2.0):

1. **Anpassungsfähigkeit**: Resistenz gegen extreme Temperatur und Feuchtigkeit

   - Hoch = überlebt in extremen Bedingungen (Wüste, Vulkan, Tundra)
   - Niedrig = gut in gemäßigten Bedingungen (Grasland, Wald)

2. **Effizienz**: Nahrungsnutzung und Metabolismus

   - Hoch = benötigt weniger Nahrung, reproduziert früher
   - Niedrig = benötigt mehr Nahrung

3. **Mobilität**: Fähigkeit zur Wanderung zwischen Zellen
   - Hoch = häufigere Migration zu Nachbarzellen
   - Niedrig = bleibt meist in derselben Zelle

### Umgebungen (Biome)

8 verschiedene räumliche Bedingungen:

| Umgebung | Temperatur | Feuchtigkeit | Nahrung | Beschreibung                       |
| -------- | ---------- | ------------ | ------- | ---------------------------------- |
| GRASLAND | 20°C       | 50%          | 0.8     | Ausgeglichen, gut für Generalisten |
| WALD     | 15°C       | 80%          | 1.0     | Viel Nahrung, hohe Feuchtigkeit    |
| WÜSTE    | 40°C       | 10%          | 0.2     | Extreme Hitze, wenig Nahrung       |
| TUNDRA   | -15°C      | 40%          | 0.3     | Extreme Kälte, wenig Nahrung       |
| GEBIRGE  | -5°C       | 30%          | 0.5     | Kalt, moderate Nahrung             |
| SUMPF    | 25°C       | 90%          | 0.6     | Warm, sehr feucht                  |
| OZEAN    | 10°C       | 100%         | 0.7     | Kühl, maximale Feuchtigkeit        |
| VULKAN   | 60°C       | 5%           | 0.1     | Extreme Hitze, kaum Nahrung        |

## Schnellstart

### Kompilieren

**Windows:**

```cmd
kompiliere.bat
```

**Linux/Mac:**

```bash
chmod +x kompiliere.sh
./kompiliere.sh
```

### Ausführen

**Windows:**

```cmd
start.bat
```

**Linux/Mac:**

```bash
chmod +x start.sh
./start.sh
```

Oder direkt:

```cmd
java -cp build Hauptprogramm
```

## Benutzung

### Interaktives Menü

Nach dem Start:

1. **Gitter-Größe wählen** (z.B. 50 für 50×50)
2. **Seed eingeben** (oder Enter für zufällig)
3. **Anzahl Start-Organismen** (z.B. 100)

Dann wählen Sie im Hauptmenü:

```
1. Simulation ausführen    - Zeitschritte durchlaufen
2. Gitter anzeigen         - Visualisierung (max 40×40)
3. Statistik anzeigen      - Populationszahlen pro Umgebung
4. Daten exportieren       - CSV-Dateien generieren
5. Organismen platzieren   - Weitere Organismen hinzufügen
h. Hilfe                   - Zeigt CA-Erklärung
q. Beenden                 - Programm schließen
```

### Beispiel-Sitzung

```
Gitter-Größe: 50
Seed: 12345
Anzahl Start-Organismen: 150

Befehl: 1
Anzahl Zeitschritte: 100
Jeden Schritt anzeigen? (j/n): n

[Simulation läuft...]

Befehl: 3
[Zeigt Statistik]

Befehl: 4
Auswahl: 3
[Exportiert beide CSV-Dateien]

Befehl: q
```

## Datenexport

### CSV-Formate

**1. Zeitreihen-CSV** (`ca_zeitreihe_*.csv`)

```csv
Zeitschritt,GesamtOrganismen,DurchschnAnpassung,DurchschnEffizienz,DurchschnMobilität,DurchschnEnergie,DurchschnAlter,GRASLAND,WALD,WÜSTE,TUNDRA,GEBIRGE,SUMPF,OZEAN,VULKAN
0,150,1.250,1.180,1.320,100.0,0.0,18,22,15,19,21,17,20,18
10,142,1.265,1.195,1.305,89.3,8.2,20,25,12,18,19,15,22,11
...
```

**2. Detail-CSV** (`ca_detail_*.csv`)

```csv
Zeitschritt,X,Y,Umgebung,Anpassung,Effizienz,Mobilität,Energie,Alter,Lebendig
100,23,45,WÜSTE,1.823,1.456,0.987,78.3,45,true
100,12,8,WALD,0.765,1.234,1.543,112.5,23,true
...
```

### Python-Analyse

Die CSV-Dateien können mit Python analysiert werden:

```python
import pandas as pd
import matplotlib.pyplot as plt

# Zeitreihen laden
df = pd.read_csv('data/ca_zeitreihe_*.csv')

# Population über Zeit
plt.plot(df['Zeitschritt'], df['GesamtOrganismen'])
plt.xlabel('Zeitschritt')
plt.ylabel('Anzahl Organismen')
plt.title('Populationsdynamik')
plt.show()

# Merkmalsentwicklung
plt.plot(df['Zeitschritt'], df['DurchschnAnpassung'], label='Anpassung')
plt.plot(df['Zeitschritt'], df['DurchschnEffizienz'], label='Effizienz')
plt.plot(df['Zeitschritt'], df['DurchschnMobilität'], label='Mobilität')
plt.legend()
plt.show()

# Umgebungsverteilung
umgebungen = ['GRASLAND', 'WALD', 'WÜSTE', 'TUNDRA', 'GEBIRGE', 'SUMPF', 'OZEAN', 'VULKAN']
finale_zeile = df.iloc[-1]
plt.bar(umgebungen, [finale_zeile[u] for u in umgebungen])
plt.xticks(rotation=45)
plt.ylabel('Anzahl Organismen')
plt.title('Finale Verteilung nach Umgebung')
plt.tight_layout()
plt.show()
```

## Forschungsfragen

Dieser CA ermöglicht die Untersuchung von:

1. **Merkmals-Spezialisierung**: Welche Merkmale begünstigen Überleben in welchen Umgebungen?
2. **Räumliche Selektion**: Konzentrieren sich Organismen in spezifischen Biomen?
3. **Evolutionäre Adaptation**: Wie verändern sich Merkmalsdurchschnitte über Zeit?
4. **Soziale Effekte**: Wie beeinflusst Nachbarschaftsdichte das Überleben?
5. **Migrationsmuster**: Wandern mobile Organismen in bessere Umgebungen?

## Theoretischer Hintergrund

### Definition Zellulärer Automat

Nach der formalen Definition besteht ein zellulärer Automat aus:

1. **Diskreter Zeit**: `t ∈ ℕ₀`
2. **Zellgitter**: Zweidimensional `(x,y)` mit `x,y ∈ [0, n-1]`
3. **Zustandsmenge**: `Q = {Umgebung} × ({Organismus} ∪ {∅})`
4. **Nachbarschaft**: Moore-Nachbarschaft `N(x,y)` (8 Zellen)
5. **Randbedingungen**: Periodisch (Torus)
6. **Übergangsregel**: `f: Q^9 → Q` (Zelle + 8 Nachbarn → neuer Zustand)

### Warum ist dies ein CA?

✓ **Lokale Regeln**: Zustand hängt nur von Nachbarschaft ab  
✓ **Synchrone Updates**: Alle Zellen werden gleichzeitig aktualisiert  
✓ **Diskrete Zustände**: Umgebung + Organismus (mit diskreten Merkmalen)  
✓ **Gitterstruktur**: 2D-Array von Zellen  
✓ **Deterministische Regeln**: Bei gleichem Zustand + Nachbarschaft → gleiches Ergebnis (mit Seed)

### Unterschied zu altem System

| Alt (Agent-basiert)                   | Neu (Zellulärer Automat)                              |
| ------------------------------------- | ----------------------------------------------------- |
| Entities bewegen sich global          | Organismen bleiben in Zellen oder wandern zu Nachbarn |
| Sichtweite 12 Zellen                  | Moore-Nachbarschaft (8 Zellen)                        |
| Komplexe Energieformeln               | Einfache CA-Regeln                                    |
| Keine echte Nachbarschaftsinteraktion | Zustand hängt von Nachbarn ab                         |
| Respawning bei Tod                    | Echte Selektion (kein Respawn)                        |

## Lizenz

Für Bildungs- und Forschungszwecke frei verfügbar.
