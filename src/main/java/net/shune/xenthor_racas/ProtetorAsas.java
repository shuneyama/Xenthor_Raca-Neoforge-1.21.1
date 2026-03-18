package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.shune.xenthor_racas.voo.AsaCelestial;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class ProtetorAsas {

    private static final Set<String> RACAS_COM_ASA = Set.of(
            Raca.CELESTIAL.id,
            Raca.BESTIAL_AEREO.id,
            Raca.FADA.id,
            Raca.DRAGONIC.id,
            Raca.VAMPIRO.id,
            Raca.DAMPIRO.id
    );

    @SubscribeEvent
    public static void aoReceberDano(LivingDamageEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        protegerAsa(jogador);
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % 20 != 0) return;
        protegerAsa(jogador);
    }

    private static void protegerAsa(ServerPlayer jogador) {
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!RACAS_COM_ASA.contains(raca)) return;

        ItemStack peito = jogador.getItemBySlot(EquipmentSlot.CHEST);
        if (peito.isEmpty()) return;

        if (peito.getItem() instanceof AsaCelestial) {
            peito.setDamageValue(0);
            return;
        }

        String itemId = peito.getItem().builtInRegistryHolder().key().location().toString();
        if (itemId.contains("asa_") || itemId.contains("wing") || itemId.contains("elytra") || itemId.contains(ModPrincipal.ID_MOD)) {
            peito.setDamageValue(0);
        }
    }
}