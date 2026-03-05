package net.shune.xenthor_racas.cliente;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.shune.xenthor_racas.ModPrincipal;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class RenderAsasCelestial {

    private static final ResourceLocation TEXTURA_ASAS =
            ResourceLocation.fromNamespaceAndPath(ModPrincipal.ID_MOD, "textures/entity/asas_celestial.png");

    private static final Set<UUID> jogadoresVoando = new HashSet<>();

    public static void marcarVoo(UUID uuid, boolean ativo) {
        if (ativo) jogadoresVoando.add(uuid);
        else jogadoresVoando.remove(uuid);
    }

    @SubscribeEvent
    public static void aoRenderizarJogadorPos(RenderPlayerEvent.Post evento) {
        Player jogador = evento.getEntity();
        if (!(jogador instanceof AbstractClientPlayer clientPlayer)) return;

        String racaSalva = clientPlayer.getPersistentData().getString(ModPrincipal.TAG_RACA);
        boolean ehCelestial = net.shune.xenthor_racas.Raca.CELESTIAL.id.equals(racaSalva);
        if (!ehCelestial) return;

        PoseStack      poseStack   = evento.getPoseStack();
        MultiBufferSource buffers  = evento.getMultiBufferSource();
        int            packedLight = evento.getPackedLight();

        boolean voando = jogadoresVoando.contains(jogador.getUUID());
        float anguloAsa = voando ? 30f : 10f;

        renderizarAsas(poseStack, buffers, packedLight, anguloAsa);
    }

    private static void renderizarAsas(PoseStack pose, MultiBufferSource buffers,
                                        int luz, float anguloAsa) {
        var consumer = buffers.getBuffer(RenderType.entityCutout(TEXTURA_ASAS));

        pose.pushPose();
        pose.translate(-0.3, 0.3, 0.15);
        pose.mulPose(Axis.ZP.rotationDegrees(-anguloAsa));
        renderQuad(pose, consumer, luz, 0.5f, 0.8f);
        pose.popPose();

        pose.pushPose();
        pose.translate(0.3, 0.3, 0.15);
        pose.mulPose(Axis.ZN.rotationDegrees(-anguloAsa));
        renderQuad(pose, consumer, luz, 0.5f, 0.8f);
        pose.popPose();
    }

    private static void renderQuad(PoseStack pose, com.mojang.blaze3d.vertex.VertexConsumer consumer,
                                    int luz, float largura, float altura) {
        var matrix = pose.last().pose();
        var normal  = pose.last().normal();

        consumer.addVertex(matrix, -largura, 0,      0).setColor(1f,1f,1f,1f).setUv(0,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(normal, 0,0,1);
        consumer.addVertex(matrix, -largura, altura, 0).setColor(1f,1f,1f,1f).setUv(0,1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(normal, 0,0,1);
        consumer.addVertex(matrix,  largura, altura, 0).setColor(1f,1f,1f,1f).setUv(1,1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(normal, 0,0,1);
        consumer.addVertex(matrix,  largura, 0,      0).setColor(1f,1f,1f,1f).setUv(1,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(luz).setNormal(normal, 0,0,1);
    }
}
