package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class VooCelestial {

    public static final String TAG_VOO_ATIVO = ModPrincipal.ID_MOD + ":celestial_voo_ativo";

    public static boolean estaAtivo(ServerPlayer jogador) {
        return jogador.getPersistentData().getBoolean(TAG_VOO_ATIVO);
    }

    public static void alternar(ServerPlayer jogador) {
        if (!Raca.CELESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        boolean ativo = !estaAtivo(jogador);
        jogador.getPersistentData().putBoolean(TAG_VOO_ATIVO, ativo);

        if (ativo) {
            ativarElytra(jogador);
            jogador.sendSystemMessage(Component.literal("Asas ativadas! Pressione novamente para desativar.")
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            desativarElytra(jogador);
            jogador.sendSystemMessage(Component.literal("Asas desativadas.")
                    .withStyle(ChatFormatting.GRAY));
        }

        net.shune.xenthor_racas.rede.RedeXenthor.enviarVooCelestial(jogador, ativo);
    }

    private static void ativarElytra(ServerPlayer jogador) {
        // Ativa o voo de elytra via fall flying
        jogador.setDeltaMovement(jogador.getDeltaMovement().add(0, 0.1, 0));
        jogador.startFallFlying();
    }

    private static void desativarElytra(ServerPlayer jogador) {
        jogador.stopFallFlying();
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CELESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;
        if (!estaAtivo(jogador)) return;

        // Mantem o voo ativo mesmo sem elytra equipada
        if (!jogador.isFallFlying() && !jogador.onGround()) {
            ativarElytra(jogador);
        }

        // Desativa ao pousar no chao
        if (jogador.onGround()) {
            jogador.getPersistentData().putBoolean(TAG_VOO_ATIVO, false);
            net.shune.xenthor_racas.rede.RedeXenthor.enviarVooCelestial(jogador, false);
        }
    }
}
