package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.Random;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class MonitorAnao {

    private static final int DURACAO_EFEITO = 400;
    private static final int INTERVALO_TICK = 100;
    private static final Random RNG = new Random();

    @SubscribeEvent
    public static void aoTickJogador(PlayerTickEvent.Post evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (jogador.tickCount % INTERVALO_TICK != 0) return;

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ANAO.id.equals(racaSalva)) return;

        jogador.forceAddEffect(new MobEffectInstance(
                MobEffects.DAMAGE_RESISTANCE, DURACAO_EFEITO, 1, true, false), null);

        boolean emCaverna = !jogador.serverLevel().canSeeSky(jogador.blockPosition());
        if (emCaverna) {
            jogador.forceAddEffect(new MobEffectInstance(
                    MobEffects.DIG_SPEED, DURACAO_EFEITO, 3, true, false), null);
        } else {
            var haste = jogador.getEffect(MobEffects.DIG_SPEED);
            if (haste != null && haste.isAmbient())
                jogador.removeEffect(MobEffects.DIG_SPEED);
        }
    }

    @SubscribeEvent
    public static void aoKnockback(LivingKnockBackEvent evento) {
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;
        evento.setCanceled(true);
    }

    @SubscribeEvent
    public static void aoDroparBloco(BlockDropsEvent evento) {
        if (!(evento.getBreaker() instanceof ServerPlayer jogador)) return;
        if (!Raca.ANAO.id.equals(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA))) return;

        BlockState estado = evento.getState();

        boolean ehMinerio = estado.is(net.minecraft.tags.BlockTags.COAL_ORES)
                || estado.is(net.minecraft.tags.BlockTags.IRON_ORES)
                || estado.is(net.minecraft.tags.BlockTags.GOLD_ORES)
                || estado.is(net.minecraft.tags.BlockTags.DIAMOND_ORES)
                || estado.is(net.minecraft.tags.BlockTags.EMERALD_ORES)
                || estado.is(net.minecraft.tags.BlockTags.LAPIS_ORES)
                || estado.is(net.minecraft.tags.BlockTags.REDSTONE_ORES)
                || estado.is(net.minecraft.tags.BlockTags.COPPER_ORES);

        if (!ehMinerio) return;

        var drops = evento.getDrops();
        if (drops.isEmpty()) return;

        double rng = RNG.nextDouble();
        int multiplicador = 1;
        if (rng < 0.25)      multiplicador = 2;
        else if (rng < 0.50) multiplicador = 3;
        else if (rng < 0.75) multiplicador = 4;

        if (multiplicador <= 1) return;

        var extras = new ArrayList<net.minecraft.world.entity.item.ItemEntity>();
        for (var itemEntity : drops) {
            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) continue;
            int quantidade = stack.getCount() * (multiplicador - 1);
            if (quantidade <= 0) continue;

            ItemStack extra = stack.copyWithCount(quantidade);
            net.minecraft.world.entity.item.ItemEntity entidade = new net.minecraft.world.entity.item.ItemEntity(
                    jogador.serverLevel(),
                    itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                    extra
            );
            extras.add(entidade);
        }
        drops.addAll(extras);
    }
}