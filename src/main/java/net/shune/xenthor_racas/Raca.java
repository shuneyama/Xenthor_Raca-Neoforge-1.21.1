package net.shune.xenthor_racas;

public enum Raca {

    HUMANO      ("humano"),
    ELFO_NATURAL("elfo_natural"),
    ELFO_NEGRO  ("elfo_negro"),
    ANAO        ("anao"),
    CELESTIAL   ("celestial"),
    MORTO_VIVO  ("morto_vivo"); // placeholder - raca futura

    public final String id;

    Raca(String id) {
        this.id = id;
    }

    public static Raca porId(String id) {
        for (Raca r : values())
            if (r.id.equalsIgnoreCase(id)) return r;
        return null;
    }
}
