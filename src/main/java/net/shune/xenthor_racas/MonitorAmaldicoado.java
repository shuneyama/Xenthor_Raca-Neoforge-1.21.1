package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAmaldicoado {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final int INTERVALO_BUFF = 20 * 60 * 10;
    private static final int DURACAO_BUFF = 20 * 60 * 10;
    private static final String NOME_EQUIPE = "xenthor_amaldicoado_glow";
    private static final String TAG_PROXIMO_BUFF = ModPrincipal.ID_MOD + ":amaldicoado_prox_buff";
    private static final Random RNG = new Random();

    @SuppressWarnings("unchecked")
    private static final List<Holder<MobEffect>> BUFFS_POSSIVEIS = List.of(
            MobEffects.DAMAGE_BOOST,
            MobEffects.MOVEMENT_SPEED,
            MobEffects.DIG_SPEED,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.REGENERATION,
            MobEffects.JUMP,
            MobEffects.FIRE_RESISTANCE,
            MobEffects.NIGHT_VISION,
            MobEffects.ABSORPTION
    );

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
        if (!Raca.AMALDICOADO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (jogador.isUnderWater()) {
            jogador.setAirSupply(jogador.getMaxAirSupply());
        }

        if (jogador.tickCount % INTERVALO_TICK == 0) {
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURACAO_EFEITO, 2, true, false), null);
            jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        }

        long agora = jogador.serverLevel().getGameTime();
        long proximo = jogador.getPersistentData().getLong(TAG_PROXIMO_BUFF);

        if (proximo == 0 || agora >= proximo) {
            jogador.getPersistentData().putLong(TAG_PROXIMO_BUFF, agora + INTERVALO_BUFF);

            Holder<MobEffect> buff = BUFFS_POSSIVEIS.get(RNG.nextInt(BUFFS_POSSIVEIS.size()));
            int nivel = RNG.nextInt(3);
            jogador.forceAddEffect(new MobEffectInstance(buff, DURACAO_BUFF, nivel, false, true), null);

            jogador.sendSystemMessage(Component.literal("A maldição te concedeu um poder temporário...")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @SubscribeEvent
    public static void aoAplicarEfeito(MobEffectEvent.Applicable evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.AMALDICOADO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (evento.getEffectInstance().getEffect().is(MobEffects.HEAL)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.REGENERATION)) {
            if (!evento.getEffectInstance().isAmbient()) {
                evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
            }
        }

        if (evento.getEffectInstance().getEffect().is(MobEffects.POISON)) {
            evento.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.AMALDICOADO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (item.is(Items.GOLDEN_APPLE) || item.is(Items.ENCHANTED_GOLDEN_APPLE) || item.is(Items.GOLDEN_CARROT)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Amaldiçoados não podem consumir alimentos dourados!")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.AMALDICOADO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItem();
        if (item.is(Items.GOLDEN_APPLE) || item.is(Items.ENCHANTED_GOLDEN_APPLE) || item.is(Items.GOLDEN_CARROT)) {
            evento.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.AMALDICOADO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

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