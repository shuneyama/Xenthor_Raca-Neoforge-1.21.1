package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoCorrompido {

    private static final Set<Item> ALIMENTOS_PERMITIDOS = Set.of(
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.POISONOUS_POTATO,
            Items.FERMENTED_SPIDER_EYE
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CORROMPIDO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (ehComida(item) && !ALIMENTOS_PERMITIDOS.contains(item.getItem())) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Corrompidos só podem comer alimentos podres!")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CORROMPIDO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (ehComida(item) && !ALIMENTOS_PERMITIDOS.contains(item.getItem())) {
            evento.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void aoTerminarDeUsar(LivingEntityUseItemEvent.Finish evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CORROMPIDO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        
        if (item.is(Items.ROTTEN_FLESH)) {
            jogador.removeEffect(MobEffects.HUNGER);
            
            jogador.getActiveEffects().removeIf(effect -> {
                String id = effect.getEffect().unwrapKey()
                        .map(k -> k.location().toString()).orElse("");
                return id.contains("rotten") || id.contains("sink");
            });
            
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0, false, true), null);
        }
        
        if (item.is(Items.SPIDER_EYE) || item.is(Items.POISONOUS_POTATO)) {
            jogador.removeEffect(MobEffects.POISON);
            jogador.removeEffect(MobEffects.HUNGER);
            jogador.removeEffect(MobEffects.CONFUSION);
        }
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }
}
