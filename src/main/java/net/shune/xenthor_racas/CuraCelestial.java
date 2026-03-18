package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;

public class CuraCelestial {

    public static final String TAG_COOLDOWN_CURA = ModPrincipal.ID_MOD + ":celestial_cura_cooldown";
    private static final long COOLDOWN_TICKS = 20 * 60 * 5; // 5 minutos
    private static final double RAIO = 5.0;

    public static boolean temCooldown(ServerPlayer jogador) {
        long fim = jogador.getPersistentData().getLong(TAG_COOLDOWN_CURA);
        return jogador.serverLevel().getGameTime() < fim;
    }

    public static long ticksRestantes(ServerPlayer jogador) {
        long fim = jogador.getPersistentData().getLong(TAG_COOLDOWN_CURA);
        return Math.max(0, fim - jogador.serverLevel().getGameTime());
    }

    public static void tentar(ServerPlayer jogador) {
        if (!Raca.CELESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (temCooldown(jogador)) {
            long segundos = ticksRestantes(jogador) / 20;
            jogador.sendSystemMessage(
                Component.literal("Cura em recarga! Aguarde " + segundos + "s.")
                    .withStyle(ChatFormatting.GOLD));
            return;
        }

        List<ServerPlayer> aliados = jogador.serverLevel().getPlayers(alvo ->
            alvo != jogador
            && alvo.distanceTo(jogador) <= RAIO
            && !Raca.MORTO_VIVO.id.equals(alvo.getPersistentData().getString(ModPrincipal.TAG_RACA))
        );

        int curados = 0;
        for (ServerPlayer aliado : aliados) {
            aliado.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 10, 2, false, true), null);
            aliado.forceAddEffect(new MobEffectInstance(MobEffects.ABSORPTION,   20 * 15, 1, false, true), null);
            aliado.sendSystemMessage(Component.literal("Você foi abençoado pelo Celestial!")
                    .withStyle(ChatFormatting.GOLD));
            curados++;
        }

        jogador.getPersistentData().putLong(TAG_COOLDOWN_CURA,
            jogador.serverLevel().getGameTime() + COOLDOWN_TICKS);

        if (curados > 0) {
            jogador.sendSystemMessage(Component.literal("Você curou " + curados + " aliado(s)!")
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            jogador.sendSystemMessage(Component.literal("Nenhum aliado proximo para curar.")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
