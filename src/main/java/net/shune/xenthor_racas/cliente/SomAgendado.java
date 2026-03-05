package net.shune.xenthor_racas.cliente;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.shune.xenthor_racas.ModPrincipal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class SomAgendado {

    private record EntradaSom(SoundEvent som, float volume, float pitch, int tickAlvo) {}

    private static final List<EntradaSom> fila = new ArrayList<>();
    private static int tickAtual = 0;

    public static void agendar(Minecraft mc, SoundEvent som, float volume, float pitch, int ticksDelay) {
        fila.add(new EntradaSom(som, volume, pitch, tickAtual + ticksDelay));
    }

    @SubscribeEvent
    public static void aoTickCliente(ClientTickEvent.Post evento) {
        Minecraft mc = Minecraft.getInstance();
        tickAtual++;

        if (mc.level == null || mc.player == null) {
            fila.clear();
            return;
        }

        Iterator<EntradaSom> it = fila.iterator();
        while (it.hasNext()) {
            EntradaSom entrada = it.next();
            if (tickAtual >= entrada.tickAlvo()) {
                mc.level.playLocalSound(
                    mc.player.getX(), mc.player.getY(), mc.player.getZ(),
                    entrada.som(), SoundSource.PLAYERS,
                    entrada.volume(), entrada.pitch(), false);
                it.remove();
            }
        }
    }
}
