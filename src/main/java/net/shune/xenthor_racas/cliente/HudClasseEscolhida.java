package net.shune.xenthor_racas.cliente;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.shune.xenthor_racas.ModPrincipal;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class HudClasseEscolhida {

    private static final int DURACAO_TOTAL = 400;
    private static final int TICKS_POP_ENTRADA  = 100;
    private static final int TICKS_POP_SAIDA     = 50;

    private static boolean ativo        = false;
    private static int     tickAtual    = 0;
    private static String  nomeClasse   = "";
    private static String  nomeElemento = "";
    private static ItemStack itemClasse = ItemStack.EMPTY;
    private static float anguloGiro     = 0f;

    public static void ativar(String idClasse, String idElemento) {
        ativo        = true;
        tickAtual    = 0;
        nomeClasse   = idClasse;
        nomeElemento = idElemento;
        anguloGiro   = 0f;
        itemClasse   = resolverItem(idClasse);
    }

    @SubscribeEvent
    public static void aoRenderizarHud(RenderGuiLayerEvent.Post evento) {
        if (!evento.getName().equals(VanillaGuiLayers.HOTBAR)) return;
        if (!ativo) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        tickAtual++;
        if (tickAtual > DURACAO_TOTAL) {
            ativo = false;
            return;
        }

        GuiGraphics graficos = evento.getGuiGraphics();
        int larguraTela = mc.getWindow().getGuiScaledWidth();
        int alturaTela  = mc.getWindow().getGuiScaledHeight();

        int centroX = larguraTela / 2;
        int yBase   = alturaTela - 65;

        renderizarTexto(graficos, mc, centroX, yBase);
        renderizarItem(graficos, mc, centroX, yBase - 30);
    }

    private static void renderizarTexto(GuiGraphics graficos, Minecraft mc, int centroX, int y) {
        float escala;
        if (tickAtual <= TICKS_POP_ENTRADA) {
            escala = easeOutBack((float) tickAtual / TICKS_POP_ENTRADA);
        } else if (tickAtual >= DURACAO_TOTAL - TICKS_POP_SAIDA) {
            int tickSaida = tickAtual - (DURACAO_TOTAL - TICKS_POP_SAIDA);
            escala = 1f - easeInBack((float) tickSaida / TICKS_POP_SAIDA);
        } else {
            escala = 1f;
        }

        if (escala <= 0f) return;

        String linhaUm  = "Voce fez a sua escolha!";
        String linhaDois = "Voce possui a classe de: " + traduzirClasse();

        int larguraMax  = Math.max(mc.font.width(linhaUm), mc.font.width(linhaDois));
        int paddingH = 8;
        int paddingV = 5;
        int larguraCaixa = larguraMax + paddingH * 2;
        int alturaCaixa  = mc.font.lineHeight * 2 + 4 + paddingV * 2;

        graficos.pose().pushPose();
        graficos.pose().translate(centroX, y, 0);
        graficos.pose().scale(escala, escala, 1f);
        graficos.pose().translate(-centroX, -y, 0);

        int esq = centroX - larguraCaixa / 2;
        int top = y - alturaCaixa / 2;

        graficos.fill(esq - 2, top - 2, esq + larguraCaixa + 2, top + alturaCaixa + 2, 0xFFFFFFFF);
        graficos.fill(esq,     top,     esq + larguraCaixa,     top + alturaCaixa,     0xCC000000);

        int yTexto = top + paddingV;
        graficos.drawCenteredString(mc.font, linhaUm,  centroX, yTexto,                        0xFFFFFF);
        graficos.drawCenteredString(mc.font, linhaDois, centroX, yTexto + mc.font.lineHeight + 4, corDaClasse());

        graficos.pose().popPose();
    }

    private static void renderizarItem(GuiGraphics graficos, Minecraft mc, int centroX, int y) {
        if (itemClasse.isEmpty()) return;

        int faseItem = tickAtual - TICKS_POP_ENTRADA;
        int inicioPop = DURACAO_TOTAL - TICKS_POP_SAIDA;

        if (faseItem < 0) return;

        float escala;
        if (faseItem <= TICKS_POP_ENTRADA) {
            escala = easeOutBack((float) faseItem / TICKS_POP_ENTRADA);
        } else if (tickAtual >= inicioPop) {
            int tickSaida = tickAtual - inicioPop;
            escala = 1f - easeInBack((float) tickSaida / TICKS_POP_SAIDA);
        } else {
            escala = 1f;
        }

        if (escala <= 0f) return;

        anguloGiro = (tickAtual * 4f) % 360f;

        ItemRenderer itemRenderer = mc.getItemRenderer();
        BakedModel modelo = itemRenderer.getModel(itemClasse, mc.level, mc.player, 0);

        PoseStack pose = graficos.pose();
        pose.pushPose();

        pose.translate(centroX, y, 150);
        pose.scale(escala * 16f, -escala * 16f, escala * 16f);
        pose.mulPose(Axis.YP.rotationDegrees(anguloGiro));

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        itemRenderer.render(
                itemClasse,
                ItemDisplayContext.GUI,
                false,
                pose,
                bufferSource,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                modelo
        );

        bufferSource.endBatch();
        pose.popPose();
    }

    private static ItemStack resolverItem(String idClasse) {
        return switch (idClasse) {
            case "guerreiro"        -> new ItemStack(Items.IRON_SWORD);
            case "guerreiro_magico" -> new ItemStack(Items.BLAZE_POWDER);
            case "mago"             -> new ItemStack(Items.AMETHYST_SHARD);
            default                 -> new ItemStack(Items.NETHER_STAR);
        };
    }

    private static String traduzirClasse() {
        return switch (nomeClasse) {
            case "guerreiro"        -> "Guerreiro";
            case "guerreiro_magico" -> "Guerreiro Mágico";
            case "mago"             -> nomeElemento.isEmpty()
                                        ? "Mago"
                                        : "Mago (" + capitalize(nomeElemento) + ")";
            default -> nomeClasse;
        };
    }

    private static int corDaClasse() {
        return switch (nomeClasse) {
            case "guerreiro"        -> 0xFF6666;
            case "guerreiro_magico" -> 0xFFAA00;
            case "mago"             -> 0xAA66FF;
            default                 -> 0xFFFFFF;
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return 1f + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    private static float easeInBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        return c3 * t * t * t - c1 * t * t;
    }
}
