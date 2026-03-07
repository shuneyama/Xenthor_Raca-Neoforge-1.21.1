package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.shune.xenthor_racas.voo.AsaCelestial;
import net.shune.xenthor_racas.voo.RegistroItens;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class VooCelestial {

    public static final String TAG_VOO_ATIVO = ModPrincipal.ID_MOD + ":celestial_voo_ativo";
    public static final String TAG_ITEM_GUARDADO = ModPrincipal.ID_MOD + ":celestial_item_peito_guardado";

    private static final float EXAUSTAO_TICK = 0.2f;
    private static final double IMPULSO_EXTRA_FORCA = 0.3;
    private static final int IMPULSO_COOLDOWN_TICKS = 10;
    private static final String TAG_IMPULSO_COOLDOWN = ModPrincipal.ID_MOD + ":celestial_impulso_cd";

    private static final Set<Raca> RACAS_COM_VOO = Set.of(
            Raca.CELESTIAL, Raca.BESTIAL_AEREO, Raca.FADA, Raca.DRAGONIC, Raca.VAMPIRO, Raca.DAMPIRO
    );

    private static double velocidadeVoo(Raca raca) {
        return switch (raca) {
            case CELESTIAL     -> 1.0;
            case BESTIAL_AEREO -> 1.25;
            case FADA          -> 1.5;
            case DRAGONIC      -> 1.0;
            case VAMPIRO       -> 1.0;
            case DAMPIRO       -> 1.0;
            default            -> 1.0;
        };
    }

    public static boolean podeVoar(ServerPlayer jogador) {
        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        return raca != null && RACAS_COM_VOO.contains(raca);
    }

    public static boolean estaAtivo(ServerPlayer jogador) {
        return jogador.getPersistentData().getBoolean(TAG_VOO_ATIVO);
    }

    public static void alternar(ServerPlayer jogador) {
        if (!podeVoar(jogador)) return;

        boolean ativo = !estaAtivo(jogador);
        jogador.getPersistentData().putBoolean(TAG_VOO_ATIVO, ativo);

        if (ativo) {
            equiparAsa(jogador);
        } else {
            desequiparAsa(jogador);
            if (jogador.isFallFlying()) jogador.stopFallFlying();
        }

        jogador.sendSystemMessage(Component.literal(
                        ativo ? "Poder de voo ativado!"
                                : "Poder de voo desativado.")
                .withStyle(ativo ? ChatFormatting.YELLOW : ChatFormatting.GRAY));

        net.shune.xenthor_racas.rede.RedeXenthor.enviarVooCelestial(jogador, ativo);
    }

    private static void equiparAsa(ServerPlayer jogador) {
        ItemStack peitoralAtual = jogador.getItemBySlot(EquipmentSlot.CHEST);
        if (!(peitoralAtual.getItem() instanceof AsaCelestial)) {
            if (!peitoralAtual.isEmpty()) {
                jogador.getPersistentData().put(TAG_ITEM_GUARDADO, peitoralAtual.save(jogador.registryAccess()));
            }
            jogador.setItemSlot(EquipmentSlot.CHEST, new ItemStack(RegistroItens.ASA_CELESTIAL.get()));
        }
    }

    private static void desequiparAsa(ServerPlayer jogador) {
        ItemStack peitoralAtual = jogador.getItemBySlot(EquipmentSlot.CHEST);
        if (peitoralAtual.getItem() instanceof AsaCelestial) {
            if (jogador.getPersistentData().contains(TAG_ITEM_GUARDADO)) {
                ItemStack guardado = ItemStack.parse(
                        jogador.registryAccess(),
                        jogador.getPersistentData().getCompound(TAG_ITEM_GUARDADO)
                ).orElse(ItemStack.EMPTY);
                jogador.setItemSlot(EquipmentSlot.CHEST, guardado);
                jogador.getPersistentData().remove(TAG_ITEM_GUARDADO);
            } else {
                jogador.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            }
        }
    }

    public static void darImpulso(ServerPlayer jogador) {
        if (!estaAtivo(jogador)) return;
        if (!jogador.isFallFlying()) return;

        long agora = jogador.serverLevel().getGameTime();
        long ultimoImpulso = jogador.getPersistentData().getLong(TAG_IMPULSO_COOLDOWN);
        if (agora - ultimoImpulso < IMPULSO_COOLDOWN_TICKS) return;

        jogador.getPersistentData().putLong(TAG_IMPULSO_COOLDOWN, agora);

        Vec3 olhar = jogador.getLookAngle();
        Vec3 velocidadeAtual = jogador.getDeltaMovement();
        jogador.setDeltaMovement(velocidadeAtual.add(olhar.scale(IMPULSO_EXTRA_FORCA)));
        jogador.hurtMarked = true;
        jogador.causeFoodExhaustion(1.0f);
    }

    @SubscribeEvent
    public static void depoisDoTick(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtivo(jogador)) return;
        if (!jogador.isFallFlying()) return;

        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        if (raca == null) return;

        double velocidade = velocidadeVoo(raca);

        if (jogador.tickCount % 20 == 0) {
            Vec3 olhar = jogador.getLookAngle();
            jogador.setDeltaMovement(olhar.scale(velocidade));
            jogador.hurtMarked = true;
        }

        jogador.causeFoodExhaustion(EXAUSTAO_TICK);
    }

    @SubscribeEvent
    public static void aoRespawnar(PlayerEvent.PlayerRespawnEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!estaAtivo(jogador)) return;

        equiparAsa(jogador);
    }
}