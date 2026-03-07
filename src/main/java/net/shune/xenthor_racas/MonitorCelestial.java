package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorCelestial {

    private static final int DURACAO_EFEITO  = 400;
    private static final int INTERVALO_TICK  = 100;
    private static final String NOME_EQUIPE  = "xenthor_celestial_glowing";

    public static void aplicarEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe == null) {
            equipe = placar.addPlayerTeam(NOME_EQUIPE);
            equipe.setColor(ChatFormatting.GOLD);
            equipe.setNameTagVisibility(Team.Visibility.ALWAYS);
        }
        placar.addPlayerToTeam(jogador.getScoreboardName(), equipe);
    }

    public static void removerEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe != null) {
            placar.removePlayerFromTeam(jogador.getScoreboardName(), equipe);
        }
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.CELESTIAL.id.equals(racaSalva)) return;

        boolean ehDia = jogador.serverLevel().isDay();

        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);

        if (ehDia) {
            jogador.forceAddEffect(new MobEffectInstance(
                    MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        } else {
            var regen = jogador.getEffect(MobEffects.REGENERATION);
            if (regen != null && regen.isAmbient())
                jogador.removeEffect(MobEffects.REGENERATION);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.CELESTIAL.id.equals(racaSalva)) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.WITHER)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
}
