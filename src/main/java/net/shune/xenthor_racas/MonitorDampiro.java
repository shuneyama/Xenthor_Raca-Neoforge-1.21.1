package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorDampiro {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String TAG_REGEN_BLOCK = ModPrincipal.ID_MOD + ":dampiro_regen_block";

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.DAMPIRO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        boolean ehNoite = !jogador.serverLevel().isDay();
        long bloqueioFim = jogador.getPersistentData().getLong(TAG_REGEN_BLOCK);
        boolean regenBloqueada = jogador.serverLevel().getGameTime() < bloqueioFim;

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DIG_SPEED, DURACAO_EFEITO, 2, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 1, true, false), null);

        if (ehNoite) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);
        } else {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 0, true, false), null);
        }

        if (!regenBloqueada) {
            if (ehNoite) {
                jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
            } else {
                jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 0, true, false), null);
            }
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.DAMPIRO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
            evento.setAmount(evento.getAmount() * 2.0f);
        }

        var atacante = evento.getSource().getDirectEntity();
        if (atacante instanceof ServerPlayer atacantePlayer) {
            ItemStack arma = atacantePlayer.getMainHandItem();
            if (arma.is(Items.WOODEN_SWORD)) {
                jogador.removeEffect(MobEffects.REGENERATION);
                jogador.getPersistentData().putLong(TAG_REGEN_BLOCK,
                        jogador.serverLevel().getGameTime() + 20 * 5);
                jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1, false, true), null);
            }
        }
    }
}