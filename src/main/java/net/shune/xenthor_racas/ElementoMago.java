package net.shune.xenthor_racas;

public enum ElementoMago {

    FOGO      ("fogo",     "irons_spellbooks:fire_spell_power"),
    GELO      ("gelo",     "irons_spellbooks:ice_spell_power"),
    AGUA      ("agua",     "irons_spellbooks:water_spell_power"),
    SANGUE    ("sangue",   "irons_spellbooks:blood_spell_power"),
    ENDER     ("ender",    "irons_spellbooks:ender_spell_power"),
    ELDRITCH  ("eldritch", "irons_spellbooks:eldritch_spell_power"),
    HOLY      ("holy",     "irons_spellbooks:holy_spell_power"),
    NATUREZA  ("natureza", "irons_spellbooks:nature_spell_power"),
    RAIO      ("raio",     "irons_spellbooks:lightning_spell_power"),
    EVOCADOR  ("evocador", "irons_spellbooks:evocation_spell_power");

    public final String id;
    public final String chaveAtributo;

    ElementoMago(String id, String chaveAtributo) {
        this.id            = id;
        this.chaveAtributo = chaveAtributo;
    }

    public static ElementoMago porId(String id) {
        for (ElementoMago e : values())
            if (e.id.equalsIgnoreCase(id)) return e;
        return null;
    }
}
