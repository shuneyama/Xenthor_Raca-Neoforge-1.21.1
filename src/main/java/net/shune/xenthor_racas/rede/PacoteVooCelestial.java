package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PacoteVooCelestial(UUID uuid, boolean ativo) implements CustomPacketPayload {
    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "voo_celestial");
    public static final CustomPacketPayload.Type<PacoteVooCelestial> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);
    public static final StreamCodec<FriendlyByteBuf, PacoteVooCelestial> CODEC =
            StreamCodec.of(
                (buf, pkt) -> { buf.writeUUID(pkt.uuid()); buf.writeBoolean(pkt.ativo()); },
                buf -> new PacoteVooCelestial(buf.readUUID(), buf.readBoolean())
            );

    @Override public Type<? extends CustomPacketPayload> type() { return TIPO; }
}
