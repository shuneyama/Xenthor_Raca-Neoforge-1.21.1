package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorMortoVivo {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_morto_vivo_glow";
    private static final String TAG_HP_SALVO = ModPrincipal.ID_MOD + ":morto_vivo_hp";

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

    public static boolean ehMortoVivo(ServerPlayer jogador) {
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        return Raca.MORTO_VIVO.id.equals(raca) || Raca.AMALDICOADO.id.equals(raca);
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (jogador.isUnderWater()) {
            jogador.setAirSupply(jogador.getMaxAirSupply());
        }

        float hpAtual = jogador.getHealth();
        float hpSalvo = jogador.getPersistentData().contains(TAG_HP_SALVO)
                ? jogador.getPersistentData().getFloat(TAG_HP_SALVO)
                : hpAtual;

        if (hpAtual > hpSalvo) {
            jogador.setHealth(hpSalvo);
        } else {
            jogador.getPersistentData().putFloat(TAG_HP_SALVO, hpAtual);
        }

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        boolean ehNoite = !jogador.serverLevel().isDay();
        boolean temSol = jogador.serverLevel().isDay()
                && jogador.serverLevel().canSeeSky(jogador.blockPosition());

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
        if (!ehMortoVivo(jogador)) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.POISON)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.HEAL)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.ABSORPTION)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.REGENERATION)) {
            if (!evento.getEffectInstance().isAmbient()) {
                evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = false)
    public static void aoCurar(LivingHealEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!ehMortoVivo(jogador)) return;
        evento.setCanceled(true);
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!ehMortoVivo(jogador)) return;

        if (evento.getSource().is(DamageTypeTags.IS_FIRE)) {
            evento.setAmount(evento.getAmount() * 2.0f);
        }

        var atacante = evento.getSource().getDirectEntity();
        if (atacante instanceof LivingEntity atacanteVivo) {
            ItemStack arma = atacanteVivo.getMainHandItem();
            if (!arma.isEmpty()) {
                var smiteKey = jogador.serverLevel().registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolder(ResourceLocation.withDefaultNamespace("smite"));
                if (smiteKey.isPresent()) {
                    int nivelSmite = EnchantmentHelper.getItemEnchantmentLevel(smiteKey.get(), arma);
                    if (nivelSmite > 0) {
                        evento.setAmount(evento.getAmount() + nivelSmite * 2.5f);
                    }
                }
            }
        }
    }
}