package net.shune.xenthor_racas.cliente;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class VisaoEspectralHelper {

    public static void processarVisualShape(BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!EspectralCache.estaEspectral(mc.player.getUUID())) return;

        BlockPos olhos = BlockPos.containing(mc.player.getEyePosition(mc.getTimer().getGameTimeDeltaPartialTick(false)));
        int dx = Math.abs(pos.getX() - olhos.getX());
        int dy = Math.abs(pos.getY() - olhos.getY());
        int dz = Math.abs(pos.getZ() - olhos.getZ());
        if (dx <= 1 && dy <= 1 && dz <= 1) {
            cir.setReturnValue(Shapes.empty());
        }
    }

    public static void processarSufocamento(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (EspectralCache.estaEspectral(mc.player.getUUID())) {
            cir.setReturnValue(false);
        }
    }
}