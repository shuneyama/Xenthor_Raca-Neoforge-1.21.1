package net.shune.xenthor_racas.efeito;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.shune.xenthor_racas.ModPrincipal;

@EventBusSubscriber(modid = ModPrincipal.ID_MOD)
public class ReceitasPocoes {

    @SubscribeEvent
    public static void aoRegistrarReceitas(RegisterBrewingRecipesEvent evento) {
        var builder = evento.getBuilder();

        builder.addMix(Potions.AWKWARD, Items.PUFFERFISH, RegistroPocoes.RESPIRACAO_TERRESTRE_1);

        builder.addMix(RegistroPocoes.RESPIRACAO_TERRESTRE_1, Items.GLOWSTONE_DUST, RegistroPocoes.RESPIRACAO_TERRESTRE_2);
    }
}