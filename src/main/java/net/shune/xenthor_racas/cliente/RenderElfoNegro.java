package net.shune.xenthor_racas.cliente;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.shune.xenthor_racas.ModPrincipal;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class RenderElfoNegro {

    private static final Set<UUID> jogadoresEmFormaNegra = new HashSet<>();
    private static final Random rng = new Random();

    public static void marcarFormaNegra(UUID uuid, boolean ativa) {
        if (ativa) jogadoresEmFormaNegra.add(uuid);
        else jogadoresEmFormaNegra.remove(uuid);
    }

    @SubscribeEvent
    public static void aoTickCliente(ClientTickEvent.Post evento) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        for (Player jogador : mc.level.players()) {
            if (!jogadoresEmFormaNegra.contains(jogador.getUUID())) continue;

            double cx = jogador.getX();
            double cy = jogador.getY() + 1.1;
            double cz = jogador.getZ();

            for (int i = 0; i < 3; i++) {
                double ox = (rng.nextDouble() - 0.5) * 0.5;
                double oy = (rng.nextDouble() - 0.5) * 0.3;
                double oz = (rng.nextDouble() - 0.5) * 0.5;

                mc.level.addParticle(
                        ParticleTypes.SQUID_INK,
                        cx + ox, cy + oy, cz + oz,
                        0, 0.01, 0
                );
            }
        }
    }
}