package net.shune.xenthor_racas;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorDanoRegen {

    @SubscribeEvent
    public static void aoTerminarDeUsar(LivingEntityUseItemEvent.Finish evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.DRAGONIC.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (ehPocaoDeFogo(item)) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 10, 2, false, true), null);
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);

        if (Raca.CORROMPIDO.id.equals(raca)) {
            String tipo = evento.getSource().type().msgId();
            if (tipo.contains("holy") || tipo.contains("divine")) {
                evento.setAmount(evento.getAmount() * 1.10f);
            }
        }

        if (Raca.BESTIAL_AEREO.id.equals(raca)) {
            if (evento.getSource().is(DamageTypeTags.IS_FALL)) {
                evento.setCanceled(true);
            }
        }

        if (Raca.MORTO_VIVO.id.equals(raca)) {
            if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
                evento.setAmount(evento.getAmount() * 1.20f);
            }
            String tipo = evento.getSource().type().msgId();
            if (tipo.contains("holy") || tipo.contains("divine")) {
                evento.setAmount(evento.getAmount() * 1.20f);
            }
        }
    }

    private static boolean ehPocaoDeFogo(ItemStack item) {
        if (item.getItem() != Items.POTION
                && item.getItem() != Items.SPLASH_POTION
                && item.getItem() != Items.LINGERING_POTION) return false;

        var conteudo = item.get(DataComponents.POTION_CONTENTS);
        if (conteudo == null) return false;

        for (var efeito : conteudo.getAllEffects()) {
            if (efeito.getEffect().is(MobEffects.FIRE_RESISTANCE)) {
                return true;
            }
        }
        return false;
    }
}