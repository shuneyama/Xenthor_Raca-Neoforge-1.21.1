package net.shune.xenthor_racas;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorFada {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final String NOME_EQUIPE = "xenthor_fada_glow";

    public static void aplicarEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe == null) {
            equipe = placar.addPlayerTeam(NOME_EQUIPE);
            equipe.setColor(ChatFormatting.LIGHT_PURPLE);
            equipe.setNameTagVisibility(Team.Visibility.ALWAYS);
        }
        placar.addPlayerToTeam(jogador.getScoreboardName(), equipe);
    }

    public static void removerEquipeGlowing(ServerPlayer jogador) {
        Scoreboard placar = jogador.serverLevel().getScoreboard();
        PlayerTeam equipe = placar.getPlayerTeam(NOME_EQUIPE);
        if (equipe != null) {
            placar.removePlayerFromTeam(jogador.getScoreboardName(), equipe);
        }
    }

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.FADA.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armadura = jogador.getItemBySlot(slot);
            if (armadura.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getToughness() > 0) {
                    jogador.setItemSlot(slot, ItemStack.EMPTY);
                    jogador.drop(armadura, false);
                    jogador.sendSystemMessage(Component.literal("Fadas nao podem usar armaduras pesadas!")
                            .withStyle(ChatFormatting.LIGHT_PURPLE));
                }
            }
        }

        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        jogador.forceAddEffect(new MobEffectInstance(MobEffects.REGENERATION, DURACAO_EFEITO, 1, true, false), null);
        jogador.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURACAO_EFEITO, 4, true, false), null);
    }

    @SubscribeEvent
    public static void aoMudarEquipamento(LivingEquipmentChangeEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.FADA.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        ItemStack novo = evento.getTo();
        if (novo.getItem() instanceof ArmorItem armorItem) {
            if (armorItem.getToughness() > 0) {
                EquipmentSlot slot = evento.getSlot();
                jogador.setItemSlot(slot, ItemStack.EMPTY);
                jogador.drop(novo.copy(), false);
                jogador.sendSystemMessage(Component.literal("Fadas nao podem usar armaduras pesadas!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }
}