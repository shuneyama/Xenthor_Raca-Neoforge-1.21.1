package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAnao {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ANAO.id.equals(racaSalva)) return;

        // Resistencia II permanente
        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);

        // Haste IV apenas em cavernas (abaixo do nivel do ceu, sem visao do ceu)
        boolean emCaverna = !jogador.serverLevel().canSeeSky(jogador.blockPosition());
        if (emCaverna) {
            jogador.forceAddEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED, DURACAO_EFEITO, 3, true, false), null);
        } else {
            var haste = jogador.getEffect(MobEffects.DIG_SPEED);
            if (haste != null && haste.isAmbient())
                jogador.removeEffect(MobEffects.DIG_SPEED);
        }
    }

    @SubscribeEvent
    public static void aoKnockback(LivingKnockBackEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ANAO.id.equals(racaSalva)) return;

        evento.setCanceled(true);
    }
}
