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
public class MonitorAlimentacaoMortoVivo {

    private static final Set<Item> ALIMENTOS_PERMITIDOS = Set.of(
            Items.ROTTEN_FLESH,
            Items.SPIDER_EYE,
            Items.POISONOUS_POTATO,
            Items.FERMENTED_SPIDER_EYE
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();

        if (item.is(Items.BONE)) {
            item.shrink(1);
            jogador.getFoodData().eat(3, 2.0f);
            return;
        }

        if (ehPocaoDeCuraOuRegen(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Mortos-Vivos não podem beber poções de cura!")
                    .withStyle(ChatFormatting.DARK_GRAY));
            return;
        }

        if (ehComida(item) && !ehPermitido(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Mortos-Vivos só podem comer alimentos podres ou ossos!")
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();

        if (ehPocaoDeCuraOuRegen(item)) {
            evento.setCanceled(true);
            return;
        }

        if (ehComida(item) && !ehPermitido(item)) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehPermitido(ItemStack item) {
        if (ALIMENTOS_PERMITIDOS.contains(item.getItem())) return true;
        String id = item.getItem().builtInRegistryHolder().key().location().toString();
        return id.equals("create:builders_tea");
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }

    private static boolean ehPocaoDeCuraOuRegen(ItemStack item) {
        if (!item.is(Items.POTION) && !item.is(Items.SPLASH_POTION) && !item.is(Items.LINGERING_POTION))
            return false;

        var conteudo = item.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
        if (conteudo == null) return false;

        for (var efeito : conteudo.getAllEffects()) {
            if (efeito.getEffect().is(net.minecraft.world.effect.MobEffects.HEAL)
                    || efeito.getEffect().is(net.minecraft.world.effect.MobEffects.REGENERATION)) {
                return true;
            }
        }
        return false;
    }
}