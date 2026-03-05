package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class FormaNegra {

    public static final String TAG_FORMA_ATIVA  = ModPrincipal.ID_MOD + ":forma_negra_ativa";
    public static final String TAG_COOLDOWN_FIM = ModPrincipal.ID_MOD + ":forma_negra_cooldown";

    private static final long DURACAO_TICKS  = 20 * 60 * 2;
    private static final long COOLDOWN_TICKS = 20 * 60 * 15;

    public static boolean estaAtiva(ServerPlayer jogador) {
        return jogador.getPersistentData().getBoolean(TAG_FORMA_ATIVA);
    }

    public static boolean temCooldown(ServerPlayer jogador) {
        long fimCooldown = jogador.getPersistentData().getLong(TAG_COOLDOWN_FIM);
        return jogador.serverLevel().getGameTime() < fimCooldown;
    }

    public static long ticksRestantesCooldown(ServerPlayer jogador) {
        long fimCooldown = jogador.getPersistentData().getLong(TAG_COOLDOWN_FIM);
        return Math.max(0, fimCooldown - jogador.serverLevel().getGameTime());
    }

    public static void tentar(ServerPlayer jogador) {
        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ELFO_NEGRO.id.equals(racaSalva)) return;

        if (estaAtiva(jogador)) {
            desativar(jogador, false);
            return;
        }

        boolean temSol = jogador.serverLevel().isDay()
                && jogador.serverLevel().canSeeSky(jogador.blockPosition());

        if (temSol) {
            jogador.sendSystemMessage(
                    Component.literal("Voce nao pode usar a Forma Negra sob a luz do sol!")
                            .withStyle(ChatFormatting.DARK_RED));
            return;
        }

        if (temCooldown(jogador)) {
            long segundos = ticksRestantesCooldown(jogador) / 20;
            jogador.sendSystemMessage(
                    Component.literal("Forma Negra em cooldown! Aguarde " + segundos + "s.")
                            .withStyle(ChatFormatting.GRAY));
            return;
        }

        ativar(jogador);
    }

    public static void ativar(ServerPlayer jogador) {
        jogador.getPersistentData().putBoolean(TAG_FORMA_ATIVA, true);
        jogador.getPersistentData().putLong(
                ModPrincipal.ID_MOD + ":forma_negra_fim",
                jogador.serverLevel().getGameTime() + DURACAO_TICKS);

        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.INVISIBILITY, (int) DURACAO_TICKS, 0, false, false), null);

        jogador.sendSystemMessage(
                Component.literal("Forma Negra ativada! Duracao: 2 minutos.")
                        .withStyle(ChatFormatting.DARK_PURPLE));

        net.shune.xenthor_racas.rede.RedeXenthor.enviarFormaNegra(jogador, true);
    }

    public static void desativar(ServerPlayer jogador, boolean forcado) {
        jogador.getPersistentData().putBoolean(TAG_FORMA_ATIVA, false);
        jogador.getPersistentData().putLong(TAG_COOLDOWN_FIM,
                jogador.serverLevel().getGameTime() + COOLDOWN_TICKS);
        jogador.getPersistentData().remove(ModPrincipal.ID_MOD + ":forma_negra_fim");

        jogador.removeEffect(MobEffects.INVISIBILITY);

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.WEAKNESS,  20 * 180, 1, false, true), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * 180, 1, false, true), null);

        String motivo = forcado ? "Forma Negra desativada pelo sol!" : "Forma Negra desativada.";
        jogador.sendSystemMessage(
                Component.literal(motivo + " Fraqueza II e Cegueira II por 3 minutos.")
                        .withStyle(ChatFormatting.GRAY));

        net.shune.xenthor_racas.rede.RedeXenthor.enviarFormaNegra(jogador, false);
    }

    @SubscribeEvent
    public static void aoTickJogador(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;

        long fimForma = jogador.getPersistentData().getLong(ModPrincipal.ID_MOD + ":forma_negra_fim");
        if (jogador.serverLevel().getGameTime() >= fimForma) {
            desativar(jogador, false);
        }
    }

    @SubscribeEvent
    public static void aoDanoRecebido(LivingIncomingDamageEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;

        DamageSource fonte = evento.getSource();
        boolean ehMagia = fonte.getDirectEntity() != null
                && fonte.getDirectEntity().getType().toString().contains("spell");

        if (!ehMagia && !fonte.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            evento.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void aoAtacarEntidade(AttackEntityEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;
        evento.setCanceled(true);
        jogador.sendSystemMessage(
                Component.literal("Voce nao pode atacar na Forma Negra!")
                        .withStyle(ChatFormatting.DARK_RED));
    }

    @SubscribeEvent
    public static void aoInteragirComBloco(PlayerInteractEvent.RightClickBlock evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;
        evento.setCanceled(true);
    }

    @SubscribeEvent
    public static void aoInteragirComEntidade(PlayerInteractEvent.EntityInteract evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;
        evento.setCanceled(true);
    }

    @SubscribeEvent
    public static void aoUsarItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtiva(jogador)) return;
        evento.setCanceled(true);
    }
}