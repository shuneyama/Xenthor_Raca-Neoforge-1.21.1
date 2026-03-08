package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorMortoVivo {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_morto_vivo_glow";

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
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        boolean ehNoite = !jogador.serverLevel().isDay();
        boolean temSol = jogador.serverLevel().isDay()
                && jogador.serverLevel().canSeeSky(jogador.blockPosition());

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, DURACAO_EFEITO, 0, true, false), null);

        if (ehNoite) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 1, true, false), null);
        } else {
            var regen = jogador.getEffect(MobEffects.REGENERATION);
            if (regen != null && regen.isAmbient()) jogador.removeEffect(MobEffects.REGENERATION);
            var forca = jogador.getEffect(MobEffects.DAMAGE_BOOST);
            if (forca != null && forca.isAmbient()) jogador.removeEffect(MobEffects.DAMAGE_BOOST);
        }

        if (temSol) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.WEAKNESS, DURACAO_EFEITO, 0, true, false), null);
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.POISON)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.HEAL)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            jogador.hurt(jogador.damageSources().magic(), 6.0f);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.REGENERATION)) {
            if (!evento.getEffectInstance().isAmbient()) {
                evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
                jogador.hurt(jogador.damageSources().magic(), 4.0f);
            }
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
            evento.setAmount(evento.getAmount() * 2.0f);
        }
    }
}