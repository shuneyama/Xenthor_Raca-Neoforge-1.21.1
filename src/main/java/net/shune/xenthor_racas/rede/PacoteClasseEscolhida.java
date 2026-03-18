package net.shune.xenthor_racas.rede;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PacoteClasseEscolhida(String idClasse, String idElemento, boolean silencioso) implements CustomPacketPayload {

    public static final ResourceLocation ID_PACOTE =
            ResourceLocation.fromNamespaceAndPath("xenthor_racas", "classe_escolhida");

    public static final CustomPacketPayload.Type<PacoteClasseEscolhida> TIPO =
            new CustomPacketPayload.Type<>(ID_PACOTE);

    public static final StreamCodec<FriendlyByteBuf, PacoteClasseEscolhida> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeUtf(pkt.idClasse());
                        buf.writeUtf(pkt.idElemento());
                        buf.writeBoolean(pkt.silencioso());
                    },
                    buf -> new PacoteClasseEscolhida(buf.readUtf(), buf.readUtf(), buf.readBoolean())
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TIPO;
    }
}