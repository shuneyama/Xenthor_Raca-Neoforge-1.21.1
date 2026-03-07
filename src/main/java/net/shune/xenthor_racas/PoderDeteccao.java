package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class PoderDeteccao {

    private static final String TAG_DETECCAO_ATIVA = ModPrincipal.ID_MOD + ":deteccao_ativa";
    private static final String TAG_DETECCAO_FIM = ModPrincipal.ID_MOD + ":deteccao_fim";
    private static final String TAG_DETECCAO_COOLDOWN = ModPrincipal.ID_MOD + ":deteccao_cooldown";

    private static final double RAIO = 50.0;

    public static void tentar(ServerPlayer jogador) {
        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        if (raca == null) return;
        if (raca != Raca.BESTIAL && raca != Raca.BESTIAL_AEREO && raca != Raca.ANDROID
                && raca != Raca.VAMPIRO && raca != Raca.DAMPIRO && raca != Raca.LOBISOMEM) return;

        long agora = jogador.serverLevel().getGameTime();

        if (jogador.getPersistentData().getBoolean(TAG_DETECCAO_ATIVA)) {
            jogador.getPersistentData().putBoolean(TAG_DETECCAO_ATIVA, false);
            jogador.sendSystemMessage(Component.literal("Deteccao desativada.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        long fimCooldown = jogador.getPersistentData().getLong(TAG_DETECCAO_COOLDOWN);
        if (agora < fimCooldown) {
            long segundos = (fimCooldown - agora) / 20;
            jogador.sendSystemMessage(Component.literal("Deteccao em cooldown! Aguarde " + segundos + "s.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        int duracaoTicks;
        int cooldownTicks = 20 * 60;
        boolean detectaPlayers = false;

        switch (raca) {
            case BESTIAL, BESTIAL_AEREO -> duracaoTicks = 20 * 60 * 2;
            case ANDROID -> {
                duracaoTicks = 20 * 20;
                detectaPlayers = true;
            }
            case VAMPIRO, DAMPIRO, LOBISOMEM -> {
                duracaoTicks = 20 * 30;
                cooldownTicks = 20 * 60 * 2;
                detectaPlayers = true;
            }
            default -> { return; }
        }

        jogador.getPersistentData().putBoolean(TAG_DETECCAO_ATIVA, true);
        jogador.getPersistentData().putLong(TAG_DETECCAO_FIM, agora + duracaoTicks);
        jogador.getPersistentData().putLong(TAG_DETECCAO_COOLDOWN, agora + duracaoTicks + cooldownTicks);
        jogador.getPersistentData().putBoolean(ModPrincipal.ID_MOD + ":deteccao_players", detectaPlayers);

        String msg = detectaPlayers ? "Scanner ativado!" : "Instinto ativado!";
        jogador.sendSystemMessage(Component.literal(msg).withStyle(ChatFormatting.YELLOW));
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!jogador.getPersistentData().getBoolean(TAG_DETECCAO_ATIVA)) return;

        long agora = jogador.serverLevel().getGameTime();
        long fim = jogador.getPersistentData().getLong(TAG_DETECCAO_FIM);

        if (agora >= fim) {
            jogador.getPersistentData().putBoolean(TAG_DETECCAO_ATIVA, false);
            jogador.sendSystemMessage(Component.literal("Deteccao expirou.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        if (jogador.tickCount % 20 != 0) return;

        boolean detectaPlayers = jogador.getPersistentData().getBoolean(ModPrincipal.ID_MOD + ":deteccao_players");

        AABB area = jogador.getBoundingBox().inflate(RAIO);
        List<LivingEntity> entidades = jogador.serverLevel().getEntitiesOfClass(LivingEntity.class, area,
                e -> e != jogador && (e instanceof Mob || (detectaPlayers && e instanceof Player)));

        for (LivingEntity entidade : entidades) {
            entidade.addEffect(new MobEffectInstance(MobEffects.GLOWING, 25, 0, false, false));
        }
    }
}