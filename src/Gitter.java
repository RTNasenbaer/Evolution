import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gitter - Zweidimensionales Feld für zellulären Automaten
 * 
 * - Moore-Nachbarschaft (8 angrenzende Zellen)
 * - Periodische Randbedingungen (Torus-Topologie)
 */
public class Gitter {

    private final int breite;
    private final int höhe;
    private Zelle[][] zellen;
    private Random zufall;

    public Gitter(int breite, int höhe, long seed) {
        this.breite = breite;
        this.höhe = höhe;
        this.zufall = new Random(seed);
        initialisiere();
    }

    /**
     * Initialisiert das Gitter mit zufälligen Umgebungen
     */
    private void initialisiere() {
        zellen = new Zelle[breite][höhe];

        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                // Zufällige Umgebung für jede Zelle
                Umgebung umgebung = Umgebung.values()[zufall.nextInt(Umgebung.values().length)];
                zellen[x][y] = new Zelle(x, y, umgebung);
            }
        }
    }

    /**
     * Initialisiert Gitter mit spezifischem Umgebungsmuster
     */
    public void setzeUmgebungsMuster(Umgebung[][] muster) {
        if (muster.length != breite || muster[0].length != höhe) {
            throw new IllegalArgumentException("Muster-Dimensionen passen nicht zum Gitter");
        }

        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                zellen[x][y].setzeUmgebung(muster[x][y]);
            }
        }
    }

    /**
     * Verteilt Organismen zufällig im Gitter
     */
    public void verteileOrganismen(int anzahl) {
        int platziert = 0;
        int versuche = 0;
        int maxVersuche = anzahl * 10;

        while (platziert < anzahl && versuche < maxVersuche) {
            int x = zufall.nextInt(breite);
            int y = zufall.nextInt(höhe);

            if (zellen[x][y].istLeer()) {
                zellen[x][y].setzeOrganismus(new Organismus());
                platziert++;
            }
            versuche++;
        }

        System.out.println("→ " + platziert + " Organismen platziert");
    }

    /**
     * Verteilt einheitliche Organismen (für Studien zur adaptiven Radiation)
     * Alle Organismen starten mit identischen Merkmalen (1.0/1.0/1.0)
     */
    public void verteileEinheitlicheOrganismen(int anzahl) {
        int platziert = 0;
        int versuche = 0;
        int maxVersuche = anzahl * 10;

        while (platziert < anzahl && versuche < maxVersuche) {
            int x = zufall.nextInt(breite);
            int y = zufall.nextInt(höhe);

            if (zellen[x][y].istLeer()) {
                zellen[x][y].setzeOrganismus(new Organismus(1.0, 1.0, 1.0));
                platziert++;
            }
            versuche++;
        }

        System.out.println("→ " + platziert + " einheitliche Organismen platziert (A=1.0 E=1.0 M=1.0)");
    }

    /**
     * Verteilt Organismen in spezifischer Umgebung
     */
    public void verteileOrganismenInUmgebung(int anzahl, Umgebung zielUmgebung) {
        List<Zelle> möglicheZellen = new ArrayList<>();

        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                if (zellen[x][y].getUmgebung() == zielUmgebung && zellen[x][y].istLeer()) {
                    möglicheZellen.add(zellen[x][y]);
                }
            }
        }

        int platziert = 0;
        for (int i = 0; i < Math.min(anzahl, möglicheZellen.size()); i++) {
            Zelle zelle = möglicheZellen.get(zufall.nextInt(möglicheZellen.size()));
            if (zelle.istLeer()) {
                zelle.setzeOrganismus(new Organismus());
                platziert++;
            }
        }

        System.out.println("→ " + platziert + " Organismen in " + zielUmgebung + " platziert");
    }

    // ============= MOORE-NACHBARSCHAFT (8 Nachbarn) =============

    /**
     * Holt alle Nachbarn einer Zelle (Moore-Nachbarschaft)
     * mit periodischen Randbedingungen
     */
    public List<Zelle> getNachbarn(int x, int y) {
        List<Zelle> nachbarn = new ArrayList<>(8);

        int[] dx = { -1, -1, -1, 0, 0, 1, 1, 1 };
        int[] dy = { -1, 0, 1, -1, 1, -1, 0, 1 };

        for (int i = 0; i < 8; i++) {
            int nx = periodisch(x + dx[i], breite);
            int ny = periodisch(y + dy[i], höhe);
            nachbarn.add(zellen[nx][ny]);
        }

        return nachbarn;
    }

    /**
     * Zählt lebende Nachbarn (für CA-Regeln)
     */
    public int zähleLebendeNachbarn(int x, int y) {
        int anzahl = 0;
        for (Zelle nachbar : getNachbarn(x, y)) {
            if (nachbar.istBesetzt()) {
                anzahl++;
            }
        }
        return anzahl;
    }

    /**
     * Berechnet durchschnittliche Energie der Nachbarn
     */
    public double getDurchschnittsEnergieNachbarn(int x, int y) {
        List<Zelle> nachbarn = getNachbarn(x, y);
        double summe = 0.0;
        int anzahl = 0;

        for (Zelle nachbar : nachbarn) {
            if (nachbar.istBesetzt()) {
                summe += nachbar.getOrganismus().getEnergie();
                anzahl++;
            }
        }

        return anzahl > 0 ? summe / anzahl : 0.0;
    }

    /**
     * Findet leere Nachbarzelle für Reproduktion
     */
    public Zelle findeLeereNachbarzelle(int x, int y) {
        List<Zelle> nachbarn = getNachbarn(x, y);
        List<Zelle> leer = new ArrayList<>();

        for (Zelle nachbar : nachbarn) {
            if (nachbar.istLeer()) {
                leer.add(nachbar);
            }
        }

        return leer.isEmpty() ? null : leer.get(zufall.nextInt(leer.size()));
    }

    /**
     * Periodische Randbedingung (Wrap-around)
     */
    private int periodisch(int position, int maximum) {
        if (position < 0)
            return maximum + position;
        if (position >= maximum)
            return position - maximum;
        return position;
    }

    // ============= GETTER =============

    public int getBreite() {
        return breite;
    }

    public int getHöhe() {
        return höhe;
    }

    public Zelle getZelle(int x, int y) {
        return zellen[x][y];
    }

    public Zelle[][] getZellen() {
        return zellen;
    }

    /**
     * Zählt alle lebenden Organismen
     */
    public int zähleOrganismen() {
        int anzahl = 0;
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                if (zellen[x][y].istBesetzt()) {
                    anzahl++;
                }
            }
        }
        return anzahl;
    }

    /**
     * Zählt Organismen pro Umgebung
     */
    public int[] zähleOrganismenProUmgebung() {
        int[] zähler = new int[Umgebung.values().length];

        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                if (zellen[x][y].istBesetzt()) {
                    Umgebung umg = zellen[x][y].getUmgebung();
                    zähler[umg.getId()]++;
                }
            }
        }

        return zähler;
    }

    /**
     * Räumt tote Organismen auf
     */
    public void räumeAuf() {
        for (int x = 0; x < breite; x++) {
            for (int y = 0; y < höhe; y++) {
                zellen[x][y].räumeAuf();
            }
        }
    }
}
