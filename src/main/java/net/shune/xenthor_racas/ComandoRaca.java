package net.shune.xenthor_racas;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

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
            Commands.literal("raca")
                .requires(origem -> origem.hasPermission(2))
                .then(Commands.argument("alvos", EntityArgument.players())
                    .then(Commands.argument("raca", StringArgumentType.word())
                        .suggests(SUGESTOES_RACA)
                        .executes(ComandoRaca::executar)
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

        String argRaca = StringArgumentType.getString(ctx, "raca").toLowerCase();
        Raca racaEscolhida = Raca.porId(argRaca);

        if (racaEscolhida == null) {
            origem.sendFailure(Component.translatable("comando.xenthor_racas.raca_desconhecida", argRaca));
            return 0;
        }

        int afetados = 0;
        for (ServerPlayer jogador : alvos) {
            AtributosRaca.aplicarRaca(jogador, racaEscolhida);
            EscalaJogador.aplicarEscala(jogador, racaEscolhida);
            jogador.getPersistentData().putString(ModPrincipal.TAG_RACA, racaEscolhida.id);

            final Raca racaFinal = racaEscolhida;
            origem.sendSuccess(() ->
                Component.translatable("comando.xenthor_racas.raca_aplicada",
                    jogador.getDisplayName(),
                    Component.translatable("raca.xenthor_racas." + racaFinal.id)),
                true);
            afetados++;
        }

        return afetados;
    }
}
