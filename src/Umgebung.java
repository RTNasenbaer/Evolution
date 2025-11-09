/**
 * Umgebung (Environment) - Räumliche Bedingungen des Gitters
 * 
 * Repräsentiert verschiedene räumliche Bedingungen (Biome),
 * die unterschiedliche Überlebensanforderungen stellen.
 */
public enum Umgebung {
    GRASLAND(0, "Grasland - Ausgeglichen", "#90EE90"), WALD(1, "Wald - Hohe Nahrung", "#228B22"), WÜSTE(2,
            "Wüste - Hitze & Trockenheit", "#FFD700"), TUNDRA(3, "Tundra - Extreme Kälte", "#E0FFFF"), GEBIRGE(4,
                    "Gebirge - Raues Terrain", "#808080"), SUMPF(5, "Sumpf - Hohe Feuchtigkeit", "#556B2F"), OZEAN(6,
                            "Ozean - Wasser", "#0077BE"), VULKAN(7, "Vulkan - Extreme Hitze", "#8B0000");

    private final int id;
    private final String beschreibung;
    private final String farbe;

    Umgebung(int id, String beschreibung, String farbe) {
        this.id = id;
        this.beschreibung = beschreibung;
        this.farbe = farbe;
    }

    public int getId() {
        return id;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public String getFarbe() {
        return farbe;
    }

    /**
     * Nahrungsverfügbarkeit (0.0 - 1.0)
     */
    public double getNahrung() {
        switch (this) {
        case GRASLAND:
            return 0.8;
        case WALD:
            return 1.0;
        case WÜSTE:
            return 0.2;
        case TUNDRA:
            return 0.3;
        case GEBIRGE:
            return 0.5;
        case SUMPF:
            return 0.6;
        case OZEAN:
            return 0.7;
        case VULKAN:
            return 0.1;
        default:
            return 0.5;
        }
    }

    /**
     * Temperatur (-20°C bis +60°C)
     */
    public double getTemperatur() {
        switch (this) {
        case GRASLAND:
            return 20.0;
        case WALD:
            return 15.0;
        case WÜSTE:
            return 40.0;
        case TUNDRA:
            return -15.0;
        case GEBIRGE:
            return -5.0;
        case SUMPF:
            return 25.0;
        case OZEAN:
            return 10.0;
        case VULKAN:
            return 60.0;
        default:
            return 20.0;
        }
    }

    /**
     * Feuchtigkeit (0% - 100%)
     */
    public double getFeuchtigkeit() {
        switch (this) {
        case GRASLAND:
            return 50.0;
        case WALD:
            return 80.0;
        case WÜSTE:
            return 10.0;
        case TUNDRA:
            return 40.0;
        case GEBIRGE:
            return 30.0;
        case SUMPF:
            return 90.0;
        case OZEAN:
            return 100.0;
        case VULKAN:
            return 5.0;
        default:
            return 50.0;
        }
    }

    public static Umgebung vonId(int id) {
        for (Umgebung u : values()) {
            if (u.id == id)
                return u;
        }
        return GRASLAND;
    }
}
