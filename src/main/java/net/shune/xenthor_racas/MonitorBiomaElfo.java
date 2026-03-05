package net.shune.xenthor_racas;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorBiomaElfo {

    private static final int DURACAO_EFEITO = 80;
    private static final int INTERVALO_TICK = 60;

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
            aplicarEfeito(jogador, new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false));
            aplicarEfeito(jogador, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false));
            removerEfeito(jogador, MobEffects.WEAKNESS);
        } else if (ehBiomaDeserto(idBioma) || ehNether(nivel)) {
            aplicarEfeito(jogador, new MobEffectInstance(MobEffects.WEAKNESS, DURACAO_EFEITO, 0, true, false));
            removerEfeito(jogador, MobEffects.REGENERATION);
            removerEfeito(jogador, MobEffects.DAMAGE_RESISTANCE);
        } else {
            removerEfeito(jogador, MobEffects.REGENERATION);
            removerEfeito(jogador, MobEffects.DAMAGE_RESISTANCE);
            removerEfeito(jogador, MobEffects.WEAKNESS);
        }
    }

    private static boolean ehBiomaFloresta(String id) {
        return id.contains("forest") || id.contains("taiga") || id.contains("jungle")
            || id.contains("grove") || id.contains("birch");
    }

    private static boolean ehBiomaDeserto(String id) {
        return id.contains("desert") || id.contains("savanna") || id.contains("badlands")
            || id.contains("mesa");
    }

    private static boolean ehNether(net.minecraft.server.level.ServerLevel nivel) {
        return nivel.dimension().equals(net.minecraft.world.level.Level.NETHER);
    }

    private static void aplicarEfeito(ServerPlayer jogador, MobEffectInstance efeito) {
        var existente = jogador.getEffect(efeito.getEffect());
        if (existente == null || existente.getAmplifier() < efeito.getAmplifier())
            jogador.addEffect(efeito);
    }

    private static void removerEfeito(ServerPlayer jogador,
            net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> efeito) {
        var existente = jogador.getEffect(efeito);
        if (existente != null && existente.isAmbient())
            jogador.removeEffect(efeito);
    }
}
