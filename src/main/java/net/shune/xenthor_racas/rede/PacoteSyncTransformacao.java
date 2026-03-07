package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PacoteSyncTransformacao(UUID uuid, String forma) implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "sync_transformacao");
    public static final CustomPacketPayload.Type<PacoteSyncTransformacao> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteSyncTransformacao> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> { buf.writeUUID(pkt.uuid()); buf.writeUtf(pkt.forma()); },
                    buf -> new PacoteSyncTransformacao(buf.readUUID(), buf.readUtf())
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}