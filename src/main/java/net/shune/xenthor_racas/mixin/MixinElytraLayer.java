package net.shune.xenthor_racas.mixin;

import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.shune.xenthor_racas.voo.AsaCelestial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

@Mixin(ElytraLayer.class)
public class MixinElytraLayer {

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/LivingEntity;FFFFFF)V",
            at = @At("HEAD"), cancellable = true)
    private void xenthor_esconderAsaCelestial(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                                              LivingEntity entidade, float limbSwing, float limbSwingAmount,
                                              float partialTick, float ageInTicks, float netHeadYaw, float headPitch,
                                              CallbackInfo ci) {
        ItemStack peito = entidade.getItemBySlot(EquipmentSlot.CHEST);
        if (peito.getItem() instanceof AsaCelestial) {
            ci.cancel();
        }
    }
}