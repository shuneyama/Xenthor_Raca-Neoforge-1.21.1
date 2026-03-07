package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoElfo {

    private static final Set<Item> ALIMENTOS_PERMITIDOS = Set.of(
            Items.APPLE,
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.MELON_SLICE,
            Items.SWEET_BERRIES,
            Items.GLOW_BERRIES,
            Items.CARROT,
            Items.GOLDEN_CARROT,
            Items.POTATO,
            Items.BAKED_POTATO,
            Items.BEETROOT,
            Items.BEETROOT_SOUP,
            Items.MUSHROOM_STEW,
            Items.SUSPICIOUS_STEW,
            Items.COD,
            Items.COOKED_COD,
            Items.SALMON,
            Items.COOKED_SALMON,
            Items.TROPICAL_FISH,
            Items.BREAD,
            Items.CAKE,
            Items.COOKIE,
            Items.PUMPKIN_PIE,
            Items.HONEY_BOTTLE,
            Items.CHORUS_FRUIT,
            Items.DRIED_KELP
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ELFO_NATURAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (ehComida(item) && !ALIMENTOS_PERMITIDOS.contains(item.getItem())) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(
                    Component.literal("Elfos Naturais so podem consumir alimentos naturais!")
                            .withStyle(ChatFormatting.DARK_GREEN));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ELFO_NATURAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (ehComida(item) && !ALIMENTOS_PERMITIDOS.contains(item.getItem())) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }
}