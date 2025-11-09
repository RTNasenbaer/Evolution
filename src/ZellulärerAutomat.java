import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ZellulärerAutomat - Hauptklasse für die CA-Simulation
 * 
 * Implementiert einen zellulären Automaten zur Analyse der
 * Überlebensfähigkeit von Organismen unter räumlichen Bedingungen.
 * 
 * CA-Eigenschaften:
 * - Diskrete Zeitschritte
 * - Zweidimensionales Gitter (m × n)
 * - Moore-Nachbarschaft (8 Nachbarn)
 * - Periodische Randbedingungen
 * - Zustandsmenge Q = {Umgebung × (Organismus ∪ ∅)}
 */
public class ZellulärerAutomat {

    private Gitter gitter;
    private int zeitschritt;
    private long seed;

    // Statistiken
    private List<StatistikEintrag> statistiken;

    private static class StatistikEintrag {
        int zeitschritt;
        int gesamtOrganismen;
        int[] proUmgebung;
        double durchschnittsAnpassung;
        double durchschnittsEffizienz;
        double durchschnittsMobilität;
        double durchschnittsEnergie;
        double durchschnittsAlter;

        @Override
        public String toString() {
            return String.format("T%d: %d Org | Fitness: A=%.2f E=%.2f M=%.2f | Ø-Energie=%.1f Ø-Alter=%.1f",
                    zeitschritt, gesamtOrganismen, durchschnittsAnpassung, durchschnittsEffizienz,
                    durchschnittsMobilität, durchschnittsEnergie, durchschnittsAlter);
        }
    }

    public ZellulärerAutomat(int breite, int höhe, long seed) {
        this.seed = seed;
        this.gitter = new Gitter(breite, höhe, seed);
        this.zeitschritt = 0;
        this.statistiken = new ArrayList<>();

        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  ZELLULÄRER AUTOMAT - Überlebensanalyse von Organismen        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║  Gitter: " + breite + " × " + höhe + " Zellen                                        ║");
        System.out.println("║  Nachbarschaft: Moore (8 Nachbarn)                            ║");
        System.out.println("║  Randbedingungen: Periodisch                                  ║");
        System.out.println("║  Seed: " + seed + "                                              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    // ============= CA-HAUPTSCHLEIFE =============

    /**
     * Führt einen Zeitschritt des CA aus
     * 
     * Ein Zeitschritt ist vollendet, wenn alle Zustände aktualisiert wurden.
     * Der neue Zustand zum Zeitpunkt t(k+1) entsteht aus den Zuständen
     * der Zelle und ihrer Nachbarschaft N zum Zeitpunkt t(k).
     */
    public void zeitschrittAusführen() {
        zeitschritt++;

        // Phase 1: Aktualisierung aller Organismen
        aktualisiereAlleOrganismen();

        // Phase 2: Reproduktion
        reproduktionPhase();

        // Phase 3: Migration (Wanderung)
        migrationsPhase();

        // Phase 4: Aufräumen
        gitter.räumeAuf();

        // Statistik erfassen
        erfasseStatistik();
    }

    /**
     * CA-Regel 1: Aktualisiere alle Organismen basierend auf Nachbarschaft
     */
    private void aktualisiereAlleOrganismen() {
        Zelle[][] zellen = gitter.getZellen();

        for (int x = 0; x < gitter.getBreite(); x++) {
            for (int y = 0; y < gitter.getHöhe(); y++) {
                Zelle zelle = zellen[x][y];

                if (zelle.istBesetzt()) {
                    // Nachbarschaftsinformation sammeln
                    int anzahlNachbarn = gitter.zähleLebendeNachbarn(x, y);
                    double nachbarEnergie = gitter.getDurchschnittsEnergieNachbarn(x, y);

                    // Organismus aktualisieren
                    Organismus org = zelle.getOrganismus();
                    org.aktualisiere(zelle.getUmgebung(), anzahlNachbarn, nachbarEnergie);
                }
            }
        }
    }

    /**
     * CA-Regel 2: Reproduktion in leere Nachbarzellen
     */
    private void reproduktionPhase() {
        List<ReproduktionsEvent> events = new ArrayList<>();

        // Sammle Reproduktionsereignisse
        Zelle[][] zellen = gitter.getZellen();
        for (int x = 0; x < gitter.getBreite(); x++) {
            for (int y = 0; y < gitter.getHöhe(); y++) {
                Zelle zelle = zellen[x][y];

                if (zelle.istBesetzt() && zelle.getOrganismus().kannReproduktion()) {
                    Zelle zielZelle = gitter.findeLeereNachbarzelle(x, y);
                    if (zielZelle != null) {
                        events.add(new ReproduktionsEvent(zelle, zielZelle));
                    }
                }
            }
        }

        // Führe Reproduktionen aus
        for (ReproduktionsEvent event : events) {
            if (event.ziel.istLeer()) { // Doppelcheck
                Organismus nachkomme = event.quelle.getOrganismus().reproduziere();
                if (nachkomme != null) {
                    event.ziel.setzeOrganismus(nachkomme);
                }
            }
        }
    }

    private static class ReproduktionsEvent {
        Zelle quelle;
        Zelle ziel;

        ReproduktionsEvent(Zelle quelle, Zelle ziel) {
            this.quelle = quelle;
            this.ziel = ziel;
        }
    }

    /**
     * CA-Regel 3: Migration zu Nachbarzellen (basierend auf Mobilität)
     */
    private void migrationsPhase() {
        List<MigrationsEvent> events = new ArrayList<>();

        // Sammle Migrationsereignisse
        Zelle[][] zellen = gitter.getZellen();
        for (int x = 0; x < gitter.getBreite(); x++) {
            for (int y = 0; y < gitter.getHöhe(); y++) {
                Zelle zelle = zellen[x][y];

                if (zelle.istBesetzt() && zelle.getOrganismus().kannWandern()) {
                    Zelle zielZelle = gitter.findeLeereNachbarzelle(x, y);
                    if (zielZelle != null) {
                        events.add(new MigrationsEvent(zelle, zielZelle));
                    }
                }
            }
        }

        // Führe Migrationen aus
        for (MigrationsEvent event : events) {
            if (event.ziel.istLeer()) { // Doppelcheck
                Organismus org = event.quelle.getOrganismus();
                event.ziel.setzeOrganismus(org);
                event.quelle.entferneOrganismus();
            }
        }
    }

    private static class MigrationsEvent {
        Zelle quelle;
        Zelle ziel;

        MigrationsEvent(Zelle quelle, Zelle ziel) {
            this.quelle = quelle;
            this.ziel = ziel;
        }
    }

    // ============= STATISTIK =============

    private void erfasseStatistik() {
        StatistikEintrag stats = new StatistikEintrag();
        stats.zeitschritt = zeitschritt;
        stats.gesamtOrganismen = gitter.zähleOrganismen();
        stats.proUmgebung = gitter.zähleOrganismenProUmgebung();

        // Durchschnittswerte berechnen
        double summeAnpassung = 0.0;
        double summeEffizienz = 0.0;
        double summeMobilität = 0.0;
        double summeEnergie = 0.0;
        double summeAlter = 0.0;
        int anzahl = 0;

        Zelle[][] zellen = gitter.getZellen();
        for (int x = 0; x < gitter.getBreite(); x++) {
            for (int y = 0; y < gitter.getHöhe(); y++) {
                if (zellen[x][y].istBesetzt()) {
                    Organismus org = zellen[x][y].getOrganismus();
                    summeAnpassung += org.getAnpassungsfähigkeit();
                    summeEffizienz += org.getEffizienz();
                    summeMobilität += org.getMobilität();
                    summeEnergie += org.getEnergie();
                    summeAlter += org.getAlter();
                    anzahl++;
                }
            }
        }

        if (anzahl > 0) {
            stats.durchschnittsAnpassung = summeAnpassung / anzahl;
            stats.durchschnittsEffizienz = summeEffizienz / anzahl;
            stats.durchschnittsMobilität = summeMobilität / anzahl;
            stats.durchschnittsEnergie = summeEnergie / anzahl;
            stats.durchschnittsAlter = summeAlter / anzahl;
        }

        statistiken.add(stats);
    }

    public void zeigeStatistik() {
        if (statistiken.isEmpty())
            return;

        StatistikEintrag stats = statistiken.get(statistiken.size() - 1);

        System.out.println("\n" + stats);
        System.out.println("├─ Pro Umgebung:");

        for (Umgebung u : Umgebung.values()) {
            int anzahl = stats.proUmgebung[u.getId()];
            if (anzahl > 0) {
                double prozent = (anzahl * 100.0) / stats.gesamtOrganismen;
                System.out.printf("│  %-12s: %4d (%.1f%%)\n", u.name(), anzahl, prozent);
            }
        }
    }

    // ============= SIMULATION =============

    public void simuliere(int schritte, boolean zeigeJedenSchritt) {
        System.out.println("\n▶ Starte Simulation für " + schritte + " Zeitschritte...\n");

        erfasseStatistik(); // Initialer Zustand

        for (int i = 0; i < schritte; i++) {
            zeitschrittAusführen();

            if (zeigeJedenSchritt || (i + 1) % 10 == 0) {
                zeigeStatistik();
            }

            // Abbruch bei Aussterben
            if (gitter.zähleOrganismen() == 0) {
                System.out.println("\n✖ ALLE ORGANISMEN AUSGESTORBEN bei Zeitschritt " + zeitschritt);
                break;
            }
        }

        System.out.println("\n✓ Simulation beendet");
    }

    // ============= EXPORT =============

    public void exportiereCSV(String dateiname) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dateiname, java.nio.charset.StandardCharsets.UTF_8))) {
            // Header
            writer.print(
                    "Zeitschritt,GesamtOrganismen,DurchschnAnpassung,DurchschnEffizienz,DurchschnMobilitaet,DurchschnEnergie,DurchschnAlter");
            for (Umgebung u : Umgebung.values()) {
                writer.print("," + u.name());
            }
            writer.println();

            // Daten
            for (StatistikEintrag stats : statistiken) {
                writer.printf("%d,%d,%.3f,%.3f,%.3f,%.2f,%.2f",
                        stats.zeitschritt, stats.gesamtOrganismen,
                        stats.durchschnittsAnpassung, stats.durchschnittsEffizienz,
                        stats.durchschnittsMobilität, stats.durchschnittsEnergie,
                        stats.durchschnittsAlter);

                for (int i = 0; i < stats.proUmgebung.length; i++) {
                    writer.print("," + stats.proUmgebung[i]);
                }
                writer.println();
            }

            System.out.println("\n✓ CSV exportiert: " + dateiname);
        } catch (IOException e) {
            System.err.println("✖ Fehler beim CSV-Export: " + e.getMessage());
        }
    }

    public void exportiereDetailCSV(String dateiname) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dateiname, java.nio.charset.StandardCharsets.UTF_8))) {
            // Header
            writer.println("Zeitschritt,X,Y,Umgebung,Anpassung,Effizienz,Mobilitaet,Energie,Alter,Lebendig");

            // Aktueller Zustand
            Zelle[][] zellen = gitter.getZellen();
            for (int x = 0; x < gitter.getBreite(); x++) {
                for (int y = 0; y < gitter.getHöhe(); y++) {
                    Zelle zelle = zellen[x][y];
                    if (zelle.istBesetzt()) {
                        Organismus org = zelle.getOrganismus();
                        writer.printf("%d,%d,%d,%s,%.3f,%.3f,%.3f,%.2f,%d,%b\n",
                                zeitschritt, x, y, zelle.getUmgebung().name(),
                                org.getAnpassungsfähigkeit(), org.getEffizienz(),
                                org.getMobilität(), org.getEnergie(), org.getAlter(),
                                org.istLebendig());
                    }
                }
            }

            System.out.println("✓ Detail-CSV exportiert: " + dateiname);
        } catch (IOException e) {
            System.err.println("✖ Fehler beim Detail-CSV-Export: " + e.getMessage());
        }
    }

    // ============= GETTER =============

    public Gitter getGitter() {
        return gitter;
    }

    public int getZeitschritt() {
        return zeitschritt;
    }
}
