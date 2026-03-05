package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorCelestial {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.CELESTIAL.id.equals(racaSalva)) return;

        boolean ehDia = jogador.serverLevel().isDay();

        // Resistencia II sempre
        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);

        // Regeneracao II apenas durante o dia
        if (ehDia) {
            jogador.forceAddEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        } else {
            var regen = jogador.getEffect(MobEffects.REGENERATION);
            if (regen != null && regen.isAmbient())
                jogador.removeEffect(MobEffects.REGENERATION);
        }

        // Glowing permanente
        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.GLOWING, DURACAO_EFEITO, 0, true, false), null);
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.CELESTIAL.id.equals(racaSalva)) return;

        // Imune a efeitos negativos de mortos-vivos (wither, decay)
        var effect = evento.getEffectInstance().getEffect();
        if (effect.is(MobEffects.WITHER)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
}
