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

public class ComandoClasse {

    private static final SuggestionProvider<CommandSourceStack> SUGESTOES_CLASSE =
            (ctx, builder) -> {
                builder.suggest("guerreiro");
                builder.suggest("guerreiro_magico");
                builder.suggest("mago");
                return builder.buildFuture();
            };

    private static final SuggestionProvider<CommandSourceStack> SUGESTOES_ELEMENTO =
            (ctx, builder) -> {
                for (ElementoMago e : ElementoMago.values())
                    builder.suggest(e.id);
                return builder.buildFuture();
            };

    public static void registrar(CommandDispatcher<CommandSourceStack> despachante) {
        despachante.register(
                Commands.literal("classes")
                        .requires(CommandSourceStack::isPlayer)
                        .then(Commands.argument("classe", StringArgumentType.word())
                                .suggests(SUGESTOES_CLASSE)
                                .executes(ComandoClasse::executarProprio)
                                .then(Commands.argument("alvos", EntityArgument.players())
                                        .requires(origem -> origem.hasPermission(2))
                                        .executes(ComandoClasse::executarOutro)
                                )
                        )
        );
    }

    public static void registrarElemento(CommandDispatcher<CommandSourceStack> despachante) {
        despachante.register(
                Commands.literal("elemento")
                        .requires(origem -> origem.hasPermission(2))
                        .then(Commands.argument("alvos", EntityArgument.players())
                                .executes(ComandoClasse::executarRemoverElemento)
                                .then(Commands.argument("elemento", StringArgumentType.word())
                                        .suggests(SUGESTOES_ELEMENTO)
                                        .executes(ComandoClasse::executarDefinirElemento)
                                )
                        )
        );
    }
    private static int executarProprio(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        if (!(origem.getEntity() instanceof ServerPlayer jogador)) return 0;

        if (jogadorJaTemClasse(jogador)) {
            jogador.sendSystemMessage(
                    Component.translatable("comando.xenthor_racas.classe_ja_escolhida")
                            .withStyle(ChatFormatting.RED));
            return 0;
        }

        String argClasse = StringArgumentType.getString(ctx, "classe").toLowerCase();
        ClasseRaca classeEscolhida = ClasseRaca.porId(argClasse);
        if (classeEscolhida == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.classe_desconhecida", argClasse));
            return 0;
        }

        aplicarEConfirmar(jogador, classeEscolhida);

        if (classeEscolhida == ClasseRaca.MAGO) {
            jogador.sendSystemMessage(Component.literal("Você é um Mago sem elemento definido.")
                    .withStyle(ChatFormatting.GRAY));
            jogador.sendSystemMessage(Component.literal("Aguarde um administrador definir seu elemento.")
                    .withStyle(ChatFormatting.GRAY));
        }

        return 1;
    }

    private static int executarOutro(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();
        String argClasse = StringArgumentType.getString(ctx, "classe").toLowerCase();

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        ClasseRaca classeEscolhida = ClasseRaca.porId(argClasse);
        if (classeEscolhida == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.classe_desconhecida", argClasse));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            aplicarEConfirmar(jogador, classeEscolhida);

            if (classeEscolhida == ClasseRaca.MAGO) {
                jogador.sendSystemMessage(Component.literal("Você é um Mago sem elemento definido.")
                        .withStyle(ChatFormatting.GRAY));
                jogador.sendSystemMessage(Component.literal("Aguarde um administrador definir seu elemento.")
                        .withStyle(ChatFormatting.GRAY));
            }

            afetados++;
        }

        return afetados;
    }

    private static int executarDefinirElemento(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        String argElemento = StringArgumentType.getString(ctx, "elemento").toLowerCase();
        ElementoMago elemento = ElementoMago.porId(argElemento);

        if (elemento == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.elemento_desconhecido", argElemento));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            String classeSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);

            if (!ClasseRaca.MAGO.id.equals(classeSalva) && !ClasseRaca.GUERREIRO_MAGICO.id.equals(classeSalva)) {
                origem.sendFailure(Component.literal(jogador.getName().getString() + " não é Mago nem Guerreiro Mágico!")
                        .withStyle(ChatFormatting.RED));
                continue;
            }

            AtributosClasse.aplicarElemento(jogador, elemento);
            jogador.getPersistentData().putString(ModPrincipal.TAG_ELEMENTO, elemento.id);

            RedeXenthor.enviarParaJogador(jogador, classeSalva, elemento.id);

            boolean ehGuerreiroMagico = ClasseRaca.GUERREIRO_MAGICO.id.equals(classeSalva);
            String nomeClasse = ehGuerreiroMagico ? "Guerreiro Mágico" : "Mago";

            jogador.sendSystemMessage(Component.literal("Seu elemento de " + nomeClasse + " foi definido como: " + capitalize(elemento.id))
                    .withStyle(ChatFormatting.GOLD));

            final ElementoMago elemFinal = elemento;
            final String nomeClasseFinal = nomeClasse;
            origem.sendSuccess(() ->
                            Component.literal(nomeClasseFinal + " (")
                                    .append(Component.translatable("elemento.xenthor_racas." + elemFinal.id))
                                    .append(") aplicado ao jogador ")
                                    .append(jogador.getDisplayName()),
                    true);

            afetados++;
        }

        return afetados;
    }

    private static int executarRemoverElemento(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            String classeSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);

            if (!ClasseRaca.MAGO.id.equals(classeSalva) && !ClasseRaca.GUERREIRO_MAGICO.id.equals(classeSalva)) {
                origem.sendFailure(Component.literal(jogador.getName().getString() + " não é Mago nem Guerreiro Mágico!")
                        .withStyle(ChatFormatting.RED));
                continue;
            }

            AtributosClasse.removerElementoMago(jogador);
            AtributosClasse.aplicarClasse(jogador, ClasseRaca.MAGO);
            jogador.getPersistentData().remove(ModPrincipal.TAG_ELEMENTO);

            RedeXenthor.enviarParaJogador(jogador, ClasseRaca.MAGO.id, "");

            jogador.sendSystemMessage(Component.literal("Seu elemento foi removido.")
                    .withStyle(ChatFormatting.GRAY));

            origem.sendSuccess(() ->
                            Component.literal("Elemento removido de " + jogador.getName().getString())
                                    .withStyle(ChatFormatting.GREEN),
                    true);

            afetados++;
        }

        return afetados;
    }

    private static int executarListaElementos(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack origem = ctx.getSource();

        origem.sendSuccess(() -> Component.literal("=== Elementos Disponiveis ===")
                .withStyle(ChatFormatting.GOLD), false);

        for (ElementoMago e : ElementoMago.values()) {
            origem.sendSuccess(() -> Component.literal(" - " + capitalize(e.id))
                    .withStyle(style -> style.withColor(corDoElemento(e))), false);
        }

        return 1;
    }

    private static int executarReset(CommandContext<CommandSourceStack> ctx) {
        if (!LicencaRacas.isLicencaValida()) return 0;
        CommandSourceStack origem = ctx.getSource();

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            AtributosClasse.removerTodosOsModificadores(jogador);
            jogador.getPersistentData().remove(ModPrincipal.TAG_CLASSE);
            jogador.getPersistentData().remove(ModPrincipal.TAG_ELEMENTO);
            jogador.getPersistentData().remove(ModPrincipal.TAG_AGUARDANDO_ELEMENTO);

            origem.sendSuccess(() ->
                            Component.translatable("comando.xenthor_racas.classe_removida",
                                            jogador.getDisplayName())
                                    .withStyle(ChatFormatting.GREEN),
                    true);
            afetados++;
        }

        return afetados;
    }

    private static void aplicarEConfirmar(ServerPlayer jogador, ClasseRaca classe) {
        AtributosClasse.aplicarClasse(jogador, classe);
        jogador.getPersistentData().putString(ModPrincipal.TAG_CLASSE, classe.id);
        jogador.getPersistentData().remove(ModPrincipal.TAG_ELEMENTO);
        RedeXenthor.enviarParaJogador(jogador, classe.id, "");
    }

    private static boolean jogadorJaTemClasse(ServerPlayer jogador) {
        String classeSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
        return classeSalva != null && !classeSalva.isEmpty();
    }

    private static int corDoElemento(ElementoMago elemento) {
        return switch (elemento) {
            case FOGO     -> 0xFF4500;
            case GELO     -> 0xADD8E6;
            case SANGUE   -> 0x8B0000;
            case ENDER    -> 0x7B2FBE;
            case ELDRITCH -> 0x2E0854;
            case HOLY     -> 0xFFD700;
            case NATUREZA -> 0x228B22;
            case RAIO     -> 0xFFFF00;
            case EVOCADOR -> 0xA9A9A9;
        };
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}