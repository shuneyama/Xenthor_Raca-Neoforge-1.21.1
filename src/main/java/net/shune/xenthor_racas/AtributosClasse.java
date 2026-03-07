package net.shune.xenthor_racas;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public class AtributosClasse {

    private static final ResourceLocation ARMADURA_GUERREIRO        = rl("guerreiro.armadura");
    private static final ResourceLocation ARMADURA_GUERREIRO_MAGICO = rl("guerreiro_magico.armadura");
    private static final ResourceLocation ARMADURA_MAGO             = rl("mago.armadura");
    private static final ResourceLocation DANO_GUERREIRO            = rl("guerreiro.dano_ataque");
    private static final ResourceLocation DANO_MAGO                 = rl("mago.dano_ataque");
    private static final ResourceLocation MANA_GUERREIRO            = rl("guerreiro.mana_maxima");
    private static final ResourceLocation MANA_GUERREIRO_MAGICO     = rl("guerreiro_magico.mana_maxima");
    private static final ResourceLocation MANA_MAGO                 = rl("mago.mana_maxima");
    private static final ResourceLocation RES_MAGICA_GUERREIRO      = rl("guerreiro.resistencia_magica");
    private static final ResourceLocation RES_MAGICA_GUERREIRO_M    = rl("guerreiro_magico.resistencia_magica");
    private static final ResourceLocation RES_MAGICA_MAGO           = rl("mago.resistencia_magica");
    private static final ResourceLocation PODER_GUERREIRO           = rl("guerreiro.poder_magico");
    private static final ResourceLocation PODER_MAGO                = rl("mago.poder_magico");

    private static final String IRONS_MANA       = "irons_spellbooks:max_mana";
    private static final String IRONS_RES_MAGICA = "irons_spellbooks:magic_resistance";
    private static final String IRONS_PODER      = "irons_spellbooks:spell_power";

    public static void aplicarClasse(Player jogador, ClasseRaca classe) {
        removerTodosOsModificadores(jogador);
        switch (classe) {
            case GUERREIRO        -> aplicarGuerreiro(jogador);
            case GUERREIRO_MAGICO -> aplicarGuerreiroMagico(jogador);
            case MAGO             -> aplicarMago(jogador);
        }
    }

    public static void aplicarMagoComElemento(Player jogador, ElementoMago elemento) {
        aplicarMago(jogador);
        removerElementoMago(jogador);
        if (!ironsCarregado()) return;
        for (ElementoMago e : ElementoMago.values()) {
            ResourceLocation id = rl("mago.elemento." + e.id);
            double valor = (e == elemento) ? 0.15 : -0.20;
            addI(jogador, e.chaveAtributo, id, valor, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static void removerTodosOsModificadores(Player jogador) {
        removerVanilla(jogador, Attributes.ARMOR, ARMADURA_GUERREIRO, ARMADURA_GUERREIRO_MAGICO, ARMADURA_MAGO);
        removerVanilla(jogador, Attributes.ATTACK_DAMAGE, DANO_GUERREIRO, DANO_MAGO);
        if (ironsCarregado()) {
            removerIrons(jogador, IRONS_MANA,       MANA_GUERREIRO, MANA_GUERREIRO_MAGICO, MANA_MAGO);
            removerIrons(jogador, IRONS_RES_MAGICA, RES_MAGICA_GUERREIRO, RES_MAGICA_GUERREIRO_M, RES_MAGICA_MAGO);
            removerIrons(jogador, IRONS_PODER,      PODER_GUERREIRO, PODER_MAGO);
            removerElementoMago(jogador);
        }
    }

    public static void removerElementoMago(Player jogador) {
        if (!ironsCarregado()) return;
        for (ElementoMago e : ElementoMago.values()) {
            AttributeInstance inst = buscarAtributo(jogador, e.chaveAtributo);
            if (inst != null) inst.removeModifier(rl("mago.elemento." + e.id));
        }
    }

    private static void aplicarGuerreiro(Player jogador) {
        addV(jogador, Attributes.ARMOR,         ARMADURA_GUERREIRO,  0.10,     AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addV(jogador, Attributes.ATTACK_DAMAGE, DANO_GUERREIRO,      0.4,      AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addI(jogador, IRONS_MANA,       MANA_GUERREIRO,       -10000.0, AttributeModifier.Operation.ADD_VALUE);
            addI(jogador, IRONS_RES_MAGICA, RES_MAGICA_GUERREIRO,   0.20,   AttributeModifier.Operation.ADD_VALUE);
            addI(jogador, IRONS_PODER,      PODER_GUERREIRO,      -10000.0, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarGuerreiroMagico(Player jogador) {
        addV(jogador, Attributes.ARMOR, ARMADURA_GUERREIRO_MAGICO, 0.05, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado())
            addI(jogador, IRONS_MANA, MANA_GUERREIRO_MAGICO, 200.0, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void aplicarMago(Player jogador) {
        addV(jogador, Attributes.ARMOR,         ARMADURA_MAGO, -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addV(jogador, Attributes.ATTACK_DAMAGE, DANO_MAGO,     -0.1,  AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addI(jogador, IRONS_MANA,       MANA_MAGO,       500.0, AttributeModifier.Operation.ADD_VALUE);
            addI(jogador, IRONS_RES_MAGICA, RES_MAGICA_MAGO,  0.10, AttributeModifier.Operation.ADD_VALUE);
            addI(jogador, IRONS_PODER,      PODER_MAGO,       0.05, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void addV(Player jogador, Holder<Attribute> atributo,
                             ResourceLocation id, double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst != null && inst.getModifier(id) == null)
            inst.addPermanentModifier(new AttributeModifier(id, valor, op));
    }

    private static void removerVanilla(Player jogador, Holder<Attribute> atributo, ResourceLocation... ids) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst == null) return;
        for (ResourceLocation id : ids) inst.removeModifier(id);
    }

    private static void addI(Player jogador, String chave, ResourceLocation id,
                             double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = buscarAtributo(jogador, chave);
        if (inst != null && inst.getModifier(id) == null)
            inst.addPermanentModifier(new AttributeModifier(id, valor, op));
    }

    private static void removerIrons(Player jogador, String chave, ResourceLocation... ids) {
        AttributeInstance inst = buscarAtributo(jogador, chave);
        if (inst == null) return;
        for (ResourceLocation id : ids) inst.removeModifier(id);
    }

    private static AttributeInstance buscarAtributo(Player jogador, String chave) {
        ResourceLocation alvo = ResourceLocation.tryParse(chave);
        if (alvo == null) return null;
        for (AttributeInstance inst : jogador.getAttributes().getSyncableAttributes())
            if (inst.getAttribute().unwrapKey().map(k -> k.location().equals(alvo)).orElse(false))
                return inst;
        return null;
    }

    private static boolean ironsCarregado() {
        return ModList.get().isLoaded("irons_spellbooks");
    }

    private static ResourceLocation rl(String caminho) {
        return ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, caminho);
    }
}