package net.shune.xenthor_racas.cliente;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.shune.xenthor_racas.ModPrincipal;
import net.shune.xenthor_racas.rede.PacotePoderPrimario;
import net.shune.xenthor_racas.rede.PacotePoderSecundario;
import net.shune.xenthor_racas.rede.PacoteTransformacao;
import org.lwjgl.glfw.GLFW;

public class TeclasXenthor {

    public static KeyMapping TECLA_PODER_1;
    public static KeyMapping TECLA_PODER_2;
    public static KeyMapping TECLA_TRANSFORMACAO;

    public static void registrarTeclas(RegisterKeyMappingsEvent evento) {
        TECLA_PODER_1 = new KeyMapping(
                "key.xenthor_racas.poder_primario",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "key.categories.xenthor_racas"
        );
        TECLA_PODER_2 = new KeyMapping(
                "key.xenthor_racas.poder_secundario",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                "key.categories.xenthor_racas"
        );
        TECLA_TRANSFORMACAO = new KeyMapping(
                "key.xenthor_racas.transformacao",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "key.categories.xenthor_racas"
        );
        evento.register(TECLA_PODER_1);
        evento.register(TECLA_PODER_2);
        evento.register(TECLA_TRANSFORMACAO);
    }

    @EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
    public static class TickHandler {
        @SubscribeEvent
        public static void aoTickCliente(ClientTickEvent.Post evento) {
            if (Minecraft.getInstance().player == null) return;

            if (TECLA_PODER_1 != null && TECLA_PODER_1.consumeClick())
                PacketDistributor.sendToServer(new PacotePoderPrimario());

            if (TECLA_PODER_2 != null && TECLA_PODER_2.consumeClick())
                PacketDistributor.sendToServer(new PacotePoderSecundario());

            if (TECLA_TRANSFORMACAO != null && TECLA_TRANSFORMACAO.consumeClick())
                PacketDistributor.sendToServer(new PacoteTransformacao());
        }
    }
}