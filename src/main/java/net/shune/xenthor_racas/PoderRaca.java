package net.shune.xenthor_racas;

import net.minecraft.server.level.ServerPlayer;

public class PoderRaca {

    public static void ativarPrimario(ServerPlayer jogador) {
        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        if (raca == null) return;

        switch (raca) {
            case ELFO_NEGRO                                        -> FormaNegra.tentar(jogador);
            case CELESTIAL, BESTIAL_AEREO, FADA, DRAGONIC, VAMPIRO, DAMPIRO -> VooCelestial.alternar(jogador);
            case BESTIAL, ANDROID                                  -> PoderDeteccao.tentar(jogador);
            case MORTO_VIVO                                        -> PoderInvocacao.tentar(jogador);
            case LOBISOMEM                                         -> PoderDeteccao.tentar(jogador);
            case ESPIRITO                                          -> PoderEspirito.tentar(jogador);
            default -> {}
        }
    }

    public static void ativarSecundario(ServerPlayer jogador) {
        Raca raca = Raca.porId(jogador.getPersistentData().getString(ModPrincipal.TAG_RACA));
        if (raca == null) return;

        switch (raca) {
            case CELESTIAL     -> CuraCelestial.tentar(jogador);
            case BESTIAL_AEREO -> PoderDeteccao.tentar(jogador);
            case VAMPIRO       -> PoderDeteccao.tentar(jogador);
            case DAMPIRO       -> PoderDeteccao.tentar(jogador);
            default -> {}
        }
    }
}