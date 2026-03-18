package net.shune.xenthor_racas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.shune.xenthor_racas.rede.RedeXenthor;

import java.util.Collection;

public class ComandoRaca {

    private static final SuggestionProvider<CommandSourceStack> SUGESTOES_RACA =
            (ctx, builder) -> {
                for (Raca r : Raca.values())
                    builder.suggest(r.id);
                return builder.buildFuture();
            };

    public static void registrar(CommandDispatcher<CommandSourceStack> despachante) {
        despachante.register(
                Commands.literal("racas")
                        .requires(CommandSourceStack::isPlayer)
                        .then(Commands.argument("raca", StringArgumentType.word())
                                .suggests(SUGESTOES_RACA)
                                .executes(ComandoRaca::executarProprio)
                                .then(Commands.argument("alvos", EntityArgument.players())
                                        .requires(origem -> origem.hasPermission(2))
                                        .executes(ComandoRaca::executarOutro)
                                )
                        )
        );
    }

    private static int executarProprio(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();

        ServerPlayer jogador;
        try {
            jogador = origem.getPlayerOrException();
        } catch (Exception e) {
            origem.sendFailure(Component.literal("Este comando deve ser usado por um jogador."));
            return 0;
        }

        String argRaca = StringArgumentType.getString(ctx, "raca").toLowerCase();
        Raca racaEscolhida = Raca.porId(argRaca);
        if (racaEscolhida == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.raca_desconhecida", argRaca));
            return 0;
        }

        aplicarParaJogador(jogador, racaEscolhida, origem);
        return 1;
    }

    private static int executarOutro(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();

        String argRaca = StringArgumentType.getString(ctx, "raca").toLowerCase();
        Raca racaEscolhida = Raca.porId(argRaca);
        if (racaEscolhida == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.raca_desconhecida", argRaca));
            return 0;
        }

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            aplicarParaJogador(jogador, racaEscolhida, origem);
            afetados++;
        }
        return afetados;
    }

    private static void aplicarParaJogador(ServerPlayer jogador, Raca racaEscolhida, CommandSourceStack origem) {
        String racaAnterior = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        removerGlowingAnterior(jogador, racaAnterior);

        if (PoderTransformacao.estaTransformado(jogador)) PoderTransformacao.desativar(jogador);
        if (VooCelestial.estaAtivo(jogador)) VooCelestial.alternar(jogador);

        AtributosRaca.aplicarRaca(jogador, racaEscolhida);
        EscalaJogador.aplicarEscala(jogador, racaEscolhida);
        jogador.getPersistentData().putString(ModPrincipal.TAG_RACA, racaEscolhida.id);
        aplicarGlowing(jogador, racaEscolhida);

        final Raca racaFinal = racaEscolhida;
        origem.sendSuccess(() ->
                        Component.translatable("comando.xenthor_racas.raca_aplicada",
                                jogador.getDisplayName(),
                                Component.translatable("raca.xenthor_racas." + racaFinal.id)),
                true);
        RedeXenthor.enviarRaca(jogador, racaEscolhida.id);
    }

    private static void removerGlowingAnterior(ServerPlayer jogador, String racaId) {
        Raca raca = Raca.porId(racaId);
        if (raca == null) return;
        switch (raca) {
            case CELESTIAL  -> MonitorCelestial.removerEquipeGlowing(jogador);
            case CORROMPIDO -> MonitorCorrompido.removerEquipeGlowing(jogador);
            case TRITAO     -> MonitorTritao.removerEquipeGlowing(jogador);
            case FADA       -> MonitorFada.removerEquipeGlowing(jogador);
            case ANDROID    -> MonitorAndroid.removerEquipeGlowing(jogador);
            case DRAGONIC   -> MonitorDragonic.removerEquipeGlowing(jogador);
            case MORTO_VIVO -> MonitorMortoVivo.removerEquipeGlowing(jogador);
            case VAMPIRO      -> MonitorVampiro.removerEquipeGlowing(jogador);
            case DAMPIRO      -> MonitorVampiro.removerEquipeGlowing(jogador);
            case AMALDICOADO  -> MonitorAmaldicoado.removerEquipeGlowing(jogador);
            case ESPIRITO     -> MonitorEspirito.removerEquipeGlowing(jogador);
            default -> {}
        }
    }

    private static void aplicarGlowing(ServerPlayer jogador, Raca raca) {
        switch (raca) {
            case CELESTIAL  -> MonitorCelestial.aplicarEquipeGlowing(jogador);
            case CORROMPIDO -> MonitorCorrompido.aplicarEquipeGlowing(jogador);
            case TRITAO     -> MonitorTritao.aplicarEquipeGlowing(jogador);
            case FADA       -> MonitorFada.aplicarEquipeGlowing(jogador);
            case ANDROID    -> MonitorAndroid.aplicarEquipeGlowing(jogador);
            case DRAGONIC   -> MonitorDragonic.aplicarEquipeGlowing(jogador);
            case MORTO_VIVO -> MonitorMortoVivo.aplicarEquipeGlowing(jogador);
            case VAMPIRO      -> MonitorVampiro.aplicarEquipeGlowing(jogador);
            case DAMPIRO      -> MonitorVampiro.aplicarEquipeGlowing(jogador);
            case AMALDICOADO  -> MonitorAmaldicoado.aplicarEquipeGlowing(jogador);
            case ESPIRITO     -> MonitorEspirito.aplicarEquipeGlowing(jogador);
            default -> {}
        }
    }
}