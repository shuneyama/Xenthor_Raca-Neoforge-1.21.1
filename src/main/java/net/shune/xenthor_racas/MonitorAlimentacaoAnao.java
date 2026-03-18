package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoAnao {

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (ehMagiaOuPocaoHoly(evento.getItemStack())) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Anões não podem usar magia divina!")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (ehMagiaOuPocaoHoly(evento.getItem())) {
            evento.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        var effect = evento.getEffectInstance().getEffect();
        if (effect.is(net.minecraft.world.effect.MobEffects.HEAL)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static boolean ehMagiaOuPocaoHoly(ItemStack item) {
        if (item.isEmpty()) return false;
        if (item.getItem() == net.minecraft.world.item.Items.POTION
                || item.getItem() == net.minecraft.world.item.Items.SPLASH_POTION
                || item.getItem() == net.minecraft.world.item.Items.LINGERING_POTION) {
            var conteudo = item.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
            if (conteudo != null) {
                for (var efeito : conteudo.getAllEffects()) {
                    if (efeito.getEffect().is(net.minecraft.world.effect.MobEffects.HEAL))
                        return true;
                }
            }
        }
        return false;
    }
}
