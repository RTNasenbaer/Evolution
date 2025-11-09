/**
 * Organismus - Zustand einer Zelle mit Überlebensmerkmalen
 * 
 * Merkmale (Traits) die das Überleben bestimmen:
 * - Anpassungsfähigkeit: Resistenz gegen extreme Temperaturen/Feuchtigkeit
 * - Effizienz: Nahrungsnutzung
 * - Mobilität: Fähigkeit zur Fortbewegung (beeinflusst Nachbarschaftsinteraktion)
 */
public class Organismus {

    // Genetische Merkmale (0.5 - 2.0)
    private double anpassungsfähigkeit;
    private double effizienz;
    private double mobilität;

    // Zustandsvariablen
    private double energie;
    private int alter;
    private boolean lebendig;

    // Konstanten
    private static final double MIN_MERKMAL = 0.5;
    private static final double MAX_MERKMAL = 2.0;
    private static final double START_ENERGIE = 100.0;
    private static final double MUTATIONS_RATE = 0.3; // Erhöht von 0.2
    private static final double MUTATIONS_STÄRKE = 0.4; // Erhöht von 0.3

    /**
     * Konstruktor mit zufälligen Merkmalen
     */
    public Organismus() {
        this.anpassungsfähigkeit = zufälligesMerkmal();
        this.effizienz = zufälligesMerkmal();
        this.mobilität = zufälligesMerkmal();
        this.energie = START_ENERGIE;
        this.alter = 0;
        this.lebendig = true;
    }

    /**
     * Konstruktor mit spezifischen Merkmalen (für Nachkommen)
     */
    public Organismus(double anpassungsfähigkeit, double effizienz, double mobilität) {
        this.anpassungsfähigkeit = begrenzeMerkmal(anpassungsfähigkeit);
        this.effizienz = begrenzeMerkmal(effizienz);
        this.mobilität = begrenzeMerkmal(mobilität);
        this.energie = START_ENERGIE;
        this.alter = 0;
        this.lebendig = true;
    }

    /**
     * Kopie-Konstruktor
     */
    public Organismus(Organismus original) {
        this.anpassungsfähigkeit = original.anpassungsfähigkeit;
        this.effizienz = original.effizienz;
        this.mobilität = original.mobilität;
        this.energie = original.energie;
        this.alter = original.alter;
        this.lebendig = original.lebendig;
    }

    // ============= ZELLULÄRE AUTOMATEN REGELN =============

    /**
     * Berechnet Überlebensfähigkeit in gegebener Umgebung.
     * Kernregel des CA: Wie gut passt dieser Organismus zur Umgebung?
     * SIGMOID-VERSION: Sanfte Übergänge mit scharfen Schwellenwerten
     */
    public double berechneÜberlebensfähigkeit(Umgebung umgebung) {
        double fitness = 1.0;

        // TEMPERATURSTRESS (Sigmoid!) - Anpassungsfähigkeit ist KRITISCH
        double tempAbweichung = Math.abs(umgebung.getTemperatur() - 20.0);
        if (tempAbweichung > 5.0) {
            // Sigmoid: 1/(1+e^(-k(x-x0))) transformiert zu Strafe
            double tempAnforderung = tempAbweichung / 10.0; // 0-4 für extreme Biome
            double tempResistenz = anpassungsfähigkeit; // 0.5-2.0
            // Je größer die Differenz, desto höher die Strafe
            double tempStress = sigmoid(tempAnforderung - tempResistenz, 2.0);
            fitness -= tempStress * 0.6;
        }

        // FEUCHTIGKEITSSTRESS (Sigmoid!) - Anpassungsfähigkeit ist KRITISCH
        double feuchtigkeitAbweichung = Math.abs(umgebung.getFeuchtigkeit() - 50.0);
        if (feuchtigkeitAbweichung > 15.0) {
            double feuchtigkeitAnforderung = feuchtigkeitAbweichung / 25.0; // 0-2 für extreme Biome
            double feuchtigkeitResistenz = anpassungsfähigkeit;
            double feuchtigkeitStress = sigmoid(feuchtigkeitAnforderung - feuchtigkeitResistenz, 2.0);
            fitness -= feuchtigkeitStress * 0.5;
        }

        // NAHRUNGSKNAPPHEIT (Sigmoid!) - Effizienz ist KRITISCH
        if (umgebung.getNahrung() < 0.6) {
            double nahrungsAnforderung = (1.0 - umgebung.getNahrung()) * 2.0; // 0.8-1.8 für knappe Biome
            double nahrungsEffizienz = effizienz;
            double hungerStress = sigmoid(nahrungsAnforderung - nahrungsEffizienz, 2.5);
            fitness -= hungerStress * 0.7;
        }

        // MOBILITÄT als Überlebensvorteil in extremen Biomen
        // Hohe Mobilität = kann bessere Mikrohabitate finden
        if (tempAbweichung > 25.0 || umgebung.getNahrung() < 0.3) {
            double mobilitätVorteil = sigmoid(mobilität - 1.0, 3.0);
            fitness += mobilitätVorteil * 0.2;
        }

        return Math.max(0.0, Math.min(1.0, fitness));
    }

    /**
     * Sigmoid-Funktion: 1 / (1 + e^(-k*x))
     * Gibt Werte zwischen 0 und 1 zurück
     * k = Steilheit (höher = schärferer Übergang)
     */
    private double sigmoid(double x, double k) {
        return 1.0 / (1.0 + Math.exp(-k * x));
    }

    /**
     * CA-Regel: Aktualisierung basierend auf Umgebung und Nachbarn
     * 
     * @param umgebung       Aktuelle räumliche Bedingung
     * @param anzahlNachbarn Anzahl lebender Nachbarn (Moore-Nachbarschaft)
     * @param nachbarEnergie Durchschnittsenergie der Nachbarn
     */
    public void aktualisiere(Umgebung umgebung, int anzahlNachbarn, double nachbarEnergie) {
        if (!lebendig)
            return;

        alter++;

        // Regel 1: Umgebungsstress (SIGMOID-basiert - moderater)
        double fitness = berechneÜberlebensfähigkeit(umgebung);
        double umgebungsVerlust = (1.0 - fitness) * 8.0; // 0-8 Energie

        // Regel 2: Grundmetabolismus (Effizienz reduziert Kosten)
        double metabolismus = 2.0 / effizienz;

        // Regel 3: Nahrungsaufnahme (abhängig von Umgebung)
        double nahrungsGewinn = umgebung.getNahrung() * 12.0 * effizienz; // Erhöht von 10.0

        // Regel 4: Soziale Interaktion (moderate Dichte ist gut)
        double sozialeAnpassung = 0.0;
        if (anzahlNachbarn < 2) {
            sozialeAnpassung = -1.0; // Isolation
        } else if (anzahlNachbarn > 6) {
            sozialeAnpassung = -2.0; // Überbevölkerung
        } else {
            sozialeAnpassung = 1.0; // Optimale Dichte
        }

        // Energieänderung
        double energieÄnderung = nahrungsGewinn - metabolismus - umgebungsVerlust + sozialeAnpassung;
        energie += energieÄnderung;

        // Tod bei Energiemangel
        if (energie <= 0) {
            energie = 0;
            lebendig = false;
        }

        // Energiemaximum
        if (energie > 200.0) {
            energie = 200.0;
        }
    }

    /**
     * CA-Regel: Kann dieser Organismus sich reproduzieren?
     * Bedingung: Genug Energie und nicht zu alt
     */
    public boolean kannReproduktion() {
        return lebendig && energie >= 100.0 && alter >= 3 && alter < 120; // Früher & länger
    }

    /**
     * CA-Regel: Erstelle Nachkommen mit Mutation
     */
    public Organismus reproduziere() {
        if (!kannReproduktion())
            return null;

        // Energiekosten
        energie -= 50.0; // Reduziert von 60

        // Nachkomme mit Mutation
        double neueAnpassung = mutiere(anpassungsfähigkeit);
        double neueEffizienz = mutiere(effizienz);
        double neueMobilität = mutiere(mobilität);

        return new Organismus(neueAnpassung, neueEffizienz, neueMobilität);
    }

    /**
     * CA-Regel: Kann dieser Organismus in Nachbarzelle wandern?
     * Höhere Mobilität = höhere Wahrscheinlichkeit
     */
    public boolean kannWandern() {
        if (!lebendig || energie < 20.0)
            return false;
        double wanderWahrscheinlichkeit = mobilität / 4.0; // 0.125 - 0.5
        return Math.random() < wanderWahrscheinlichkeit;
    }

    // ============= HILFSMETHODEN =============

    private double zufälligesMerkmal() {
        return MIN_MERKMAL + Math.random() * (MAX_MERKMAL - MIN_MERKMAL);
    }

    private double mutiere(double merkmal) {
        if (Math.random() < MUTATIONS_RATE) {
            double änderung = (Math.random() - 0.5) * 2.0 * MUTATIONS_STÄRKE * merkmal;
            merkmal += änderung;
        }
        return begrenzeMerkmal(merkmal);
    }

    private double begrenzeMerkmal(double merkmal) {
        return Math.max(MIN_MERKMAL, Math.min(MAX_MERKMAL, merkmal));
    }

    // ============= GETTER =============

    public double getAnpassungsfähigkeit() {
        return anpassungsfähigkeit;
    }

    public double getEffizienz() {
        return effizienz;
    }

    public double getMobilität() {
        return mobilität;
    }

    public double getEnergie() {
        return energie;
    }

    public int getAlter() {
        return alter;
    }

    public boolean istLebendig() {
        return lebendig;
    }

    @Override
    public String toString() {
        return String.format("Org[A=%.2f E=%.2f M=%.2f Energie=%.1f Alter=%d %s]",
                anpassungsfähigkeit, effizienz, mobilität, energie, alter,
                lebendig ? "LEBT" : "TOT");
    }
}
