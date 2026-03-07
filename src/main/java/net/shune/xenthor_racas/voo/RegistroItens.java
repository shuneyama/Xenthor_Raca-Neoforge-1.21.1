package net.shune.xenthor_racas.voo;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.shune.xenthor_racas.ModPrincipal;

import java.util.function.Supplier;

public class RegistroItens {

    public static final DeferredRegister<Item> ITENS =
            DeferredRegister.create(Registries.ITEM, ModPrincipal.ID_MOD);

    public static final Supplier<AsaCelestial> ASA_CELESTIAL =
            ITENS.register("asa_celestial", () -> new AsaCelestial(new Item.Properties()));

    public static void registrar(IEventBus barramentoMod) {
        ITENS.register(barramentoMod);
    }
}