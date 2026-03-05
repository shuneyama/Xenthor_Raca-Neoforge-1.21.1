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
import net.shune.xenthor_racas.rede.PacoteAtivarCuraCelestial;
import net.shune.xenthor_racas.rede.PacoteAtivarVooCelestial;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD, value = Dist.CLIENT)
public class TeclasCelestial {

    public static KeyMapping TECLA_CURA;
    public static KeyMapping TECLA_VOO;

    @SubscribeEvent
    public static void aoRegistrarTeclas(RegisterKeyMappingsEvent evento) {
        TECLA_CURA = new KeyMapping(
            "key.xenthor_racas.cura_celestial",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.xenthor_racas"
        );
        TECLA_VOO = new KeyMapping(
            "key.xenthor_racas.voo_celestial",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.categories.xenthor_racas"
        );
        evento.register(TECLA_CURA);
        evento.register(TECLA_VOO);
    }

    @SubscribeEvent
    public static void aoTickCliente(ClientTickEvent.Post evento) {
        if (Minecraft.getInstance().player == null) return;

        if (TECLA_CURA != null && TECLA_CURA.consumeClick())
            PacketDistributor.sendToServer(new PacoteAtivarCuraCelestial());

        if (TECLA_VOO != null && TECLA_VOO.consumeClick())
            PacketDistributor.sendToServer(new PacoteAtivarVooCelestial());
    }
}
