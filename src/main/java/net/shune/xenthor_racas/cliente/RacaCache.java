package net.shune.xenthor_racas.cliente;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RacaCache {

    private static final Map<UUID, String> RACAS = new ConcurrentHashMap<>();

    public static void definir(UUID uuid, String racaId) {
        if (racaId == null || racaId.isEmpty()) {
            RACAS.remove(uuid);
        } else {
            RACAS.put(uuid, racaId);
        }
    }

    public static String obter(UUID uuid) {
        return RACAS.getOrDefault(uuid, "");
    }
}