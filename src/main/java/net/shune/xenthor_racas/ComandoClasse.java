package net.shune.xenthor_racas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
                .then(Commands.argument("alvos", EntityArgument.players())
                    .then(Commands.argument("classe", StringArgumentType.word())
                        .suggests(SUGESTOES_CLASSE)
                        .executes(ComandoClasse::executar)
                    )
                )
        );

        despachante.register(
            Commands.literal("classes_elemento")
                .requires(origem -> origem.isPlayer() && aguardandoElemento(origem))
                .then(Commands.argument("elemento", StringArgumentType.word())
                    .suggests(SUGESTOES_ELEMENTO)
                    .executes(ComandoClasse::executarElemento)
                )
        );

        registrarReset(despachante, "shune");
        registrarReset(despachante, "xenthor");
    }

    private static void registrarReset(CommandDispatcher<CommandSourceStack> despachante, String raiz) {
        despachante.register(
                Commands.literal(raiz)
                        .requires(origem -> origem.hasPermission(2))
                        .then(Commands.literal("classes")
                                .then(Commands.literal("resetar")
                                        .then(Commands.argument("alvos", EntityArgument.players())
                                                .executes(ComandoClasse::executarReset)
                                        )
                                )
                        )
        );
    }

    private static int executar(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack origem = ctx.getSource();

        Collection<ServerPlayer> alvos;
        try {
            alvos = EntityArgument.getPlayers(ctx, "alvos");
        } catch (Exception e) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.sem_alvos"));
            return 0;
        }

        String argClasse = StringArgumentType.getString(ctx, "classe").toLowerCase();
        int afetados = 0;

        for (ServerPlayer jogador : alvos) {
            if (jogadorJaTemClasse(jogador)) {
                jogador.sendSystemMessage(
                    Component.translatable("comando.xenthor_racas.classe_ja_escolhida")
                        .withStyle(ChatFormatting.RED));
                continue;
            }

            ClasseRaca classeEscolhida = ClasseRaca.porId(argClasse);
            if (classeEscolhida == null) {
                origem.sendFailure(Component.translatable("comando.xenthor_racas.classe_desconhecida", argClasse));
                return 0;
            }

            if (classeEscolhida == ClasseRaca.MAGO) {
                jogador.getPersistentData().putBoolean(ModPrincipal.TAG_AGUARDANDO_ELEMENTO, true);
                enviarMenuElementos(jogador);
                return 1;
            }

            aplicarEConfirmar(jogador, classeEscolhida);
            afetados++;
        }

        return afetados;
    }

    private static int executarElemento(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack origem = ctx.getSource();
        if (!(origem.getEntity() instanceof ServerPlayer jogador)) return 0;

        if (!jogador.getPersistentData().getBoolean(ModPrincipal.TAG_AGUARDANDO_ELEMENTO)) {
            jogador.sendSystemMessage(
                Component.literal("Voce nao esta aguardando escolha de elemento.")
                    .withStyle(ChatFormatting.RED));
            return 0;
        }

        String argElemento = StringArgumentType.getString(ctx, "elemento").toLowerCase();
        ElementoMago elemento = ElementoMago.porId(argElemento);

        if (elemento == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.elemento_desconhecido", argElemento));
            return 0;
        }

        AtributosClasse.aplicarMagoComElemento(jogador, elemento);
        jogador.getPersistentData().putString(ModPrincipal.TAG_CLASSE, ClasseRaca.MAGO.id);
        jogador.getPersistentData().putString(ModPrincipal.TAG_ELEMENTO, elemento.id);
        jogador.getPersistentData().remove(ModPrincipal.TAG_AGUARDANDO_ELEMENTO);

        RedeXenthor.enviarParaJogador(jogador, ClasseRaca.MAGO.id, elemento.id);

        final ElementoMago elemFinal = elemento;
        origem.sendSuccess(() ->
            Component.translatable("comando.xenthor_racas.mago_elemento_aplicado",
                jogador.getDisplayName(),
                Component.translatable("elemento.xenthor_racas." + elemFinal.id)),
            true);

        return 1;
    }

    private static int executarReset(CommandContext<CommandSourceStack> ctx) {
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

    private static void enviarMenuElementos(ServerPlayer jogador) {
        jogador.sendSystemMessage(Component.translatable("comando.xenthor_racas.escolha_elemento")
            .withStyle(ChatFormatting.GOLD));

        MutableComponent linha = Component.empty();
        ElementoMago[] elementos = ElementoMago.values();

        for (int i = 0; i < elementos.length; i++) {
            ElementoMago e = elementos[i];
            MutableComponent botao = Component.literal("[" + capitalize(e.id) + "]")
                .withStyle(style -> style
                    .withColor(corDoElemento(e))
                    .withClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/classes_elemento " + e.id
                    ))
                );

            linha = linha.append(botao);
            if (i < elementos.length - 1)
                linha = linha.append(Component.literal(" "));

            if ((i + 1) % 5 == 0 && i < elementos.length - 1) {
                jogador.sendSystemMessage(linha);
                linha = Component.empty();
            }
        }

        if (!linha.getSiblings().isEmpty())
            jogador.sendSystemMessage(linha);
    }

    private static boolean jogadorJaTemClasse(ServerPlayer jogador) {
        String classeSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
        return classeSalva != null && !classeSalva.isEmpty();
    }

    private static boolean aguardandoElemento(CommandSourceStack origem) {
        if (!(origem.getEntity() instanceof ServerPlayer jogador)) return false;
        return jogador.getPersistentData().getBoolean(ModPrincipal.TAG_AGUARDANDO_ELEMENTO);
    }

    private static int corDoElemento(ElementoMago elemento) {
        return switch (elemento) {
            case FOGO     -> 0xFF4500;
            case GELO     -> 0xADD8E6;
            case AGUA     -> 0x1E90FF;
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
