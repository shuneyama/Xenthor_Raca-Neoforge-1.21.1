package net.shune.xenthor_racas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class ComandoAnalise {

    private static final SuggestionProvider<CommandSourceStack> SUGESTOES_RACA =
            (ctx, builder) -> {
                for (Raca r : Raca.values())
                    builder.suggest(r.id);
                return builder.buildFuture();
            };

    private static final SuggestionProvider<CommandSourceStack> SUGESTOES_CLASSE =
            (ctx, builder) -> {
                for (ClasseRaca c : ClasseRaca.values())
                    builder.suggest(c.id);
                return builder.buildFuture();
            };

    public static void registrar(CommandDispatcher<CommandSourceStack> despachante, String raiz) {
        despachante.register(
                Commands.literal(raiz)
                        .requires(origem -> origem.hasPermission(2))
                        .then(Commands.literal("racas")
                                .then(Commands.literal("analise")
                                        .executes(ComandoAnalise::executarAnaliseRacas)
                                )
                                .then(Commands.argument("raca", StringArgumentType.word())
                                        .suggests(SUGESTOES_RACA)
                                        .executes(ComandoAnalise::executarFiltroRaca)
                                )
                        )
                        .then(Commands.literal("classes")
                                .then(Commands.literal("analise")
                                        .executes(ComandoAnalise::executarAnaliseClasses)
                                )
                                .then(Commands.argument("classe", StringArgumentType.word())
                                        .suggests(SUGESTOES_CLASSE)
                                        .executes(ComandoAnalise::executarFiltroClasse)
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

    private static int executarFiltroRaca(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        String argRaca = StringArgumentType.getString(ctx, "raca").toLowerCase();
        Raca racaFiltro = Raca.porId(argRaca);

        if (racaFiltro == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.raca_desconhecida", argRaca));
            return 0;
        }

        List<ServerPlayer> jogadores = origem.getServer().getPlayerList().getPlayers();
        List<ServerPlayer> filtrados = new ArrayList<>();

        for (ServerPlayer jogador : jogadores) {
            String racaId = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
            if (racaFiltro.id.equals(racaId)) {
                filtrados.add(jogador);
            }
        }

        ChatFormatting cor = corDaRaca(racaFiltro);

        origem.sendSuccess(() -> Component.literal("═══ Jogadores com raca: ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(racaFiltro.id).withStyle(cor))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)), false);

        if (filtrados.isEmpty()) {
            origem.sendSuccess(() -> Component.literal("Nenhum jogador com essa raca.").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        for (ServerPlayer jogador : filtrados) {
            origem.sendSuccess(() -> Component.literal("  " + jogador.getName().getString()).withStyle(ChatFormatting.WHITE), false);
        }

        return filtrados.size();
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

    private static int executarFiltroClasse(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        String argClasse = StringArgumentType.getString(ctx, "classe").toLowerCase();
        ClasseRaca classeFiltro = ClasseRaca.porId(argClasse);

        if (classeFiltro == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.classe_desconhecida", argClasse));
            return 0;
        }

        List<ServerPlayer> jogadores = origem.getServer().getPlayerList().getPlayers();
        List<ServerPlayer> filtrados = new ArrayList<>();

        for (ServerPlayer jogador : jogadores) {
            String classeId = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
            if (classeFiltro.id.equals(classeId)) {
                filtrados.add(jogador);
            }
        }

        ChatFormatting cor = corDaClasse(classeFiltro);

        origem.sendSuccess(() -> Component.literal("═══ Jogadores com classe: ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                .append(Component.literal(classeFiltro.id).withStyle(cor))
                .append(Component.literal(" ═══").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)), false);

        if (filtrados.isEmpty()) {
            origem.sendSuccess(() -> Component.literal("Nenhum jogador com essa classe.").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        for (ServerPlayer jogador : filtrados) {
            String elementoId = jogador.getPersistentData().getString(ModPrincipal.TAG_ELEMENTO);
            MutableComponent linha = Component.literal("  " + jogador.getName().getString()).withStyle(ChatFormatting.WHITE);
            if (classeFiltro == ClasseRaca.MAGO && elementoId != null && !elementoId.isEmpty()) {
                linha = linha.append(Component.literal(" (" + elementoId + ")").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
            MutableComponent linhaFinal = linha;
            origem.sendSuccess(() -> linhaFinal, false);
        }

        return filtrados.size();
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