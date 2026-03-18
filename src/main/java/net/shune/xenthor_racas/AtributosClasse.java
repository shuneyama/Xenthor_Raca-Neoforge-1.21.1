package net.shune.xenthor_racas;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.util.Optional;

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

    private static final ResourceLocation IRONS_MANA_RL       = ResourceLocation.parse("irons_spellbooks:max_mana");
    private static final ResourceLocation IRONS_RES_MAGICA_RL = ResourceLocation.parse("irons_spellbooks:spell_resist");
    private static final ResourceLocation IRONS_PODER_RL      = ResourceLocation.parse("irons_spellbooks:spell_power");

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
            ResourceLocation attrLoc = ResourceLocation.parse(e.chaveAtributo);
            addIrons(jogador, attrLoc, id, valor, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    public static void removerTodosOsModificadores(Player jogador) {
        removerVanilla(jogador, Attributes.ARMOR, ARMADURA_GUERREIRO, ARMADURA_GUERREIRO_MAGICO, ARMADURA_MAGO);
        removerVanilla(jogador, Attributes.ATTACK_DAMAGE, DANO_GUERREIRO, DANO_MAGO);
        if (ironsCarregado()) {
            removerIrons(jogador, IRONS_MANA_RL,       MANA_GUERREIRO, MANA_GUERREIRO_MAGICO, MANA_MAGO);
            removerIrons(jogador, IRONS_RES_MAGICA_RL, RES_MAGICA_GUERREIRO, RES_MAGICA_GUERREIRO_M, RES_MAGICA_MAGO);
            removerIrons(jogador, IRONS_PODER_RL,      PODER_GUERREIRO, PODER_MAGO);
            removerElementoMago(jogador);
        }
    }

    public static void removerElementoMago(Player jogador) {
        if (!ironsCarregado()) return;
        for (ElementoMago e : ElementoMago.values()) {
            ResourceLocation attrLoc = ResourceLocation.parse(e.chaveAtributo);
            AttributeInstance inst = getAtributoIrons(jogador, attrLoc);
            if (inst != null) inst.removeModifier(rl("mago.elemento." + e.id));
        }
    }

    public static boolean ehGuerreiro(Player jogador) {
        String classe = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
        return ClasseRaca.GUERREIRO.id.equals(classe);
    }

    private static void aplicarGuerreiro(Player jogador) {
        addV(jogador, Attributes.ARMOR,         ARMADURA_GUERREIRO,  2.0,   AttributeModifier.Operation.ADD_VALUE);
        addV(jogador, Attributes.ATTACK_DAMAGE, DANO_GUERREIRO,      0.4,   AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_RES_MAGICA_RL, RES_MAGICA_GUERREIRO, 0.20, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_MANA_RL,       MANA_GUERREIRO,      -1.0,  AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            addIrons(jogador, IRONS_PODER_RL,      PODER_GUERREIRO,     -1.0,  AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        }
    }

    private static void aplicarGuerreiroMagico(Player jogador) {
        addV(jogador, Attributes.ARMOR, ARMADURA_GUERREIRO_MAGICO, 1.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado())
            addIrons(jogador, IRONS_MANA_RL, MANA_GUERREIRO_MAGICO, 100.0, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void aplicarMago(Player jogador) {
        addV(jogador, Attributes.ARMOR,         ARMADURA_MAGO, -2.0,  AttributeModifier.Operation.ADD_VALUE);
        addV(jogador, Attributes.ATTACK_DAMAGE, DANO_MAGO,     -0.1,  AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_MANA_RL,       MANA_MAGO,       400.0, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_RES_MAGICA_RL, RES_MAGICA_MAGO,  0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_RL,      PODER_MAGO,       0.05, AttributeModifier.Operation.ADD_VALUE);
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

    private static void addIrons(Player jogador, ResourceLocation attrLoc, ResourceLocation modId,
                                 double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = getAtributoIrons(jogador, attrLoc);
        if (inst != null && inst.getModifier(modId) == null) {
            inst.addPermanentModifier(new AttributeModifier(modId, valor, op));
        }
    }

    private static void removerIrons(Player jogador, ResourceLocation attrLoc, ResourceLocation... ids) {
        AttributeInstance inst = getAtributoIrons(jogador, attrLoc);
        if (inst == null) return;
        for (ResourceLocation id : ids) inst.removeModifier(id);
    }

    private static AttributeInstance getAtributoIrons(Player jogador, ResourceLocation attrLoc) {
        Optional<Holder.Reference<Attribute>> holder = BuiltInRegistries.ATTRIBUTE.getHolder(attrLoc);
        if (holder.isEmpty()) return null;
        return jogador.getAttribute(holder.get());
    }

    private static boolean ironsCarregado() {
        return ModList.get().isLoaded("irons_spellbooks");
    }

    private static ResourceLocation rl(String caminho) {
        return ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, caminho);
    }
}