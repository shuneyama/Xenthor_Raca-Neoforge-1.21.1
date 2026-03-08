package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PacoteSyncRaca(UUID uuid, String racaId) implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "sync_raca");
    public static final CustomPacketPayload.Type<PacoteSyncRaca> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteSyncRaca> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> { buf.writeUUID(pkt.uuid()); buf.writeUtf(pkt.racaId()); },
                    buf -> new PacoteSyncRaca(buf.readUUID(), buf.readUtf())
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}