package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacoteGlowing() implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "glowing");
    public static final CustomPacketPayload.Type<PacoteGlowing> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteGlowing> CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new PacoteGlowing());

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}