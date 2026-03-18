package net.shune.xenthor_racas;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorBiomaElfo {

    private static final int DURACAO_EFEITO = 300;
    private static final int INTERVALO_TICK = 100;

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NATURAL.id.equals(racaSalva)) return;

        var nivel = jogador.serverLevel();
        BlockPos pos = jogador.blockPosition();
        var chaveBioma = nivel.getBiome(pos).unwrapKey().orElse(null);

        if (chaveBioma == null) return;
        String idBioma = chaveBioma.location().toString();

        if (ehBiomaFloresta(idBioma)) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
            jogador.removeEffect(MobEffects.WEAKNESS);
        } else if (ehBiomaDeserto(idBioma) || ehNether(nivel)) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.WEAKNESS, DURACAO_EFEITO, 0, true, false), null);
            jogador.removeEffect(MobEffects.REGENERATION);
            jogador.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        } else {
            removerEfeitoAmbient(jogador, MobEffects.REGENERATION);
            removerEfeitoAmbient(jogador, MobEffects.DAMAGE_RESISTANCE);
            removerEfeitoAmbient(jogador, MobEffects.WEAKNESS);
        }
    }

    private static boolean ehBiomaFloresta(String id) {
        return id.contains("forest") || id.contains("taiga") || id.contains("jungle")
                || id.contains("grove") || id.contains("birch") || id.contains("plains") || id.contains("cherry") || id.contains("mangrove");
    }

    private static boolean ehBiomaDeserto(String id) {
        return id.contains("desert") || id.contains("savanna") || id.contains("badlands")
                || id.contains("mesa");
    }

    private static boolean ehNether(net.minecraft.server.level.ServerLevel nivel) {
        return nivel.dimension().equals(net.minecraft.world.level.Level.NETHER);
    }

    private static void removerEfeitoAmbient(ServerPlayer jogador,
                                             net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> efeito) {
        var existente = jogador.getEffect(efeito);
        if (existente != null && existente.isAmbient()) {
            jogador.removeEffect(efeito);
        }
    }
}