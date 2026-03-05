package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoElfoNegro {

    private static final Set<Item> ITENS_SAGRADOS_PROIBIDOS = Set.of(
            Items.GOLDEN_APPLE,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.GOLDEN_CARROT
    );

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        ItemStack item = evento.getItemStack();

        if (ITENS_SAGRADOS_PROIBIDOS.contains(item.getItem())) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(
                    Component.literal("Elfos Negros nao podem consumir itens sagrados!")
                            .withStyle(ChatFormatting.DARK_RED));
            return;
        }

        if (ehPocaoDeCura(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(
                    Component.literal("Elfos Negros nao podem usar pocoes de cura!")
                            .withStyle(ChatFormatting.DARK_RED));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        ItemStack item = evento.getItem();
        if (ITENS_SAGRADOS_PROIBIDOS.contains(item.getItem()) || ehPocaoDeCura(item)) {
            evento.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.HEAL)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static boolean ehPocaoDeCura(ItemStack item) {
        if (item.getItem() != Items.POTION
                && item.getItem() != Items.SPLASH_POTION
                && item.getItem() != Items.LINGERING_POTION) return false;

        PotionContents conteudo = item.get(DataComponents.POTION_CONTENTS);
        if (conteudo == null) return false;

        for (var efeito : conteudo.getAllEffects()) {
            if (efeito.getEffect().is(MobEffects.HEAL)
                    || efeito.getEffect().is(MobEffects.REGENERATION)) {
                return true;
            }
        }
        return false;
    }
}