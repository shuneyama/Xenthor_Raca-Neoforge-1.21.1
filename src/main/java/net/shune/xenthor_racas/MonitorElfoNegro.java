package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorElfoNegro {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        boolean ehNoite = !jogador.serverLevel().isDay();
        boolean temSol  = jogador.serverLevel().isDay()
                && jogador.serverLevel().canSeeSky(jogador.blockPosition());

        aplicarEfeito(jogador, new MobEffectInstance(MobEffects.NIGHT_VISION, DURACAO_EFEITO, 0, true, false));
        aplicarEfeito(jogador, new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 0, true, false));

        if (ehNoite) {
            aplicarEfeito(jogador, new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 2, true, false));
        } else {
            var regen = jogador.getEffect(MobEffects.REGENERATION);
            if (regen != null && regen.isAmbient())
                jogador.removeEffect(MobEffects.REGENERATION);
        }

        if (FormaNegra.estaAtiva(jogador) && temSol) {
            FormaNegra.desativar(jogador, true);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.POISON)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static void aplicarEfeito(ServerPlayer jogador, MobEffectInstance efeito) {
        jogador.forceAddEffect(efeito, null);
    }
}