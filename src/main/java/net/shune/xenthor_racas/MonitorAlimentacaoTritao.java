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
public class MonitorAlimentacaoTritao {

    private static final Set<Item> ALIMENTOS_PERMITIDOS = Set.of(
            Items.COD, Items.COOKED_COD,
            Items.SALMON, Items.COOKED_SALMON,
            Items.TROPICAL_FISH, Items.PUFFERFISH,
            Items.DRIED_KELP,
            Items.SEAGRASS,
            Items.KELP
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.TRITAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (ehComida(item) && !ehPermitido(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Tritões só podem comer peixes e alimentos do mar!")
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.TRITAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (ehComida(evento.getItem()) && !ehPermitido(evento.getItem())) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehPermitido(ItemStack item) {
        if (ALIMENTOS_PERMITIDOS.contains(item.getItem())) return true;
        String id = item.getItem().builtInRegistryHolder().key().location().toString();
        return id.startsWith("sushigocrafting:") || id.equals("create:builders_tea");
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }
}