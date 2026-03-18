package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorBestialAereo {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        if (!Raca.BESTIAL_AEREO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DIG_SPEED, DURACAO_EFEITO, 2, true, false), null);
    }
}
