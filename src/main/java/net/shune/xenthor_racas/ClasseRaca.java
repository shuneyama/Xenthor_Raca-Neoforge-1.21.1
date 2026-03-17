package net.shune.xenthor_racas;

public enum ClasseRaca {

    GUERREIRO(
        "guerreiro",
        /* bonusArmadura        */ 0.10,
        /* bonusDanoAtaque      */ 0.4,
        /* resistenciaMagica    */ 0.20,
        /* maxMana              */ -10000.0,
        /* poderMagico          */ -10000.0
    ),

    GUERREIRO_MAGICO(
        "guerreiro_magico",
        /* bonusArmadura        */ 0.05,
        /* bonusDanoAtaque      */ 0.0,
        /* resistenciaMagica    */ 0.0,
        /* maxMana              */ 200.0,
        /* poderMagico          */ 0.0
    ),

    MAGO(
        "mago",
        /* bonusArmadura        */ -0.10,
        /* bonusDanoAtaque      */ -0.1,
        /* resistenciaMagica    */ 0.10,
        /* maxMana              */ 500.0,
        /* poderMagico          */ 0.05
    );

    public final String id;

    public final double bonusArmadura;

    public final double bonusDanoAtaque;

    public final double resistenciaMagica;

    public final double maxMana;

    public final double poderMagico;

    ClasseRaca(String id, double bonusArmadura, double bonusDanoAtaque,
               double resistenciaMagica, double maxMana, double poderMagico) {
        this.id               = id;
        this.bonusArmadura    = bonusArmadura;
        this.bonusDanoAtaque  = bonusDanoAtaque;
        this.resistenciaMagica = resistenciaMagica;
        this.maxMana          = maxMana;
        this.poderMagico      = poderMagico;
    }

    public static ClasseRaca porId(String id) {
        for (ClasseRaca classe : values()) {
            if (classe.id.equalsIgnoreCase(id)) return classe;
        }
        return null;
    }
}
