package net.shune.xenthor_racas.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shune.xenthor_racas.ModPrincipal;
import net.shune.xenthor_racas.Raca;
import net.shune.xenthor_racas.cliente.EspectralCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockState {

    @Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("RETURN"), cancellable = true)
    private void xenthor_phasingColisao(BlockGetter world, BlockPos pos, CollisionContext context,
                                        CallbackInfoReturnable<VoxelShape> cir) {
        VoxelShape shape = cir.getReturnValue();
        if (shape.isEmpty()) return;
        if (!(context instanceof EntityCollisionContext entityCtx)) return;

        Entity entity = entityCtx.getEntity();
        if (!(entity instanceof Player jogador)) return;

        if (xenthor_estaEspectral(jogador)) {
            boolean acima = xenthor_estaAcima(jogador, shape, pos);
            if (acima) {
                if (jogador.isShiftKeyDown() && jogador.onGround()) {
                    cir.setReturnValue(Shapes.empty());
                }
            } else {
                cir.setReturnValue(Shapes.empty());
            }
            return;
        }

        if (xenthor_ehFada(jogador)) {
            BlockState estado = (BlockState) (Object) this;
            if (estado.is(BlockTags.LEAVES)) {
                cir.setReturnValue(Shapes.empty());
            }
        }
    }

    @Inject(method = "getVisualShape", at = @At("RETURN"), cancellable = true)
    private void xenthor_xrayEspectral(BlockGetter world, BlockPos pos, CollisionContext context,
                                       CallbackInfoReturnable<VoxelShape> cir) {
        if (cir.getReturnValue().isEmpty()) return;
        try {
            net.shune.xenthor_racas.cliente.VisaoEspectralHelper.processarVisualShape(pos, cir);
        } catch (Throwable ignored) {}
    }

    @Unique
    private boolean xenthor_estaEspectral(Player jogador) {
        if (jogador.level().isClientSide) {
            return EspectralCache.estaEspectral(jogador.getUUID());
        }
        String racaId = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!Raca.ESPIRITO.id.equals(racaId)) return false;
        String tagEspectral = ModPrincipal.ID_MOD + ":espirito_espectral";
        return jogador.getPersistentData().getBoolean(tagEspectral);
    }

    @Unique
    private boolean xenthor_ehFada(Player jogador) {
        if (jogador.level().isClientSide) {
            return Raca.FADA.id.equals(net.shune.xenthor_racas.cliente.RacaCache.obter(jogador.getUUID()));
        }
        String racaId = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        return Raca.FADA.id.equals(racaId);
    }

    @Unique
    private boolean xenthor_estaAcima(Entity entity, VoxelShape shape, BlockPos pos) {
        double topoBloco = pos.getY() + shape.max(Direction.Axis.Y);
        double tolerancia = entity.onGround() ? 8.05 / 16.0 : 0.0015;
        return entity.getY() > topoBloco - tolerancia;
    }
}