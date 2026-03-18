package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class PoderEspirito {

    private static final String TAG_ESPECTRAL = ModPrincipal.ID_MOD + ":espirito_espectral";
    private static final String TAG_ESPECTRAL_FIM = ModPrincipal.ID_MOD + ":espirito_espectral_fim";
    private static final String TAG_ESPECTRAL_CD = ModPrincipal.ID_MOD + ":espirito_espectral_cd";
    private static final int DURACAO_TICKS = 20 * 30;
    private static final int COOLDOWN_TICKS = 20 * 60 * 3;

    public static void tentar(ServerPlayer jogador) {
        if (!Raca.ESPIRITO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        long agora = jogador.serverLevel().getGameTime();

        if (jogador.getPersistentData().getBoolean(TAG_ESPECTRAL)) {
            desativar(jogador);
            return;
        }

        long fimCd = jogador.getPersistentData().getLong(TAG_ESPECTRAL_CD);
        if (agora < fimCd) {
            long seg = (fimCd - agora) / 20;
            jogador.sendSystemMessage(Component.literal("Forma espectral em recarga! Aguarde " + seg + "s.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        jogador.getPersistentData().putBoolean(TAG_ESPECTRAL, true);
        jogador.getPersistentData().putLong(TAG_ESPECTRAL_FIM, agora + DURACAO_TICKS);

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Integer.MAX_VALUE, 1, false, false), null);

        net.shune.xenthor_racas.rede.RedeXenthor.enviarEspectral(jogador, true);
        jogador.sendSystemMessage(Component.literal("Forma espectral ativada! Duração: 30 segundos.")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    private static void desativar(ServerPlayer jogador) {
        jogador.getPersistentData().putBoolean(TAG_ESPECTRAL, false);
        jogador.getPersistentData().remove(TAG_ESPECTRAL_FIM);
        jogador.removeEffect(MobEffects.INVISIBILITY);
        jogador.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);

        long agora = jogador.serverLevel().getGameTime();
        jogador.getPersistentData().putLong(TAG_ESPECTRAL_CD, agora + COOLDOWN_TICKS);

        net.shune.xenthor_racas.rede.RedeXenthor.enviarEspectral(jogador, false);
        jogador.sendSystemMessage(Component.literal("Forma espectral desativada.")
                .withStyle(ChatFormatting.GRAY));
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ESPIRITO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        jogador.getFoodData().setFoodLevel(20);
        jogador.getFoodData().setSaturation(20.0f);

        if (jogador.getPersistentData().getBoolean(TAG_ESPECTRAL)) {
            long agora = jogador.serverLevel().getGameTime();
            long fim = jogador.getPersistentData().getLong(TAG_ESPECTRAL_FIM);
            if (agora >= fim) {
                desativar(jogador);
                jogador.sendSystemMessage(Component.literal("Forma espectral expirou.")
                        .withStyle(ChatFormatting.GRAY));
                return;
            }
        }

        boolean ehNoite = !jogador.serverLevel().isDay();
        if (jogador.tickCount % 100 != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0, true, false), null);

        if (ehNoite) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 400, 1, true, false), null);
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ESPIRITO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
            evento.setCanceled(true);
        }
    }
}
