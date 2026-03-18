package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class PoderInvocacao {

    private static final String TAG_COOLDOWN = ModPrincipal.ID_MOD + ":invocacao_cooldown";
    private static final String TAG_INVOCACAO = ModPrincipal.ID_MOD + ":esqueletos_invocados";
    private static final int COOLDOWN_TICKS = 20 * 60 * 5;
    private static final int QUANTIDADE = 6;
    private static final double DIST_TELEPORTE = 20.0;

    private static final ConcurrentHashMap<UUID, List<Integer>> ESQUELETOS = new ConcurrentHashMap<>();

    public static void tentar(ServerPlayer jogador) {
        if (!Raca.MORTO_VIVO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        long agora = jogador.serverLevel().getGameTime();
        long fimCooldown = jogador.getPersistentData().getLong(TAG_COOLDOWN);

        if (agora < fimCooldown) {
            long segundos = (fimCooldown - agora) / 20;
            jogador.sendSystemMessage(Component.literal("Invocação em recarga! Aguarde " + segundos + "s.")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        jogador.getPersistentData().putLong(TAG_COOLDOWN, agora + COOLDOWN_TICKS);

        ServerLevel nivel = jogador.serverLevel();
        Vec3 pos = jogador.position();

        List<Integer> ids = new ArrayList<>();
        int invocados = 0;
        for (int i = 0; i < QUANTIDADE; i++) {
            double angulo = (2 * Math.PI / QUANTIDADE) * i;
            double x = pos.x + Math.cos(angulo) * 2.0;
            double z = pos.z + Math.sin(angulo) * 2.0;

            Skeleton esqueleto = new Skeleton(EntityType.SKELETON, nivel);
            esqueleto.setPos(x, pos.y, z);
            esqueleto.setPersistenceRequired();
            esqueleto.getPersistentData().putUUID(ModPrincipal.ID_MOD + ":dono", jogador.getUUID());
            nivel.addFreshEntity(esqueleto);

            esqueleto.targetSelector.removeAllGoals(g -> true);
            esqueleto.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(esqueleto, Player.class, 10, true, false,
                    target -> {
                        if (target instanceof ServerPlayer alvo) {
                            return !Raca.MORTO_VIVO.id.equals(alvo.getPersistentData().getString(ModPrincipal.TAG_RACA));
                        }
                        return true;
                    }));
            esqueleto.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(esqueleto, Mob.class, 10, true, false,
                    target -> !(target instanceof Skeleton)));

            ids.add(esqueleto.getId());
            invocados++;
        }

        ESQUELETOS.put(jogador.getUUID(), ids);

        jogador.sendSystemMessage(Component.literal("Você invocou " + invocados + " esqueletos!")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % 40 != 0) return;

        List<Integer> ids = ESQUELETOS.get(jogador.getUUID());
        if (ids == null || ids.isEmpty()) return;

        ServerLevel nivel = jogador.serverLevel();
        Vec3 posJogador = jogador.position();

        ids.removeIf(id -> {
            var entidade = nivel.getEntity(id);
            if (entidade == null || !entidade.isAlive()) return true;

            if (entidade.distanceToSqr(posJogador) > DIST_TELEPORTE * DIST_TELEPORTE) {
                double angulo = Math.random() * 2 * Math.PI;
                double x = posJogador.x + Math.cos(angulo) * 3.0;
                double z = posJogador.z + Math.sin(angulo) * 3.0;
                entidade.teleportTo(x, posJogador.y, z);
            }
            return false;
        });

        if (ids.isEmpty()) {
            ESQUELETOS.remove(jogador.getUUID());
        }
    }
}