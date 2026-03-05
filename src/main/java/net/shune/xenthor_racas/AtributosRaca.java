package net.shune.xenthor_racas;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public class AtributosRaca {

    // Elfo Natural
    private static final ResourceLocation VELOCIDADE_ELFO_NAT     = rl("elfo_natural.velocidade");
    private static final ResourceLocation PODER_NATUREZA_ELFO_NAT = rl("elfo_natural.poder_natureza");
    private static final ResourceLocation PODER_CURA_ELFO_NAT     = rl("elfo_natural.poder_cura");
    private static final ResourceLocation RES_FOGO_ELFO_NAT       = rl("elfo_natural.res_fogo");
    private static final ResourceLocation RES_SANGUE_ELFO_NAT     = rl("elfo_natural.res_sangue");
    private static final ResourceLocation RES_ENDER_ELFO_NAT      = rl("elfo_natural.res_ender");
    private static final ResourceLocation RES_ELDRITCH_ELFO_NAT   = rl("elfo_natural.res_eldritch");

    // Elfo Negro
    private static final ResourceLocation VELOCIDADE_ELFO_NEG     = rl("elfo_negro.velocidade");
    private static final ResourceLocation FORCA_ELFO_NEG          = rl("elfo_negro.forca");
    private static final ResourceLocation PODER_SANGUE_ELFO_NEG   = rl("elfo_negro.poder_sangue");
    private static final ResourceLocation PODER_ENDER_ELFO_NEG    = rl("elfo_negro.poder_ender");
    private static final ResourceLocation PODER_ELDRITCH_ELFO_NEG = rl("elfo_negro.poder_eldritch");
    private static final ResourceLocation RES_HOLY_ELFO_NEG       = rl("elfo_negro.res_holy");

    // Anao
    private static final ResourceLocation VIDA_ANAO               = rl("anao.vida");
    private static final ResourceLocation MINERACAO_ANAO          = rl("anao.mineracao");
    private static final ResourceLocation LUCK_ANAO               = rl("anao.luck");

    // Celestial
    private static final ResourceLocation PODER_HOLY_CELESTIAL    = rl("celestial.poder_holy");
    private static final ResourceLocation RES_MAGIA_NEG_CELESTIAL = rl("celestial.res_magia_negra");

    // Irons Spellbooks
    private static final String IRONS_PODER_NAT    = "irons_spellbooks:nature_spell_power";
    private static final String IRONS_PODER_HOLY   = "irons_spellbooks:holy_spell_power";
    private static final String IRONS_PODER_FOGO   = "irons_spellbooks:fire_spell_power";
    private static final String IRONS_PODER_SANGUE = "irons_spellbooks:blood_spell_power";
    private static final String IRONS_PODER_ENDER  = "irons_spellbooks:ender_spell_power";
    private static final String IRONS_PODER_ELDRI  = "irons_spellbooks:eldritch_spell_power";

    public static void aplicarRaca(Player jogador, Raca raca) {
        removerTodosOsModificadores(jogador);
        switch (raca) {
            case ELFO_NATURAL -> aplicarElfoNatural(jogador);
            case ELFO_NEGRO   -> aplicarElfoNegro(jogador);
            case ANAO         -> aplicarAnao(jogador);
            case CELESTIAL    -> aplicarCelestial(jogador);
            case HUMANO       -> {}
        }
    }

    public static void removerTodosOsModificadores(Player jogador) {
        removerVanilla(jogador, Attributes.MOVEMENT_SPEED,
                VELOCIDADE_ELFO_NAT, VELOCIDADE_ELFO_NEG);
        removerVanilla(jogador, Attributes.ATTACK_DAMAGE, FORCA_ELFO_NEG);
        removerVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ANAO);
        removerVanilla(jogador, Attributes.MINING_EFFICIENCY, MINERACAO_ANAO);
        removerVanilla(jogador, Attributes.LUCK, LUCK_ANAO);
        if (ironsCarregado()) {
            removerIrons(jogador, IRONS_PODER_NAT,    PODER_NATUREZA_ELFO_NAT);
            removerIrons(jogador, IRONS_PODER_HOLY,   PODER_CURA_ELFO_NAT, RES_HOLY_ELFO_NEG, PODER_HOLY_CELESTIAL);
            removerIrons(jogador, IRONS_PODER_FOGO,   RES_FOGO_ELFO_NAT);
            removerIrons(jogador, IRONS_PODER_SANGUE, RES_SANGUE_ELFO_NAT, PODER_SANGUE_ELFO_NEG, RES_MAGIA_NEG_CELESTIAL);
            removerIrons(jogador, IRONS_PODER_ENDER,  RES_ENDER_ELFO_NAT,  PODER_ENDER_ELFO_NEG);
            removerIrons(jogador, IRONS_PODER_ELDRI,  RES_ELDRITCH_ELFO_NAT, PODER_ELDRITCH_ELFO_NEG);
        }
    }

    private static void aplicarElfoNatural(Player jogador) {
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_ELFO_NAT,
                0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_NAT,    PODER_NATUREZA_ELFO_NAT,  0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY,   PODER_CURA_ELFO_NAT,      0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO,   RES_FOGO_ELFO_NAT,       -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_SANGUE, RES_SANGUE_ELFO_NAT,     -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER,  RES_ENDER_ELFO_NAT,      -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI,  RES_ELDRITCH_ELFO_NAT,   -0.05, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarElfoNegro(Player jogador) {
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_ELFO_NEG,
                0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addVanilla(jogador, Attributes.ATTACK_DAMAGE, FORCA_ELFO_NEG,
                1.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_SANGUE_ELFO_NEG,   0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER,  PODER_ENDER_ELFO_NEG,    0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI,  PODER_ELDRITCH_ELFO_NEG, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY,   RES_HOLY_ELFO_NEG,      -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarAnao(Player jogador) {
        // +5 coracoes = +10 de max health
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ANAO,
                10.0, AttributeModifier.Operation.ADD_VALUE);
        // +20% velocidade de mineracao
        addVanilla(jogador, Attributes.MINING_EFFICIENCY, MINERACAO_ANAO,
                0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        // +20% fortuna via luck
        addVanilla(jogador, Attributes.LUCK, LUCK_ANAO,
                0.20, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void aplicarCelestial(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_HOLY,   PODER_HOLY_CELESTIAL,      0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_SANGUE, RES_MAGIA_NEG_CELESTIAL,  -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER,  RES_MAGIA_NEG_CELESTIAL,  -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI,  RES_MAGIA_NEG_CELESTIAL,  -0.05, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void addVanilla(Player jogador, Holder<Attribute> atributo,
                                    ResourceLocation id, double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst != null && inst.getModifier(id) == null)
            inst.addTransientModifier(new AttributeModifier(id, valor, op));
    }

    private static void removerVanilla(Player jogador, Holder<Attribute> atributo,
                                        ResourceLocation... ids) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst == null) return;
        for (ResourceLocation id : ids) inst.removeModifier(id);
    }

    private static void addIrons(Player jogador, String chave, ResourceLocation id,
                                  double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = buscarAtributo(jogador, chave);
        if (inst != null && inst.getModifier(id) == null)
            inst.addTransientModifier(new AttributeModifier(id, valor, op));
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
