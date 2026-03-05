package net.shune.xenthor_racas;

/**
 * Enum representando as três classes jogáveis.
 * Cada classe carrega seus próprios modificadores de atributos.
 *
 * Atributos do Irons Spells utilizados:
 *   - irons_spellbooks:max_mana
 *   - irons_spellbooks:magic_resistance
 *   - irons_spellbooks:spell_power
 */
public enum ClasseRaca {

    /**
     * ⚔️ Guerreiro
     * Bônus:  +10% de armadura natural, 1.4 de dano de ataque, +20% de resistência mágica
     * Penalidade: sem mana, não pode conjurar magias
     */
    GUERREIRO(
        "guerreiro",
        /* bonusArmadura        */ 0.10,
        /* bonusDanoAtaque      */ 0.4,
        /* resistenciaMagica    */ 0.20,
        /* maxMana              */ -10000.0,
        /* poderMagico          */ -10000.0
    ),

    /**
     * ⚔️🧙 Guerreiro Mágico
     * Bônus:  200 de mana, +5% de armadura
     * Penalidade: nenhuma
     */
    GUERREIRO_MAGICO(
        "guerreiro_magico",
        /* bonusArmadura        */ 0.05,
        /* bonusDanoAtaque      */ 0.0,
        /* resistenciaMagica    */ 0.0,
        /* maxMana              */ 200.0,
        /* poderMagico          */ 0.0
    ),

    /**
     * 🧙 Mago
     * Bônus:  500 de mana, +10% de resistência mágica, +5% de poder mágico
     * Penalidade: -10% de armadura, 0.9 de dano de ataque
     */
    MAGO(
        "mago",
        /* bonusArmadura        */ -0.10,
        /* bonusDanoAtaque      */ -0.1,
        /* resistenciaMagica    */ 0.10,
        /* maxMana              */ 500.0,
        /* poderMagico          */ 0.05
    );

    // -------------------------------------------------------------------------
    // Campos
    // -------------------------------------------------------------------------

    /** Identificador usado no comando (minúsculo, sem espaços). */
    public final String id;

    /** Bônus/penalidade multiplicativo na armadura base (ex: 0.10 = +10%). */
    public final double bonusArmadura;

    /** Bônus/penalidade aditivo no dano de ataque (padrão vanilla é 1.0). */
    public final double bonusDanoAtaque;

    /** Bônus/penalidade aditivo na resistência mágica (Irons Spells). */
    public final double resistenciaMagica;

    /** Bônus/penalidade aditivo na mana máxima (Irons Spells). */
    public final double maxMana;

    /** Bônus/penalidade aditivo no poder mágico (Irons Spells). */
    public final double poderMagico;

    // -------------------------------------------------------------------------
    // Construtor
    // -------------------------------------------------------------------------

    ClasseRaca(String id, double bonusArmadura, double bonusDanoAtaque,
               double resistenciaMagica, double maxMana, double poderMagico) {
        this.id               = id;
        this.bonusArmadura    = bonusArmadura;
        this.bonusDanoAtaque  = bonusDanoAtaque;
        this.resistenciaMagica = resistenciaMagica;
        this.maxMana          = maxMana;
        this.poderMagico      = poderMagico;
    }

    // -------------------------------------------------------------------------
    // Utilitários
    // -------------------------------------------------------------------------

    /** Retorna a ClasseRaca correspondente ao id informado, ou null se não encontrada. */
    public static ClasseRaca porId(String id) {
        for (ClasseRaca classe : values()) {
            if (classe.id.equalsIgnoreCase(id)) return classe;
        }
        return null;
    }
}
