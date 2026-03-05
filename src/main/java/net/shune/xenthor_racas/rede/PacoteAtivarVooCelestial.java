package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacoteAtivarVooCelestial() implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "ativar_voo_celestial");
    public static final CustomPacketPayload.Type<PacoteAtivarVooCelestial> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteAtivarVooCelestial> CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new PacoteAtivarVooCelestial());

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}
