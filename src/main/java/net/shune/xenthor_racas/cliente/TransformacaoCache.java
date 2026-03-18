package net.shune.xenthor_racas.cliente;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.ambient.Bat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransformacaoCache {

    private static final Map<UUID, String> FORMAS = new ConcurrentHashMap<>();
    private static final Map<UUID, LivingEntity> ENTIDADES = new ConcurrentHashMap<>();

    public static void definir(UUID uuid, String forma) {
        String anterior = FORMAS.getOrDefault(uuid, "");
        if (forma == null || forma.isEmpty()) {
            FORMAS.remove(uuid);
            ENTIDADES.remove(uuid);
        } else {
            FORMAS.put(uuid, forma);
            if (!forma.equals(anterior)) {
                ENTIDADES.remove(uuid);
            }
        }
    }

    public static String obterForma(UUID uuid) {
        return FORMAS.getOrDefault(uuid, "");
    }

    public static boolean estaTransformado(UUID uuid) {
        String forma = FORMAS.get(uuid);
        return forma != null && !forma.isEmpty();
    }

    public static LivingEntity obterEntidade(UUID uuid) {
        String forma = obterForma(uuid);
        if (forma.isEmpty()) return null;

        LivingEntity entidade = ENTIDADES.get(uuid);
        if (entidade != null) return entidade;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;

        entidade = switch (forma) {
            case "morcego" -> new Bat(EntityType.BAT, mc.level);
            case "lobo" -> new Wolf(EntityType.WOLF, mc.level);
            case "peixe" -> new Cod(EntityType.COD, mc.level);
            case "raposa" -> criarRaposaBranca(mc);
            default -> null;
        };

        if (entidade != null) {
            ENTIDADES.put(uuid, entidade);
        }
        return entidade;
    }

    private static Fox criarRaposaBranca(Minecraft mc) {
        Fox raposa = new Fox(EntityType.FOX, mc.level);
        raposa.setVariant(Fox.Type.SNOW);
        return raposa;
    }
}
