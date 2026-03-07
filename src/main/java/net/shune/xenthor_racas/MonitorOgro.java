package net.shune.xenthor_racas;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorOgro {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final ResourceLocation MINERACAO_OGRO =
            ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "ogro.mineracao_mao");

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.OGRO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (jogador.tickCount % 5 == 0) {
            AttributeInstance mineracao = jogador.getAttribute(Attributes.BLOCK_BREAK_SPEED);
            if (mineracao != null) {
                boolean maoVazia = jogador.getMainHandItem().isEmpty();
                boolean temModificador = mineracao.getModifier(MINERACAO_OGRO) != null;

                if (maoVazia && !temModificador) {
                    mineracao.addTransientModifier(new AttributeModifier(MINERACAO_OGRO, 5.0, AttributeModifier.Operation.ADD_VALUE));
                } else if (!maoVazia && temModificador) {
                    mineracao.removeModifier(MINERACAO_OGRO);
                }
            }
        }

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.OGRO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.MOVEMENT_SLOWDOWN)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
}