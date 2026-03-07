package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorBestial {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        if (!Raca.BESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DIG_SPEED, DURACAO_EFEITO, 1, true, false), null);
    }

    @SubscribeEvent
    public static void aoTerminarDeUsar(LivingEntityUseItemEvent.Finish evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.BESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (!ehComida(item)) return;

        if (ehCarne(item)) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true), null);
        } else {
            jogador.getFoodData().setSaturation(Math.max(0, jogador.getFoodData().getSaturationLevel() - 3.0f));
        }
    }

    private static boolean ehCarne(ItemStack item) {
        return item.is(Items.COOKED_BEEF) || item.is(Items.BEEF)
                || item.is(Items.COOKED_PORKCHOP) || item.is(Items.PORKCHOP)
                || item.is(Items.COOKED_CHICKEN) || item.is(Items.CHICKEN)
                || item.is(Items.COOKED_MUTTON) || item.is(Items.MUTTON)
                || item.is(Items.COOKED_RABBIT) || item.is(Items.RABBIT)
                || item.is(Items.ROTTEN_FLESH);
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }
}