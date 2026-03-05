package net.shune.xenthor_racas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class EscalaJogador {

    private static final ResourceLocation ESCALA_ELFO_NAT =
            ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "elfo_natural.escala");
    private static final ResourceLocation ESCALA_ELFO_NEG =
            ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "elfo_negro.escala");
    private static final ResourceLocation ESCALA_ANAO =
            ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "anao.escala");

    public static void aplicarEscala(ServerPlayer jogador, Raca raca) {
        removerEscala(jogador);
        double escala = switch (raca) {
            case ELFO_NATURAL -> 0.20;
            case ELFO_NEGRO   -> 0.15;
            case ANAO         -> -0.30;
            default           -> 0.0;
        };
        if (escala == 0.0) return;

        ResourceLocation id = switch (raca) {
            case ELFO_NATURAL -> ESCALA_ELFO_NAT;
            case ELFO_NEGRO   -> ESCALA_ELFO_NEG;
            case ANAO         -> ESCALA_ANAO;
            default           -> ESCALA_ELFO_NAT;
        };

        AttributeInstance inst = jogador.getAttribute(Attributes.SCALE);
        if (inst == null) return;
        inst.addTransientModifier(new AttributeModifier(id, escala, AttributeModifier.Operation.ADD_VALUE));
    }

    public static void removerEscala(ServerPlayer jogador) {
        AttributeInstance inst = jogador.getAttribute(Attributes.SCALE);
        if (inst == null) return;
        inst.removeModifier(ESCALA_ELFO_NAT);
        inst.removeModifier(ESCALA_ELFO_NEG);
        inst.removeModifier(ESCALA_ANAO);
    }

    @SubscribeEvent
    public static void aoJogadorClonar(PlayerEvent.Clone evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        Raca raca = Raca.porId(racaSalva);
        if (raca == null) return;
        aplicarEscala(jogador, raca);
    }
}
