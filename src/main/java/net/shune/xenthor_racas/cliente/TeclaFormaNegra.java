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
import net.shune.xenthor_racas.rede.PacoteAtivarFormaNegra;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class TeclaFormaNegra {

    public static KeyMapping TECLA_FORMA_NEGRA;

    @SubscribeEvent
    public static void aoRegistrarTeclas(RegisterKeyMappingsEvent evento) {
        TECLA_FORMA_NEGRA = new KeyMapping(
            "key.xenthor_racas.forma_negra",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.xenthor_racas"
        );
        evento.register(TECLA_FORMA_NEGRA);
    }

    @SubscribeEvent
    public static void aoTickCliente(ClientTickEvent.Post evento) {
        if (TECLA_FORMA_NEGRA == null) return;
        if (!TECLA_FORMA_NEGRA.consumeClick()) return;
        if (Minecraft.getInstance().player == null) return;

        PacketDistributor.sendToServer(new PacoteAtivarFormaNegra());
    }
}
