package net.shune.xenthor_racas;

public enum Raca {

    HUMANO          ("humano"),
    ELFO_NATURAL    ("elfo_natural"),
    ELFO_NEGRO      ("elfo_negro"),
    ANAO            ("anao"),
    CELESTIAL       ("celestial"),
    CORROMPIDO      ("corrompido"),
    BESTIAL         ("bestial"),
    BESTIAL_AEREO   ("bestial_aereo"),
    TRITAO          ("tritao"),
    FADA            ("fada"),
    OGRO            ("ogro"),
    ANDROID         ("android"),
    DRAGONIC        ("dragonic"),
    MORTO_VIVO      ("morto_vivo"),
    VAMPIRO         ("vampiro"),
    DAMPIRO         ("dampiro"),
    LOBISOMEM       ("lobisomem"),
    AMALDICOADO     ("amaldicoado"),
    ESPIRITO        ("espirito"),
    KITSUNE         ("kitsune");

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