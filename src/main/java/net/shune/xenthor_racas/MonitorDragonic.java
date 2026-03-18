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
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorDragonic {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_dragonic_glow";
    private static final String TAG_ULTIMA_FOME = ModPrincipal.ID_MOD + ":dragonic_ultima_fome";
    private static final String TAG_ULTIMA_SAT = ModPrincipal.ID_MOD + ":dragonic_ultima_sat";

    public static void aplicarEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe == null) {
            equipe = placar.addPlayerTeam(NOME_EQUIPE);
            equipe.setColor(ChatFormatting.DARK_PURPLE);
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
        if (!Raca.DRAGONIC.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        int fomeAtual = jogador.getFoodData().getFoodLevel();
        float satAtual = jogador.getFoodData().getSaturationLevel();

        int fomeAnterior = jogador.getPersistentData().getInt(TAG_ULTIMA_FOME);
        float satAnterior = jogador.getPersistentData().getFloat(TAG_ULTIMA_SAT);

        if (fomeAnterior > 0 || satAnterior > 0) {
            if (fomeAtual < fomeAnterior) {
                int diferenca = fomeAnterior - fomeAtual;
                int recuperar = (int) Math.ceil(diferenca * 0.05);
                if (recuperar > 0) {
                    jogador.getFoodData().setFoodLevel(Math.min(20, fomeAtual + recuperar));
                }
            }
            if (satAtual < satAnterior) {
                float diferenca = satAnterior - satAtual;
                float recuperar = diferenca * 0.05f;
                if (recuperar > 0.001f) {
                    jogador.getFoodData().setSaturation(Math.min(20f, satAtual + recuperar));
                }
            }
        }

        jogador.getPersistentData().putInt(TAG_ULTIMA_FOME, jogador.getFoodData().getFoodLevel());
        jogador.getPersistentData().putFloat(TAG_ULTIMA_SAT, jogador.getFoodData().getSaturationLevel());

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 2, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, DURACAO_EFEITO, 0, true, false), null);
    }
}
