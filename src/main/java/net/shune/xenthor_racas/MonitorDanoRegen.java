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
        String tipo = evento.getSource().type().msgId();
        boolean ehMagiaHoly = tipo.contains("holy") || tipo.contains("divine") || tipo.contains("smite");
        boolean ehMagiaFogo = evento.getSource().is(DamageTypeTags.IS_FIRE) || tipo.contains("fire") || tipo.contains("blaze");
        boolean ehMagiaRaio = tipo.contains("lightning") || tipo.contains("electro") || tipo.contains("shock");
        boolean ehMagiaGelo = tipo.contains("ice") || tipo.contains("frost") || tipo.contains("freeze");

        if (Raca.CORROMPIDO.id.equals(raca)) {
            if (ehMagiaHoly) {
                evento.setAmount(evento.getAmount() * 1.10f);
            }
        }

        if (Raca.CELESTIAL.id.equals(raca)) {
            boolean ehMagiaNegra = tipo.contains("blood") || tipo.contains("ender") || tipo.contains("eldritch") 
                    || tipo.contains("void") || tipo.contains("wither");
            if (ehMagiaNegra) {
                evento.setAmount(evento.getAmount() * 1.10f);
            }
        }

        if (Raca.BESTIAL_AEREO.id.equals(raca)) {
            if (evento.getSource().is(DamageTypeTags.IS_FALL)) {
                evento.setCanceled(true);
            }
            if (ehMagiaRaio) {
                evento.setAmount(evento.getAmount() * 1.10f);
            }
        }

        if (Raca.ANDROID.id.equals(raca)) {
            if (ehMagiaRaio) {
                evento.setAmount(evento.getAmount() * 1.10f);
            }
            if (ehMagiaGelo) {
                evento.setAmount(evento.getAmount() * 1.05f);
            }
        }

        if (Raca.MORTO_VIVO.id.equals(raca)) {
            if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
                evento.setAmount(evento.getAmount() * 1.20f);
            }
            if (ehMagiaHoly) {
                evento.setAmount(evento.getAmount() * 1.20f);
            }
        }

        if (Raca.VAMPIRO.id.equals(raca) || Raca.DAMPIRO.id.equals(raca)) {
            if (ehMagiaHoly) {
                evento.setAmount(evento.getAmount() * 1.15f);
            }
            if (ehMagiaFogo) {
                evento.setAmount(evento.getAmount() * 1.15f);
            }
        }

        if (Raca.OGRO.id.equals(raca)) {
            if (ehMagiaHoly) {
                evento.setAmount(evento.getAmount() * 1.10f);
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
