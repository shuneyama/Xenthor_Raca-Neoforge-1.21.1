package net.shune.xenthor_racas.rede;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.shune.xenthor_racas.PoderRaca;
import net.shune.xenthor_racas.PoderTransformacao;
import net.shune.xenthor_racas.cliente.ManipuladorPacoteCliente;

public class RedeXenthor {

    public static void registrar(IEventBus barramentoMod) {
        barramentoMod.addListener(RedeXenthor::aoRegistrarPayloads);
    }

    private static void aoRegistrarPayloads(RegisterPayloadHandlersEvent evento) {
        PayloadRegistrar reg = evento.registrar("1.0.0");

        reg.playToClient(PacoteClasseEscolhida.TIPO, PacoteClasseEscolhida.CODEC, ManipuladorPacoteCliente::aoReceberClasseEscolhida);
        reg.playToClient(PacoteFormaNegra.TIPO, PacoteFormaNegra.CODEC, ManipuladorPacoteCliente::aoReceberFormaNegra);
        reg.playToClient(PacoteVooCelestial.TIPO, PacoteVooCelestial.CODEC, ManipuladorPacoteCliente::aoReceberVooCelestial);
        reg.playToClient(PacoteSyncTransformacao.TIPO, PacoteSyncTransformacao.CODEC, ManipuladorPacoteCliente::aoReceberTransformacao);
        reg.playToClient(PacoteSyncEspectral.TIPO, PacoteSyncEspectral.CODEC, ManipuladorPacoteCliente::aoReceberEspectral);
        reg.playToClient(PacoteSyncRaca.TIPO, PacoteSyncRaca.CODEC, ManipuladorPacoteCliente::aoReceberRaca);

        reg.playToServer(PacotePoderPrimario.TIPO, PacotePoderPrimario.CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer j) PoderRaca.ativarPrimario(j);
                }));

        reg.playToServer(PacotePoderSecundario.TIPO, PacotePoderSecundario.CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer j) PoderRaca.ativarSecundario(j);
                }));

        reg.playToServer(PacoteTransformacao.TIPO, PacoteTransformacao.CODEC,
                (pkt, ctx) -> ctx.enqueueWork(() -> {
                    if (ctx.player() instanceof ServerPlayer j) PoderTransformacao.tentar(j);
                }));
    }

    public static void enviarParaJogador(ServerPlayer jogador, String idClasse, String idElemento) {
        PacketDistributor.sendToPlayer(jogador, new PacoteClasseEscolhida(idClasse, idElemento));
    }

    public static void enviarFormaNegra(ServerPlayer jogador, boolean ativa) {
        PacketDistributor.sendToAllPlayers(new PacoteFormaNegra(jogador.getUUID(), ativa));
    }

    public static void enviarVooCelestial(ServerPlayer jogador, boolean ativo) {
        PacketDistributor.sendToAllPlayers(new PacoteVooCelestial(jogador.getUUID(), ativo));
    }

    public static void enviarTransformacao(ServerPlayer jogador, String forma) {
        PacketDistributor.sendToAllPlayers(new PacoteSyncTransformacao(jogador.getUUID(), forma));
    }

    public static void enviarEspectral(ServerPlayer jogador, boolean ativo) {
        PacketDistributor.sendToAllPlayers(new PacoteSyncEspectral(jogador.getUUID(), ativo));
    }

    public static void enviarRaca(ServerPlayer jogador, String racaId) {
        PacketDistributor.sendToAllPlayers(new PacoteSyncRaca(jogador.getUUID(), racaId));
    }
}