package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacoteAtivarCuraCelestial() implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "ativar_cura_celestial");
    public static final CustomPacketPayload.Type<PacoteAtivarCuraCelestial> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteAtivarCuraCelestial> CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new PacoteAtivarCuraCelestial());

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}
