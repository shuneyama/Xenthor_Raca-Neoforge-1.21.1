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
public class MonitorCorrompido {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_corrompido_glow";

    public static void aplicarEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe == null) {
            equipe = placar.addPlayerTeam(NOME_EQUIPE);
            equipe.setColor(ChatFormatting.BLACK);
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

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.CORROMPIDO.id.equals(racaSalva)) return;

        if (jogador.isInWater()) {
            if (!jogador.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 0, true, false), null);
            }
            if (jogador.tickCount % 10 == 0) {
                jogador.hurt(jogador.damageSources().drown(), 4.0f);
            }
        }

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        boolean ehDia = jogador.serverLevel().isDay();

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);

        if (!ehDia) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
        } else {
            var res = jogador.getEffect(MobEffects.DAMAGE_RESISTANCE);
            if (res != null && res.isAmbient())
                jogador.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CORROMPIDO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.POISON)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }
}