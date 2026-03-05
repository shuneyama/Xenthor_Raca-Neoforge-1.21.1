package net.shune.xenthor_racas.rede;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.shune.xenthor_racas.CuraCelestial;
import net.shune.xenthor_racas.FormaNegra;
import net.shune.xenthor_racas.VooCelestial;
import net.shune.xenthor_racas.cliente.ManipuladorPacoteCliente;

public class RedeXenthor {

    public static void registrar(IEventBus barramentoMod) {
        barramentoMod.addListener(RedeXenthor::aoRegistrarPayloads);
    }

    private static void aoRegistrarPayloads(RegisterPayloadHandlersEvent evento) {
        PayloadRegistrar reg = evento.registrar("1.0.0");

        reg.playToClient(PacoteClasseEscolhida.TIPO,   PacoteClasseEscolhida.CODEC,   ManipuladorPacoteCliente::aoReceberClasseEscolhida);
        reg.playToClient(PacoteFormaNegra.TIPO,        PacoteFormaNegra.CODEC,        ManipuladorPacoteCliente::aoReceberFormaNegra);
        reg.playToClient(PacoteVooCelestial.TIPO,      PacoteVooCelestial.CODEC,      ManipuladorPacoteCliente::aoReceberVooCelestial);

        reg.playToServer(PacoteAtivarFormaNegra.TIPO,  PacoteAtivarFormaNegra.CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer j) FormaNegra.tentar(j);
            }));

        reg.playToServer(PacoteAtivarCuraCelestial.TIPO, PacoteAtivarCuraCelestial.CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer j) CuraCelestial.tentar(j);
            }));

        reg.playToServer(PacoteAtivarVooCelestial.TIPO, PacoteAtivarVooCelestial.CODEC,
            (pkt, ctx) -> ctx.enqueueWork(() -> {
                if (ctx.player() instanceof ServerPlayer j) VooCelestial.alternar(j);
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
}
