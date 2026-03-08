package net.shune.xenthor_racas;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import java.time.LocalDate;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class LicencaRacas {

    private static final int _P1 = (50 * 2) + 1;
    private static final int _P2 = (1 << 6) + 36;
    private static final int _P3 = (10 * 5) - 50;

    private static final int _ANO = 2026;
    private static final int _MES = 5;
    private static final int _DIA = 31;

    private static boolean licencaValida = false;
    private static boolean isSingleplayer = false;

    public static boolean isLicencaValida() {
        return licencaValida;
    }

    public static boolean isSingleplayer() {
        return isSingleplayer;
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();

        LocalDate hoje = LocalDate.now();
        LocalDate limite = LocalDate.of(_ANO, _MES, _DIA);
        if (hoje.isAfter(limite)) {
            licencaValida = false;
            isSingleplayer = false;
            return;
        }

        if (server.isSingleplayer()) {
            licencaValida = true;
            isSingleplayer = true;
            return;
        }

        isSingleplayer = false;
        int serverPort = server.getPort();
        int portaAutorizada = (_P1 * _P2) - _P3;

        licencaValida = (serverPort == portaAutorizada);
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        licencaValida = false;
        isSingleplayer = false;
    }
}