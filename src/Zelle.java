/**
 * Zelle - Einzelne Zelle im zellulären Automaten
 * 
 * Zustand der Zelle = {Umgebung, Organismus (optional)}
 */
public class Zelle {

    private final int x;
    private final int y;
    private Umgebung umgebung;
    private Organismus organismus; // null = leer

    public Zelle(int x, int y, Umgebung umgebung) {
        this.x = x;
        this.y = y;
        this.umgebung = umgebung;
        this.organismus = null;
    }

    // ============= ZUSTANDSABFRAGEN =============

    public boolean istBesetzt() {
        return organismus != null && organismus.istLebendig();
    }

    public boolean istLeer() {
        return organismus == null || !organismus.istLebendig();
    }

    // ============= ZUSTANDSÄNDERUNGEN =============

    public void setzeOrganismus(Organismus org) {
        this.organismus = org;
    }

    public void entferneOrganismus() {
        this.organismus = null;
    }

    public void setzeUmgebung(Umgebung umgebung) {
        this.umgebung = umgebung;
    }

    /**
     * Räumt tote Organismen auf
     */
    public void räumeAuf() {
        if (organismus != null && !organismus.istLebendig()) {
            organismus = null;
        }
    }

    // ============= GETTER =============

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Umgebung getUmgebung() {
        return umgebung;
    }

    public Organismus getOrganismus() {
        return organismus;
    }

    @Override
    public String toString() {
        String orgInfo = istBesetzt() ? organismus.toString() : "LEER";
        return String.format("Zelle(%d,%d) %s: %s", x, y, umgebung.name(), orgInfo);
    }
}
