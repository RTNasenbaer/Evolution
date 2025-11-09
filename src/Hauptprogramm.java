import java.util.Scanner;

/**
 * Hauptprogramm - Zellulärer Automat für Überlebensanalyse
 * 
 * Interaktive Terminal-Anwendung zur Simulation und Analyse
 * der Überlebensfähigkeit von Organismen unter räumlichen Bedingungen.
 */
public class Hauptprogramm {

    private static ZellulärerAutomat ca;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        zeigeWillkommen();

        // Initialisierung
        System.out.print("\nGitter-Größe (z.B. 50): ");
        int größe = leseInt(50);

        System.out.print("Seed (Enter für zufällig): ");
        String seedInput = scanner.nextLine().trim();
        long seed = seedInput.isEmpty() ? System.currentTimeMillis() : Long.parseLong(seedInput);

        ca = new ZellulärerAutomat(größe, größe, seed);

        // Organismen platzieren
        System.out.print("\nAnzahl Start-Organismen: ");
        int anzahl = leseInt(100);

        System.out.print("Einheitliche Startpopulation? (j/N): ");
        String einheitlich = scanner.nextLine().trim().toLowerCase();

        if (einheitlich.equals("j") || einheitlich.equals("ja")) {
            ca.getGitter().verteileEinheitlicheOrganismen(anzahl);
            System.out.println("→ Alle Organismen starten mit identischen Merkmalen");
            System.out.println("→ Ideal zur Beobachtung adaptiver Radiation\n");
        } else {
            ca.getGitter().verteileOrganismen(anzahl);
        }

        // Hauptmenü
        while (true) {
            zeigeHauptmenü();
            String befehl = scanner.nextLine().trim().toLowerCase();

            if (befehl.isEmpty())
                continue;

            switch (befehl.charAt(0)) {
            case '1':
                führeSimulationAus();
                break;
            case '2':
                zeigeGitter();
                break;
            case '3':
                zeigeStatistik();
                break;
            case '4':
                exportiereDaten();
                break;
            case '5':
                platziereOrganismen();
                break;
            case 'h':
            case '?':
                zeigeHilfe();
                break;
            case 'q':
            case 'x':
                System.out.println("\n✓ Programm beendet");
                return;
            default:
                System.out.println("✖ Unbekannter Befehl. Drücke 'h' für Hilfe.");
            }
        }
    }

    private static void zeigeWillkommen() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                ║");
        System.out.println("║        ZELLULÄRER AUTOMAT - Überlebensanalyse                 ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Analyse der Überlebensfähigkeit von Organismen durch         ║");
        System.out.println("║  Variation spezifischer Merkmale unter unterschiedlichen      ║");
        System.out.println("║  räumlichen Bedingungen mittels zellulärer Automaten.         ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private static void zeigeHauptmenü() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│ HAUPTMENÜ                                                      │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. Simulation ausführen                                        │");
        System.out.println("│ 2. Gitter anzeigen                                             │");
        System.out.println("│ 3. Statistik anzeigen                                          │");
        System.out.println("│ 4. Daten exportieren                                           │");
        System.out.println("│ 5. Organismen platzieren                                       │");
        System.out.println("│ h. Hilfe                                                       │");
        System.out.println("│ q. Beenden                                                     │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Befehl: ");
    }

    private static void führeSimulationAus() {
        System.out.print("\nAnzahl Zeitschritte: ");
        int schritte = leseInt(100);

        System.out.print("Jeden Schritt anzeigen? (j/n): ");
        boolean jedenSchritt = scanner.nextLine().trim().toLowerCase().startsWith("j");

        ca.simuliere(schritte, jedenSchritt);
    }

    private static void zeigeGitter() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ GITTER-VISUALISIERUNG (Zeitschritt " + ca.getZeitschritt() + ")");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        Gitter gitter = ca.getGitter();
        int größe = Math.min(gitter.getBreite(), 40); // Max 40x40 für Terminal

        // Legende
        System.out.println("\nLegende:");
        System.out.println("  · = Leer");
        System.out.println("  ● = Organismus");

        // Gitter zeichnen
        System.out.print("\n  ");
        for (int x = 0; x < größe; x++) {
            System.out.print(x % 10);
        }
        System.out.println();

        for (int y = 0; y < größe; y++) {
            System.out.printf("%2d ", y);
            for (int x = 0; x < größe; x++) {
                Zelle zelle = gitter.getZelle(x, y);
                if (zelle.istBesetzt()) {
                    System.out.print("●");
                } else {
                    System.out.print("·");
                }
            }
            System.out.println();
        }
    }

    private static void zeigeStatistik() {
        ca.zeigeStatistik();
    }

    private static void exportiereDaten() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│ DATEN EXPORTIEREN                                              │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. Zeitreihen-CSV (Statistik über Zeit)                        │");
        System.out.println("│ 2. Detail-CSV (Aktueller Zustand aller Organismen)            │");
        System.out.println("│ 3. Beide                                                       │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Auswahl: ");

        String auswahl = scanner.nextLine().trim();
        long timestamp = System.currentTimeMillis();

        switch (auswahl) {
        case "1":
            ca.exportiereCSV("data/ca_zeitreihe_" + timestamp + ".csv");
            break;
        case "2":
            ca.exportiereDetailCSV("data/ca_detail_" + timestamp + ".csv");
            break;
        case "3":
            ca.exportiereCSV("data/ca_zeitreihe_" + timestamp + ".csv");
            ca.exportiereDetailCSV("data/ca_detail_" + timestamp + ".csv");
            break;
        default:
            System.out.println("✖ Ungültige Auswahl");
        }
    }

    private static void platziereOrganismen() {
        System.out.println("\n┌────────────────────────────────────────────────────────────────┐");
        System.out.println("│ ORGANISMEN PLATZIEREN                                          │");
        System.out.println("├────────────────────────────────────────────────────────────────┤");
        System.out.println("│ 1. Zufällig verteilen                                          │");
        System.out.println("│ 2. In spezifischer Umgebung                                    │");
        System.out.println("└────────────────────────────────────────────────────────────────┘");
        System.out.print("Auswahl: ");

        String auswahl = scanner.nextLine().trim();

        System.out.print("Anzahl Organismen: ");
        int anzahl = leseInt(50);

        switch (auswahl) {
        case "1":
            ca.getGitter().verteileOrganismen(anzahl);
            break;
        case "2":
            System.out.println("\nVerfügbare Umgebungen:");
            for (Umgebung u : Umgebung.values()) {
                System.out.println("  " + u.getId() + ". " + u.name());
            }
            System.out.print("Umgebung wählen (ID): ");
            int umgId = leseInt(0);
            Umgebung umgebung = Umgebung.vonId(umgId);
            ca.getGitter().verteileOrganismenInUmgebung(anzahl, umgebung);
            break;
        default:
            System.out.println("✖ Ungültige Auswahl");
        }
    }

    private static void zeigeHilfe() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ HILFE                                                          ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                ║");
        System.out.println("║ ZELLULÄRER AUTOMAT                                             ║");
        System.out.println("║                                                                ║");
        System.out.println("║ Eigenschaften:                                                 ║");
        System.out.println("║  • Diskrete Zeitschritte                                       ║");
        System.out.println("║  • Zweidimensionales Gitter (m × n Zellen)                     ║");
        System.out.println("║  • Moore-Nachbarschaft (8 angrenzende Zellen)                  ║");
        System.out.println("║  • Periodische Randbedingungen (Torus)                         ║");
        System.out.println("║                                                                ║");
        System.out.println("║ Zustand einer Zelle:                                           ║");
        System.out.println("║  • Umgebung (räumliche Bedingung)                              ║");
        System.out.println("║  • Organismus (optional, mit 3 Merkmalen)                      ║");
        System.out.println("║                                                                ║");
        System.out.println("║ CA-Regeln:                                                     ║");
        System.out.println("║  1. Aktualisierung: Energie basierend auf Umgebung & Nachbarn  ║");
        System.out.println("║  2. Reproduktion: In leere Nachbarzellen                       ║");
        System.out.println("║  3. Migration: Wanderung zu Nachbarzellen                      ║");
        System.out.println("║  4. Selektion: Tod bei Energiemangel                           ║");
        System.out.println("║                                                                ║");
        System.out.println("║ Merkmale der Organismen:                                       ║");
        System.out.println("║  • Anpassungsfähigkeit: Resistenz gegen Temperatur/Feuchtigkeit║");
        System.out.println("║  • Effizienz: Nahrungsnutzung                                  ║");
        System.out.println("║  • Mobilität: Wanderungsfähigkeit                              ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    private static int leseInt(int standard) {
        try {
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? standard : Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return standard;
        }
    }
}
