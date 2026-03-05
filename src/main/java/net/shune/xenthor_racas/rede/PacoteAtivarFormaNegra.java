package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacoteAtivarFormaNegra() implements CustomPacketPayload {

    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "ativar_forma_negra");

    public static final CustomPacketPayload.Type<PacoteAtivarFormaNegra> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);

    public static final StreamCodec<FriendlyByteBuf, PacoteAtivarFormaNegra> CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new PacoteAtivarFormaNegra());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}
