package net.shune.xenthor_racas.cliente;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.shune.xenthor_racas.mixin.AccessorBat;
import net.shune.xenthor_racas.mixin.AccessorWalkAnimationState;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RenderTransformacao {

    public static boolean renderizar(Player jogador, float entityYaw, float partialTick,
                                     PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity entidade = TransformacaoCache.obterEntidade(jogador.getUUID());
        if (entidade == null) return false;

        String forma = TransformacaoCache.obterForma(jogador.getUUID());
        sincronizarAtributos(jogador, entidade, forma);

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer renderer = mc.getEntityRenderDispatcher().getRenderer(entidade);
        if (renderer == null) return false;

        entidade.setPos(jogador.getX(), jogador.getY(), jogador.getZ());

        try {
            Vec3 offset = renderer.getRenderOffset(entidade, partialTick);
            poseStack.pushPose();
            poseStack.translate(offset.x, offset.y, offset.z);
            renderer.render(entidade, entityYaw, partialTick, poseStack, buffer, packedLight);
            poseStack.popPose();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static void sincronizarAtributos(Player jogador, LivingEntity entidade, String forma) {
        ((AccessorWalkAnimationState) entidade.walkAnimation).xenthor_setSpeedOld(
                ((AccessorWalkAnimationState) jogador.walkAnimation).xenthor_getSpeedOld());
        entidade.walkAnimation.setSpeed(jogador.walkAnimation.speed());
        ((AccessorWalkAnimationState) entidade.walkAnimation).xenthor_setPosition(jogador.walkAnimation.position());

        entidade.swinging = jogador.swinging;
        entidade.swingTime = jogador.swingTime;
        entidade.oAttackAnim = jogador.oAttackAnim;
        entidade.attackAnim = jogador.attackAnim;

        entidade.yBodyRot = jogador.yBodyRot;
        entidade.yBodyRotO = jogador.yBodyRotO;
        entidade.yHeadRot = jogador.yHeadRot;
        entidade.yHeadRotO = jogador.yHeadRotO;

        entidade.setYRot(jogador.getYRot());
        entidade.yRotO = jogador.yRotO;

        entidade.tickCount = jogador.tickCount;
        entidade.setOnGround(jogador.onGround());
        entidade.hurtTime = jogador.hurtTime;

        float x = (float) ((jogador.getX() - jogador.xOld) / 1.62);
        float y = (float) ((jogador.getY() - jogador.yOld) / 1.62);
        float z = (float) ((jogador.getZ() - jogador.zOld) / 1.62);
        entidade.setDeltaMovement(new Vec3(x, y, z));

        entidade.setInvisible(false);

        if ("morcego".equals(forma) && entidade instanceof Bat bat) {
            bat.setResting(false);
            entidade.setXRot(jogador.getXRot());
            entidade.xRotO = jogador.xRotO;
            ((AccessorBat) bat).xenthor_setupAnimationStates();
        } else if ("peixe".equals(forma)) {
            entidade.setXRot(0);
            entidade.xRotO = 0;
        } else {
            entidade.setXRot(jogador.getXRot());
            entidade.xRotO = jogador.xRotO;
            entidade.setPose(jogador.getPose());
        }
    }
}