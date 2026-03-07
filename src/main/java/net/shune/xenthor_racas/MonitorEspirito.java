package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class MonitorEspirito {

    private static final String NOME_EQUIPE = "xenthor_espirito_glow";

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
}