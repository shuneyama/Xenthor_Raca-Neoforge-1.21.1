package net.shune.xenthor_racas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.List;

public class ComandoAnalise {

    public static void registrar(CommandDispatcher<CommandSourceStack> despachante, String raiz) {
        despachante.register(
                Commands.literal(raiz)
                        .requires(origem -> origem.hasPermission(2))
                        .then(Commands.literal("analise")
                                .then(Commands.literal("racas")
                                        .executes(ComandoAnalise::executarAnaliseRacas)
                                )
                                .then(Commands.literal("classes")
                                        .executes(ComandoAnalise::executarAnaliseClasses)
                                )
                                .then(Commands.literal("elementos")
                                        .executes(ComandoAnalise::executarAnaliseElementos)
                                )
                        )
                        .then(Commands.literal("reset")
                                .then(Commands.literal("raca")
                                        .executes(ctx -> executarResetRaca(ctx, null))
                                        .then(Commands.argument("alvo", EntityArgument.players())
                                                .executes(ctx -> executarResetRaca(ctx, EntityArgument.getPlayers(ctx, "alvo")))
                                        )
                                )
                                .then(Commands.literal("cooldown")
                                        .executes(ctx -> executarResetCooldowns(ctx, null))
                                        .then(Commands.argument("alvo", EntityArgument.players())
                                                .executes(ctx -> executarResetCooldowns(ctx, EntityArgument.getPlayers(ctx, "alvo")))
                                        )
                                )
                                .then(Commands.literal("classe")
                                        .executes(ctx -> executarResetClasse(ctx, null))
                                        .then(Commands.argument("alvo", EntityArgument.players())
                                                .executes(ctx -> executarResetClasse(ctx, EntityArgument.getPlayers(ctx, "alvo")))
                                        )
                                )
                        )
        );
    }

    private static int executarAnaliseRacas(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        List<ServerPlayer> jogadores = origem.getServer().getPlayerList().getPlayers();

        if (jogadores.isEmpty()) {
            origem.sendSuccess(() -> Component.literal("Nenhum jogador online.").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        origem.sendSuccess(() -> Component.literal("═══ Racas dos Jogadores ═══").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        for (ServerPlayer jogador : jogadores) {
            String racaId = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
            Raca raca = Raca.porId(racaId);
            String nomeRaca = raca != null ? raca.id : "nenhuma";

            MutableComponent linha = Component.literal(jogador.getName().getString() + " - ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(nomeRaca).withStyle(corDaRaca(raca)));

            origem.sendSuccess(() -> linha, false);
        }

        return jogadores.size();
    }

    private static int executarAnaliseClasses(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        List<ServerPlayer> jogadores = origem.getServer().getPlayerList().getPlayers();

        if (jogadores.isEmpty()) {
            origem.sendSuccess(() -> Component.literal("Nenhum jogador online.").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        origem.sendSuccess(() -> Component.literal("═══ Classes dos Jogadores ═══").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        for (ServerPlayer jogador : jogadores) {
            String classeId = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
            String elementoId = jogador.getPersistentData().getString(ModPrincipal.TAG_ELEMENTO);
            ClasseRaca classe = ClasseRaca.porId(classeId);
            String nomeClasse = classe != null ? classe.id : "nenhuma";

            MutableComponent linha = Component.literal(jogador.getName().getString() + " - ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(nomeClasse).withStyle(corDaClasse(classe)));

            if (classe == ClasseRaca.MAGO && elementoId != null && !elementoId.isEmpty()) {
                linha = linha.append(Component.literal(" (" + elementoId + ")").withStyle(ChatFormatting.LIGHT_PURPLE));
            }

            MutableComponent linhaFinal = linha;
            origem.sendSuccess(() -> linhaFinal, false);
        }

        return jogadores.size();
    }

    private static int executarAnaliseElementos(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        List<ServerPlayer> jogadores = origem.getServer().getPlayerList().getPlayers();

        if (jogadores.isEmpty()) {
            origem.sendSuccess(() -> Component.literal("Nenhum jogador online.").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        origem.sendSuccess(() -> Component.literal("═══ Elementos dos Jogadores ═══").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD), false);

        for (ServerPlayer jogador : jogadores) {
            String classeId = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
            String elementoId = jogador.getPersistentData().getString(ModPrincipal.TAG_ELEMENTO);

            if (!ClasseRaca.MAGO.id.equals(classeId) && !ClasseRaca.GUERREIRO_MAGICO.id.equals(classeId)) continue;

            String nomeElemento = (elementoId != null && !elementoId.isEmpty()) ? elementoId : "sem elemento";
            MutableComponent linha = Component.literal(jogador.getName().getString() + " - ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(nomeElemento).withStyle(ChatFormatting.LIGHT_PURPLE));
            origem.sendSuccess(() -> linha, false);
        }

        return 1;
    }

    private static Collection<ServerPlayer> resolverAlvos(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> alvos) {
        if (alvos != null && !alvos.isEmpty()) return alvos;
        try {
            ServerPlayer self = ctx.getSource().getPlayerOrException();
            return List.of(self);
        } catch (Exception e) {
            return List.of();
        }
    }

    private static int executarResetRaca(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> alvosArg) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        Collection<ServerPlayer> alvos = resolverAlvos(ctx, alvosArg);
        if (alvos.isEmpty()) { origem.sendFailure(Component.literal("Nenhum alvo encontrado.").withStyle(ChatFormatting.RED)); return 0; }

        for (ServerPlayer jogador : alvos) {
            String racaAnterior = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);

            if (PoderTransformacao.estaTransformado(jogador)) PoderTransformacao.desativar(jogador);
            if (VooCelestial.estaAtivo(jogador)) VooCelestial.alternar(jogador);
            if (FormaNegra.estaAtiva(jogador)) FormaNegra.desativar(jogador, false);

            Raca racaObj = Raca.porId(racaAnterior);
            if (racaObj != null) {
                switch (racaObj) {
                    case CELESTIAL   -> MonitorCelestial.removerEquipeGlowing(jogador);
                    case CORROMPIDO  -> MonitorCorrompido.removerEquipeGlowing(jogador);
                    case TRITAO      -> MonitorTritao.removerEquipeGlowing(jogador);
                    case FADA        -> MonitorFada.removerEquipeGlowing(jogador);
                    case ANDROID     -> MonitorAndroid.removerEquipeGlowing(jogador);
                    case DRAGONIC    -> MonitorDragonic.removerEquipeGlowing(jogador);
                    case MORTO_VIVO  -> MonitorMortoVivo.removerEquipeGlowing(jogador);
                    case VAMPIRO     -> MonitorVampiro.removerEquipeGlowing(jogador);
                    case DAMPIRO     -> MonitorVampiro.removerEquipeGlowing(jogador);
                    case AMALDICOADO -> MonitorAmaldicoado.removerEquipeGlowing(jogador);
                    case ESPIRITO    -> MonitorEspirito.removerEquipeGlowing(jogador);
                    default -> {}
                }
            }

            AtributosRaca.removerTodosOsModificadores(jogador);
            EscalaJogador.aplicarEscala(jogador, Raca.HUMANO);
            jogador.getPersistentData().putString(ModPrincipal.TAG_RACA, Raca.HUMANO.id);
            net.shune.xenthor_racas.rede.RedeXenthor.enviarRaca(jogador, Raca.HUMANO.id);
            jogador.removeAllEffects();

            origem.sendSuccess(() -> Component.literal("Raça de " + jogador.getName().getString() + " resetada para Humano.")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return alvos.size();
    }

    private static int executarResetCooldowns(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> alvosArg) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        Collection<ServerPlayer> alvos = resolverAlvos(ctx, alvosArg);
        if (alvos.isEmpty()) { origem.sendFailure(Component.literal("Nenhum alvo encontrado.").withStyle(ChatFormatting.RED)); return 0; }

        for (ServerPlayer jogador : alvos) {
            var dados = jogador.getPersistentData();
            dados.remove(ModPrincipal.ID_MOD + ":deteccao_cooldown");
            dados.remove(ModPrincipal.ID_MOD + ":invocacao_cooldown");
            dados.remove(ModPrincipal.ID_MOD + ":celestial_cura_cooldown");
            dados.remove(ModPrincipal.ID_MOD + ":celestial_impulso_cd");
            dados.remove(ModPrincipal.ID_MOD + ":forma_negra_cooldown");
            dados.remove(ModPrincipal.ID_MOD + ":espirito_espectral_cd");
            dados.remove(ModPrincipal.ID_MOD + ":espirito_espectral_fim");
            dados.remove(ModPrincipal.ID_MOD + ":vampiro_regen_block");
            dados.remove(ModPrincipal.ID_MOD + ":dampiro_regen_block");
            dados.remove(ModPrincipal.ID_MOD + ":lobisomem_regen_block");
            dados.remove(ModPrincipal.ID_MOD + ":amaldicoado_prox_buff");

            origem.sendSuccess(() -> Component.literal("Cooldowns de " + jogador.getName().getString() + " resetados.")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return alvos.size();
    }

    private static int executarResetClasse(CommandContext<CommandSourceStack> ctx, Collection<ServerPlayer> alvosArg) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        Collection<ServerPlayer> alvos = resolverAlvos(ctx, alvosArg);
        if (alvos.isEmpty()) { origem.sendFailure(Component.literal("Nenhum alvo encontrado.").withStyle(ChatFormatting.RED)); return 0; }

        for (ServerPlayer jogador : alvos) {
            AtributosClasse.removerTodosOsModificadores(jogador);
            jogador.getPersistentData().remove(ModPrincipal.TAG_CLASSE);
            jogador.getPersistentData().remove(ModPrincipal.TAG_ELEMENTO);
            jogador.getPersistentData().remove(ModPrincipal.TAG_AGUARDANDO_ELEMENTO);

            origem.sendSuccess(() -> Component.literal("Classe de " + jogador.getName().getString() + " removida.")
                    .withStyle(ChatFormatting.GREEN), true);
        }
        return alvos.size();
    }

    private static ChatFormatting corDaRaca(Raca raca) {
        if (raca == null) return ChatFormatting.GRAY;
        return switch (raca) {
            case HUMANO        -> ChatFormatting.WHITE;
            case ELFO_NATURAL  -> ChatFormatting.GREEN;
            case ELFO_NEGRO    -> ChatFormatting.DARK_PURPLE;
            case ANAO          -> ChatFormatting.GOLD;
            case CELESTIAL     -> ChatFormatting.YELLOW;
            case CORROMPIDO    -> ChatFormatting.DARK_RED;
            case BESTIAL       -> ChatFormatting.RED;
            case BESTIAL_AEREO -> ChatFormatting.AQUA;
            case TRITAO        -> ChatFormatting.DARK_AQUA;
            case FADA          -> ChatFormatting.LIGHT_PURPLE;
            case OGRO          -> ChatFormatting.DARK_GREEN;
            case ANDROID       -> ChatFormatting.BLUE;
            case DRAGONIC      -> ChatFormatting.DARK_PURPLE;
            case MORTO_VIVO    -> ChatFormatting.DARK_GRAY;
            case VAMPIRO       -> ChatFormatting.RED;
            case DAMPIRO       -> ChatFormatting.DARK_RED;
            case LOBISOMEM     -> ChatFormatting.GRAY;
            case AMALDICOADO   -> ChatFormatting.DARK_GRAY;
            case ESPIRITO      -> ChatFormatting.BLACK;
            case KITSUNE       -> ChatFormatting.GOLD;
        };
    }

    private static ChatFormatting corDaClasse(ClasseRaca classe) {
        if (classe == null) return ChatFormatting.GRAY;
        return switch (classe) {
            case GUERREIRO        -> ChatFormatting.RED;
            case GUERREIRO_MAGICO -> ChatFormatting.GOLD;
            case MAGO             -> ChatFormatting.LIGHT_PURPLE;
        };
    }
}