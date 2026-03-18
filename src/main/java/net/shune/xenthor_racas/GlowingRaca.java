package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class GlowingRaca {

    private static final String TAG_GLOW_ATIVO = ModPrincipal.ID_MOD + ":glow_personalizado_ativo";
    private static final int DURACAO_GLOW = 20 * 20;
    private static final int INTERVALO_RENOVAR = 20 * 5;

    private static final Set<String> RACAS_COM_GLOW = Set.of(
            Raca.CELESTIAL.id,
            Raca.CORROMPIDO.id,
            Raca.TRITAO.id,
            Raca.FADA.id,
            Raca.ANDROID.id,
            Raca.DRAGONIC.id,
            Raca.MORTO_VIVO.id,
            Raca.VAMPIRO.id,
            Raca.DAMPIRO.id,
            Raca.AMALDICOADO.id,
            Raca.ESPIRITO.id
    );

    public static void alternar(ServerPlayer jogador) {
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!RACAS_COM_GLOW.contains(raca)) return;

        boolean ativo = jogador.getPersistentData().getBoolean(TAG_GLOW_ATIVO);
        if (ativo) {
            jogador.getPersistentData().putBoolean(TAG_GLOW_ATIVO, false);
            jogador.removeEffect(MobEffects.GLOWING);
        } else {
            jogador.getPersistentData().putBoolean(TAG_GLOW_ATIVO, true);
            jogador.addEffect(new MobEffectInstance(MobEffects.GLOWING, DURACAO_GLOW, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!jogador.getPersistentData().getBoolean(TAG_GLOW_ATIVO)) return;

        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!RACAS_COM_GLOW.contains(raca)) {
            jogador.getPersistentData().putBoolean(TAG_GLOW_ATIVO, false);
            jogador.removeEffect(MobEffects.GLOWING);
            return;
        }

        if (jogador.tickCount % INTERVALO_RENOVAR == 0) {
            jogador.addEffect(new MobEffectInstance(MobEffects.GLOWING, DURACAO_GLOW, 0, false, false));
        }
    }
}