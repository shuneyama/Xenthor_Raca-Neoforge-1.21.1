package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record PacoteFormaNegra(UUID uuid, boolean ativa) implements CustomPacketPayload {

    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "forma_negra");

    public static final CustomPacketPayload.Type<PacoteFormaNegra> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);

    public static final StreamCodec<FriendlyByteBuf, PacoteFormaNegra> CODEC =
            StreamCodec.of(
                (buf, pkt) -> { buf.writeUUID(pkt.uuid()); buf.writeBoolean(pkt.ativa()); },
                buf -> new PacoteFormaNegra(buf.readUUID(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}
