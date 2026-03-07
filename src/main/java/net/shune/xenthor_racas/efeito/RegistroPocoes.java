package net.shune.xenthor_racas.efeito;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.shune.xenthor_racas.ModPrincipal;

public class RegistroPocoes {

    public static final DeferredRegister<Potion> POCOES =
            DeferredRegister.create(Registries.POTION, ModPrincipal.ID_MOD);

    public static final DeferredHolder<Potion, Potion> RESPIRACAO_TERRESTRE_1 =
            POCOES.register("respiracao_terrestre", () ->
                    new Potion(new MobEffectInstance(
                            RegistroEfeitos.RESPIRACAO_TERRESTRE, 20 * 60 * 5, 0)));

    public static final DeferredHolder<Potion, Potion> RESPIRACAO_TERRESTRE_2 =
            POCOES.register("respiracao_terrestre_forte", () ->
                    new Potion(new MobEffectInstance(
                            RegistroEfeitos.RESPIRACAO_TERRESTRE, 20 * 60 * 8, 1)));

    public static final DeferredHolder<Potion, Potion> RESPIRACAO_TERRESTRE_3 =
            POCOES.register("respiracao_terrestre_suprema", () ->
                    new Potion(new MobEffectInstance(
                            RegistroEfeitos.RESPIRACAO_TERRESTRE, 20 * 60 * 30, 2)));

    public static void registrar(IEventBus barramentoMod) {
        POCOES.register(barramentoMod);
    }
}