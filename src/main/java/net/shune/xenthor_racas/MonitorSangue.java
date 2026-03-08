package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorSangue {

    private static final ResourceLocation BOLSA_SANGUE =
            ResourceLocation.fromNamespaceAndPath("xenthormod", "bolsa_de_sangue");
    private static final ResourceLocation BOLSA_SANGUE_PROCESSADA =
            ResourceLocation.fromNamespaceAndPath("xenthormod", "bolsa_de_sangue_processada");

    @SubscribeEvent
    public static void aoInteragirComEntidade(PlayerInteractEvent.EntityInteract evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.VAMPIRO.id.equals(raca) && !Raca.DAMPIRO.id.equals(raca)) return;

        if (!(evento.getTarget() instanceof LivingEntity alvo)) return;

        ItemStack maoSecundaria = jogador.getOffhandItem();
        boolean temFrasco = maoSecundaria.is(Items.GLASS_BOTTLE);

        if (jogador.isShiftKeyDown() && temFrasco) {
            maoSecundaria.shrink(1);
            jogador.getInventory().placeItemBackInInventory(new ItemStack(Items.POTION));
            jogador.serverLevel().playSound(null, jogador.getX(), jogador.getY(), jogador.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 0.8f);
            jogador.sendSystemMessage(Component.literal("Voce coletou sangue em um frasco!")
                    .withStyle(ChatFormatting.DARK_RED));
            alvo.hurt(jogador.damageSources().playerAttack(jogador), 2.0f);
            return;
        }

        int fome;
        float saturacao;

        if (alvo instanceof Player) {
            fome = 8;
            saturacao = 10.0f;
        } else if (alvo instanceof Monster) {
            fome = 3;
            saturacao = 2.0f;
        } else {
            fome = 5;
            saturacao = 5.0f;
        }

        jogador.getFoodData().eat(fome, saturacao);
        alvo.hurt(jogador.damageSources().playerAttack(jogador), 2.0f);
        jogador.serverLevel().playSound(null, jogador.getX(), jogador.getY(), jogador.getZ(),
                SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 0.8f);
    }

    @SubscribeEvent
    public static void aoClicarComItem(PlayerInteractEvent.RightClickItem evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.VAMPIRO.id.equals(raca) && !Raca.DAMPIRO.id.equals(raca)) return;

        ItemStack item = evento.getItemStack();
        if (ehComida(item) && !ehBolsaDeSangue(item)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Vampiros so podem beber sangue!")
                    .withStyle(ChatFormatting.DARK_RED));
        }
    }

    @SubscribeEvent
    public static void aoIniciarUso(LivingEntityUseItemEvent.Start evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        String raca = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.VAMPIRO.id.equals(raca) && !Raca.DAMPIRO.id.equals(raca)) return;

        if (ehComida(evento.getItem()) && !ehBolsaDeSangue(evento.getItem())) {
            evento.setCanceled(true);
        }
    }

    private static boolean ehComida(ItemStack item) {
        return item.getItem().getFoodProperties(item, null) != null;
    }

    private static boolean ehBolsaDeSangue(ItemStack item) {
        ResourceLocation id = item.getItemHolder().unwrapKey()
                .map(k -> k.location())
                .orElse(null);
        if (id == null) return false;
        return id.equals(BOLSA_SANGUE) || id.equals(BOLSA_SANGUE_PROCESSADA);
    }
}