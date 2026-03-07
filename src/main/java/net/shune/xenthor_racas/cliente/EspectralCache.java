package net.shune.xenthor_racas.cliente;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EspectralCache {

    private static final Map<UUID, Boolean> ESTADOS = new ConcurrentHashMap<>();

    public static void definir(UUID uuid, boolean ativo) {
        if (ativo) {
            ESTADOS.put(uuid, true);
        } else {
            ESTADOS.remove(uuid);
        }
    }

    public static boolean estaEspectral(UUID uuid) {
        return ESTADOS.getOrDefault(uuid, false);
    }
}