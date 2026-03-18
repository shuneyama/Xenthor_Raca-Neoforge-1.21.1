package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorTritao {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_tritao_glow";
    private static final String TAG_AR_TICKS = ModPrincipal.ID_MOD + ":tritao_ar_ticks";
    private static final int LIMITE_AR_TICKS = 20 * 60 * 3;

    public static void aplicarEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe == null) {
            equipe = placar.addPlayerTeam(NOME_EQUIPE);
            equipe.setColor(ChatFormatting.AQUA);
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
        if (!Raca.TRITAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        boolean estaChovendo = jogador.serverLevel().isRaining()
                && jogador.serverLevel().canSeeSky(jogador.blockPosition());
        boolean naAgua = jogador.isInWater();
        boolean seguro = naAgua || estaChovendo;

        if (naAgua) {
            jogador.setAirSupply(jogador.getMaxAirSupply());
        }

        if (seguro) {
            jogador.getPersistentData().putInt(TAG_AR_TICKS, 0);
        } else {
            boolean temPocao = false;
            try {
                temPocao = jogador.hasEffect(net.shune.xenthor_racas.efeito.RegistroEfeitos.RESPIRACAO_TERRESTRE);
            } catch (Exception ignored) {}

            if (temPocao) {
                jogador.getPersistentData().putInt(TAG_AR_TICKS, 0);
            } else {
                int ticksForaDaAgua = jogador.getPersistentData().getInt(TAG_AR_TICKS) + 1;
                jogador.getPersistentData().putInt(TAG_AR_TICKS, ticksForaDaAgua);

                int ticksRestantes = LIMITE_AR_TICKS - ticksForaDaAgua;
                if (ticksRestantes > 0) {
                    int segundos = ticksRestantes / 20;
                    int minutos = segundos / 60;
                    int segs = segundos % 60;
                    String tempo = String.format("%d:%02d", minutos, segs);
                    ChatFormatting cor = segundos <= 30 ? ChatFormatting.RED : ChatFormatting.AQUA;
                    jogador.displayClientMessage(
                            Component.literal("Ar restante: " + tempo).withStyle(cor), true);
                } else {
                    jogador.displayClientMessage(
                            Component.literal("Você está sufocando!").withStyle(ChatFormatting.DARK_RED), true);
                    if (ticksForaDaAgua % 20 == 0) {
                        jogador.hurt(jogador.damageSources().drown(), 2.0f);
                    }
                }
            }
        }

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);

        if (naAgua) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, DURACAO_EFEITO, 2, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, DURACAO_EFEITO, 0, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 2, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        }

        if (estaChovendo) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 2, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        }

        String idBioma = jogador.serverLevel().getBiome(jogador.blockPosition())
                .unwrapKey().map(k -> k.location().toString()).orElse("");
        boolean ehDeserto = idBioma.contains("desert") || idBioma.contains("savanna") || idBioma.contains("badlands");
        boolean ehNether = jogador.serverLevel().dimension().equals(Level.NETHER);

        if (ehDeserto || ehNether) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DURACAO_EFEITO, 1, true, false), null);
        }
    }
}