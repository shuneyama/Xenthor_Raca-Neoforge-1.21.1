package net.shune.xenthor_racas;

import io.redspace.ironsspellbooks.api.events.SpellPreCastEvent;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Set;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class BloqueadorMagia {

    private static final Set<Raca> RACAS_BLOQUEADAS_HOLY = Set.of(
            Raca.ANAO,
            Raca.CORROMPIDO,
            Raca.OGRO,
            Raca.ANDROID
    );

    @SubscribeEvent
    public static void aoPreCastarMagia(SpellPreCastEvent evento) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return;
        if (!(evento.getEntity() instanceof ServerPlayer jogador)) return;

        String classeId = jogador.getPersistentData().getString(ModPrincipal.TAG_CLASSE);
        if (ClasseRaca.GUERREIRO.id.equals(classeId)) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Guerreiros não podem usar magias!")
                    .withStyle(ChatFormatting.RED));
            return;
        }

        String racaSalva = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        Raca raca = Raca.porId(racaSalva);
        if (raca == null || !RACAS_BLOQUEADAS_HOLY.contains(raca)) return;

        if (SchoolRegistry.HOLY_RESOURCE.equals(evento.getSchoolType().getId())) {
            evento.setCanceled(true);
            jogador.sendSystemMessage(Component.literal("Sua raça não pode usar magias sagradas!")
                    .withStyle(ChatFormatting.RED));
        }
    }
}