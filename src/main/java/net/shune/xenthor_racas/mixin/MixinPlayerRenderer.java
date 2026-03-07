package net.shune.xenthor_racas.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.shune.xenthor_racas.cliente.RenderTransformacao;
import net.shune.xenthor_racas.cliente.TransformacaoCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class MixinPlayerRenderer {

    @Inject(method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("HEAD"), cancellable = true)
    private void xenthor_renderTransformacao(AbstractClientPlayer jogador, float entityYaw, float partialTick,
                                             PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                             CallbackInfo ci) {
        if (TransformacaoCache.estaTransformado(jogador.getUUID())) {
            boolean renderizado = RenderTransformacao.renderizar(jogador, entityYaw, partialTick, poseStack, buffer, packedLight);
            if (renderizado) {
                ci.cancel();
            }
        }
    }
}