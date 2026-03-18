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
public class MonitorAlimentacaoAndroid {

    private static final Set<Item> MINERIOS = Set.of(
            Items.COAL, Items.CHARCOAL,
            Items.RAW_IRON, Items.RAW_GOLD, Items.RAW_COPPER,
            Items.IRON_INGOT, Items.GOLD_INGOT, Items.COPPER_INGOT,
            Items.DIAMOND, Items.EMERALD, Items.LAPIS_LAZULI,
            Items.REDSTONE, Items.QUARTZ, Items.AMETHYST_SHARD,
            Items.NETHERITE_SCRAP, Items.IRON_NUGGET, Items.GOLD_NUGGET
    );

    private static final Set<Item> MINERIOS_PREMIUM = Set.of(
            Items.COAL, Items.CHARCOAL, Items.REDSTONE
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANDROID.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();

        if (MINERIOS.contains(item.getItem())) {
            item.shrink(1);
            if (MINERIOS_PREMIUM.contains(item.getItem())) {
                jogador.getFoodData().eat(5, 4.0f);
            } else {
                jogador.getFoodData().eat(3, 2.0f);
            }
            return;
        }

        if (ehPocao(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Androids não podem beber poções!")
                    .withStyle(ChatFormatting.BLUE));
            return;
        }

        if (ehComidaNaoMinerio(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Androids só podem consumir minérios!")
                    .withStyle(ChatFormatting.BLUE));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANDROID.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (ehPocao(item)) {
            evento.setCanceled(true);
            return;
        }
        if (ehComidaNaoMinerio(item)) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehComidaNaoMinerio(ItemStack item) {
        if (MINERIOS.contains(item.getItem())) return false;
        return item.getItem().getFoodProperties(item, null) != null
                && !item.is(Items.POTION)
                && !item.is(Items.SPLASH_POTION)
                && !item.is(Items.LINGERING_POTION)
                && ehComidaVanilla(item);
    }

    private static boolean ehComidaVanilla(ItemStack item) {
        return item.getItem().builtInRegistryHolder().key().location().getNamespace().equals("minecraft");
    }

    private static boolean ehPocao(ItemStack item) {
        if (!item.is(Items.POTION) && !item.is(Items.SPLASH_POTION) && !item.is(Items.LINGERING_POTION)) return false;
        var conteudo = item.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
        if (conteudo != null && conteudo.potion().isPresent()) {
            var potionKey = conteudo.potion().get().unwrapKey();
            if (potionKey.isPresent() && potionKey.get().location().getPath().equals("water")) return false;
        }
        return true;
    }
}