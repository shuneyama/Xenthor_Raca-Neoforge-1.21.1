package net.shune.xenthor_racas.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import net.shune.xenthor_racas.cliente.EspectralCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class MixinScreenEffect {

    @Inject(method = "renderScreenEffect", at = @At("HEAD"), cancellable = true)
    private static void xenthor_semOverlayBlocoEspectral(Minecraft mc, PoseStack poseStack, CallbackInfo ci) {
        if (mc.player == null) return;
        if (EspectralCache.estaEspectral(mc.player.getUUID())) {
            ci.cancel();
        }
    }
}