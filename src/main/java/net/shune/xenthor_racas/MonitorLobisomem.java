package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorLobisomem {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String TAG_REGEN_BLOCK = ModPrincipal.ID_MOD + ":lobisomem_regen_block";

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        if (!Raca.LOBISOMEM.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        boolean ehNoite = !jogador.serverLevel().isDay();
        long bloqueioFim = jogador.getPersistentData().getLong(TAG_REGEN_BLOCK);
        boolean regenBloqueada = jogador.serverLevel().getGameTime() < bloqueioFim;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 1, true, false), null);

        if (ehNoite) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 2, true, false), null);
        } else {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);
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
        if (!Raca.LOBISOMEM.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        var atacante = evento.getSource().getDirectEntity();
        if (atacante instanceof ServerPlayer atacantePlayer) {
            ItemStack arma = atacantePlayer.getMainHandItem();
            String itemId = arma.getItem().builtInRegistryHolder().key().location().toString();
            if (itemId.contains("silver")) {
                jogador.removeEffect(MobEffects.REGENERATION);
                jogador.getPersistentData().putLong(TAG_REGEN_BLOCK,
                        jogador.serverLevel().getGameTime() + 20 * 5);
            }
        }
    }

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.LOBISOMEM.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (ehComida(item) && !ehCarne(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Lobisomens so podem comer carne!")
                    .withStyle(ChatFormatting.RED));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.LOBISOMEM.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (ehComida(evento.getItem()) && !ehCarne(evento.getItem())) {
            evento.setCanceled(true);
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