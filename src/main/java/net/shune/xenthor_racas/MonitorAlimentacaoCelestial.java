package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAlimentacaoCelestial {

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CELESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack item = evento.getItemStack();
        if (ehProibido(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Celestiais nao podem consumir carne podre ou magia negra!")
                    .withStyle(ChatFormatting.GOLD));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.CELESTIAL.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        if (ehProibido(evento.getItem())) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehProibido(ItemStack item) {
        if (item.isEmpty()) return false;
        // Carne podre
        if (item.getItem() == Items.ROTTEN_FLESH) return true;
        // Pocoes com efeitos de magia negra (sangue, ender, eldritch via Irons)
        if (item.getItem() == Items.POTION
                || item.getItem() == Items.SPLASH_POTION
                || item.getItem() == Items.LINGERING_POTION) {
            var conteudo = item.get(DataComponents.POTION_CONTENTS);
            if (conteudo != null) {
                for (var efeito : conteudo.getAllEffects()) {
                    if (efeito.getEffect().is(MobEffects.WITHER)
                            || efeito.getEffect().is(MobEffects.POISON)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
