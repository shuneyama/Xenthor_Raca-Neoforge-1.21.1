package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorDanoElfo {

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NATURAL.id.equals(racaSalva)) return;

        DamageSource fonte = evento.getSource();

        if (fonte.is(DamageTypeTags.IS_FIRE)) {
            evento.setAmount(evento.getAmount() * 2.0f);
        }
    }
}