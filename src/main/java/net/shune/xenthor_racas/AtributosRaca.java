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

    private static final ResourceLocation VELOCIDADE_ELFO_NAT     = rl("elfo_natural.velocidade");
    private static final ResourceLocation PODER_NATUREZA_ELFO_NAT = rl("elfo_natural.poder_natureza");
    private static final ResourceLocation PODER_CURA_ELFO_NAT     = rl("elfo_natural.poder_cura");
    private static final ResourceLocation RES_FOGO_ELFO_NAT       = rl("elfo_natural.res_fogo");
    private static final ResourceLocation RES_SANGUE_ELFO_NAT     = rl("elfo_natural.res_sangue");
    private static final ResourceLocation RES_ENDER_ELFO_NAT      = rl("elfo_natural.res_ender");
    private static final ResourceLocation RES_ELDRITCH_ELFO_NAT   = rl("elfo_natural.res_eldritch");

    private static final ResourceLocation VELOCIDADE_ELFO_NEG     = rl("elfo_negro.velocidade");
    private static final ResourceLocation FORCA_ELFO_NEG          = rl("elfo_negro.forca");
    private static final ResourceLocation PODER_SANGUE_ELFO_NEG   = rl("elfo_negro.poder_sangue");
    private static final ResourceLocation PODER_ENDER_ELFO_NEG    = rl("elfo_negro.poder_ender");
    private static final ResourceLocation PODER_ELDRITCH_ELFO_NEG = rl("elfo_negro.poder_eldritch");
    private static final ResourceLocation RES_HOLY_ELFO_NEG       = rl("elfo_negro.res_holy");

    private static final ResourceLocation VIDA_ANAO               = rl("anao.vida");
    private static final ResourceLocation MINERACAO_ANAO          = rl("anao.mineracao");
    private static final ResourceLocation LUCK_ANAO               = rl("anao.luck");

    private static final ResourceLocation PODER_HOLY_CELESTIAL    = rl("celestial.poder_holy");
    private static final ResourceLocation RES_MAGIA_NEG_CELESTIAL = rl("celestial.res_magia_negra");

    private static final ResourceLocation PODER_NEG_CORROMPIDO    = rl("corrompido.poder_negra");
    private static final ResourceLocation RES_HOLY_CORROMPIDO     = rl("corrompido.res_holy");

    private static final ResourceLocation VELOCIDADE_BESTIAL      = rl("bestial.velocidade");
    private static final ResourceLocation ARMADURA_BESTIAL        = rl("bestial.armadura");

    private static final ResourceLocation VIDA_BESTIAL_AEREO      = rl("bestial_aereo.vida");
    private static final ResourceLocation ARMADURA_BESTIAL_AEREO  = rl("bestial_aereo.armadura");
    private static final ResourceLocation RES_RAIO_BESTIAL_AEREO  = rl("bestial_aereo.res_raio");

    private static final ResourceLocation PODER_AGUA_TRITAO       = rl("tritao.poder_agua");
    private static final ResourceLocation PODER_GELO_TRITAO       = rl("tritao.poder_gelo");
    private static final ResourceLocation RES_FOGO_TRITAO         = rl("tritao.res_fogo");

    private static final ResourceLocation VIDA_FADA               = rl("fada.vida");
    private static final ResourceLocation ARMADURA_FADA           = rl("fada.armadura");
    private static final ResourceLocation PODER_NATUREZA_FADA     = rl("fada.poder_natureza");

    private static final ResourceLocation VIDA_OGRO               = rl("ogro.vida");
    private static final ResourceLocation VELOCIDADE_OGRO         = rl("ogro.velocidade");
    private static final ResourceLocation RES_HOLY_OGRO           = rl("ogro.res_holy");

    private static final ResourceLocation VIDA_ANDROID            = rl("android.vida");
    private static final ResourceLocation ARMADURA_ANDROID        = rl("android.armadura");
    private static final ResourceLocation RES_RAIO_ANDROID        = rl("android.res_raio");
    private static final ResourceLocation RES_AGUA_ANDROID        = rl("android.res_agua");
    private static final ResourceLocation RES_GELO_ANDROID        = rl("android.res_gelo");

    private static final ResourceLocation VIDA_DRAGONIC           = rl("dragonic.vida");
    private static final ResourceLocation ARMADURA_DRAGONIC       = rl("dragonic.armadura");
    private static final ResourceLocation PODER_NEG_DRAGONIC      = rl("dragonic.poder_negra");

    private static final ResourceLocation PODER_SANGUE_MORTO_VIVO = rl("morto_vivo.poder_sangue");
    private static final ResourceLocation RES_FOGO_MORTO_VIVO     = rl("morto_vivo.res_fogo");
    private static final ResourceLocation RES_HOLY_MORTO_VIVO     = rl("morto_vivo.res_holy");

    private static final ResourceLocation PODER_SANGUE_VAMPIRO   = rl("vampiro.poder_sangue");
    private static final ResourceLocation PODER_NEG_VAMPIRO      = rl("vampiro.poder_negra");
    private static final ResourceLocation RES_HOLY_VAMPIRO       = rl("vampiro.res_holy");
    private static final ResourceLocation RES_FOGO_VAMPIRO       = rl("vampiro.res_fogo");

    private static final ResourceLocation PODER_SANGUE_DAMPIRO   = rl("dampiro.poder_sangue");
    private static final ResourceLocation PODER_NEG_DAMPIRO      = rl("dampiro.poder_negra");
    private static final ResourceLocation RES_HOLY_DAMPIRO       = rl("dampiro.res_holy");
    private static final ResourceLocation RES_FOGO_DAMPIRO       = rl("dampiro.res_fogo");

    private static final ResourceLocation PODER_NAT_LOBISOMEM    = rl("lobisomem.poder_natureza");
    private static final ResourceLocation RES_HOLY_LOBISOMEM     = rl("lobisomem.res_holy");

    private static final ResourceLocation ARMADURA_AMALDICOADO   = rl("amaldicoado.armadura");
    private static final ResourceLocation RES_HOLY_AMALDICOADO   = rl("amaldicoado.res_holy");

    private static final ResourceLocation VIDA_ESPIRITO          = rl("espirito.vida");
    private static final ResourceLocation RES_HOLY_ESPIRITO      = rl("espirito.res_holy");

    private static final ResourceLocation VIDA_KITSUNE           = rl("kitsune.vida");

    private static final String IRONS_PODER_NAT    = "irons_spellbooks:nature_spell_power";
    private static final String IRONS_PODER_HOLY   = "irons_spellbooks:holy_spell_power";
    private static final String IRONS_PODER_FOGO   = "irons_spellbooks:fire_spell_power";
    private static final String IRONS_PODER_SANGUE = "irons_spellbooks:blood_spell_power";
    private static final String IRONS_PODER_ENDER  = "irons_spellbooks:ender_spell_power";
    private static final String IRONS_PODER_ELDRI  = "irons_spellbooks:eldritch_spell_power";
    private static final String IRONS_PODER_AGUA   = "irons_spellbooks:ice_spell_power";
    private static final String IRONS_PODER_RAIO   = "irons_spellbooks:lightning_spell_power";

    public static void aplicarRaca(Player jogador, Raca raca) {
        removerTodosOsModificadores(jogador);
        switch (raca) {
            case ELFO_NATURAL  -> aplicarElfoNatural(jogador);
            case ELFO_NEGRO    -> aplicarElfoNegro(jogador);
            case ANAO          -> aplicarAnao(jogador);
            case CELESTIAL     -> aplicarCelestial(jogador);
            case CORROMPIDO    -> aplicarCorrompido(jogador);
            case BESTIAL       -> aplicarBestial(jogador);
            case BESTIAL_AEREO -> aplicarBestialAereo(jogador);
            case TRITAO        -> aplicarTritao(jogador);
            case FADA          -> aplicarFada(jogador);
            case OGRO          -> aplicarOgro(jogador);
            case ANDROID       -> aplicarAndroid(jogador);
            case DRAGONIC      -> aplicarDragonic(jogador);
            case MORTO_VIVO    -> aplicarMortoVivo(jogador);
            case VAMPIRO       -> aplicarVampiro(jogador);
            case DAMPIRO       -> aplicarDampiro(jogador);
            case LOBISOMEM     -> aplicarLobisomem(jogador);
            case AMALDICOADO   -> aplicarAmaldicoado(jogador);
            case ESPIRITO      -> aplicarEspirito(jogador);
            case KITSUNE       -> aplicarKitsune(jogador);
            case HUMANO        -> {}
        }
    }

    public static void removerTodosOsModificadores(Player jogador) {
        removerVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_ELFO_NAT, VELOCIDADE_ELFO_NEG, VELOCIDADE_BESTIAL, VELOCIDADE_OGRO);
        removerVanilla(jogador, Attributes.ATTACK_DAMAGE, FORCA_ELFO_NEG);
        removerVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ANAO, VIDA_BESTIAL_AEREO, VIDA_FADA, VIDA_OGRO, VIDA_ANDROID, VIDA_DRAGONIC, VIDA_ESPIRITO, VIDA_KITSUNE);
        removerVanilla(jogador, Attributes.MINING_EFFICIENCY, MINERACAO_ANAO);
        removerVanilla(jogador, Attributes.LUCK, LUCK_ANAO);
        removerVanilla(jogador, Attributes.ARMOR, ARMADURA_BESTIAL, ARMADURA_BESTIAL_AEREO, ARMADURA_FADA, ARMADURA_ANDROID, ARMADURA_DRAGONIC, ARMADURA_AMALDICOADO);
        if (ironsCarregado()) {
            removerIrons(jogador, IRONS_PODER_NAT, PODER_NATUREZA_ELFO_NAT, PODER_NATUREZA_FADA, PODER_NAT_LOBISOMEM);
            removerIrons(jogador, IRONS_PODER_HOLY, PODER_CURA_ELFO_NAT, RES_HOLY_ELFO_NEG, PODER_HOLY_CELESTIAL, RES_HOLY_CORROMPIDO, RES_HOLY_OGRO, RES_HOLY_MORTO_VIVO, RES_HOLY_VAMPIRO, RES_HOLY_DAMPIRO, RES_HOLY_LOBISOMEM, RES_HOLY_AMALDICOADO, RES_HOLY_ESPIRITO);
            removerIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_ELFO_NAT, RES_FOGO_TRITAO, RES_FOGO_MORTO_VIVO, RES_FOGO_VAMPIRO, RES_FOGO_DAMPIRO);
            removerIrons(jogador, IRONS_PODER_SANGUE, RES_SANGUE_ELFO_NAT, PODER_SANGUE_ELFO_NEG, RES_MAGIA_NEG_CELESTIAL, PODER_NEG_CORROMPIDO, PODER_SANGUE_MORTO_VIVO, PODER_NEG_DRAGONIC, PODER_SANGUE_VAMPIRO, PODER_SANGUE_DAMPIRO, PODER_NEG_VAMPIRO, PODER_NEG_DAMPIRO);
            removerIrons(jogador, IRONS_PODER_ENDER, RES_ENDER_ELFO_NAT, PODER_ENDER_ELFO_NEG);
            removerIrons(jogador, IRONS_PODER_ELDRI, RES_ELDRITCH_ELFO_NAT, PODER_ELDRITCH_ELFO_NEG);
            removerIrons(jogador, IRONS_PODER_AGUA, PODER_AGUA_TRITAO, RES_AGUA_ANDROID, PODER_GELO_TRITAO, RES_GELO_ANDROID);
            removerIrons(jogador, IRONS_PODER_RAIO, RES_RAIO_BESTIAL_AEREO, RES_RAIO_ANDROID);
        }
    }

    private static void aplicarElfoNatural(Player jogador) {
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_ELFO_NAT, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_NAT, PODER_NATUREZA_ELFO_NAT, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, PODER_CURA_ELFO_NAT, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_ELFO_NAT, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_SANGUE, RES_SANGUE_ELFO_NAT, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, RES_ENDER_ELFO_NAT, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, RES_ELDRITCH_ELFO_NAT, -0.05, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarElfoNegro(Player jogador) {
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_ELFO_NEG, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addVanilla(jogador, Attributes.ATTACK_DAMAGE, FORCA_ELFO_NEG, 1.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_SANGUE_ELFO_NEG, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, PODER_ENDER_ELFO_NEG, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, PODER_ELDRITCH_ELFO_NEG, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_ELFO_NEG, -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarAnao(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ANAO, 10.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.MINING_EFFICIENCY, MINERACAO_ANAO, 0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addVanilla(jogador, Attributes.LUCK, LUCK_ANAO, 0.20, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void aplicarCelestial(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_HOLY, PODER_HOLY_CELESTIAL, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_SANGUE, RES_MAGIA_NEG_CELESTIAL, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, RES_MAGIA_NEG_CELESTIAL, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, RES_MAGIA_NEG_CELESTIAL, -0.05, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarCorrompido(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_NEG_CORROMPIDO, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, PODER_NEG_CORROMPIDO, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, PODER_NEG_CORROMPIDO, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_CORROMPIDO, -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarBestial(Player jogador) {
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_BESTIAL, 0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_BESTIAL, -0.10, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    private static void aplicarBestialAereo(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_BESTIAL_AEREO, -6.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_BESTIAL_AEREO, -0.30, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_RAIO, RES_RAIO_BESTIAL_AEREO, -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarTritao(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_AGUA, PODER_AGUA_TRITAO, 0.20, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_AGUA, PODER_GELO_TRITAO, 0.20, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_TRITAO, -0.20, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarFada(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_FADA, -10.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_FADA, -0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_NAT, PODER_NATUREZA_FADA, 0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarOgro(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_OGRO, 20.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.MOVEMENT_SPEED, VELOCIDADE_OGRO, -0.20, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_OGRO, -1.0, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarAndroid(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ANDROID, 10.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_ANDROID, 15.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_RAIO, RES_RAIO_ANDROID, -0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_AGUA, RES_AGUA_ANDROID, -0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_OGRO, -1.0, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarDragonic(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_DRAGONIC, 10.0, AttributeModifier.Operation.ADD_VALUE);
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_DRAGONIC, 2.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_NEG_DRAGONIC, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, PODER_NEG_DRAGONIC, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, PODER_NEG_DRAGONIC, 0.15, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarMortoVivo(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_SANGUE_MORTO_VIVO, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_MORTO_VIVO, -0.20, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_MORTO_VIVO, -0.20, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarVampiro(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_SANGUE_VAMPIRO, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, PODER_NEG_VAMPIRO, 0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, PODER_NEG_VAMPIRO, 0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_VAMPIRO, -0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_VAMPIRO, -0.15, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarDampiro(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_SANGUE, PODER_SANGUE_DAMPIRO, 0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ENDER, PODER_NEG_DAMPIRO, 0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_ELDRI, PODER_NEG_DAMPIRO, 0.05, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_DAMPIRO, -0.15, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_FOGO, RES_FOGO_DAMPIRO, -0.15, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarLobisomem(Player jogador) {
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_NAT, PODER_NAT_LOBISOMEM, 0.10, AttributeModifier.Operation.ADD_VALUE);
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_LOBISOMEM, -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarAmaldicoado(Player jogador) {
        addVanilla(jogador, Attributes.ARMOR, ARMADURA_AMALDICOADO, 10.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_AMALDICOADO, -0.15, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarEspirito(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_ESPIRITO, -10.0, AttributeModifier.Operation.ADD_VALUE);
        if (ironsCarregado()) {
            addIrons(jogador, IRONS_PODER_HOLY, RES_HOLY_ESPIRITO, -0.10, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    private static void aplicarKitsune(Player jogador) {
        addVanilla(jogador, Attributes.MAX_HEALTH, VIDA_KITSUNE, 12.0, AttributeModifier.Operation.ADD_VALUE);
    }

    private static void addVanilla(Player jogador, Holder<Attribute> atributo, ResourceLocation id, double valor, AttributeModifier.Operation op) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst != null && inst.getModifier(id) == null)
            inst.addPermanentModifier(new AttributeModifier(id, valor, op));
    }

    private static void removerVanilla(Player jogador, Holder<Attribute> atributo, ResourceLocation... ids) {
        AttributeInstance inst = jogador.getAttribute(atributo);
        if (inst == null) return;
        for (ResourceLocation id : ids) inst.removeModifier(id);
    }

    private static void addIrons(Player jogador, String chave, ResourceLocation id, double valor, AttributeModifier.Operation op) {
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