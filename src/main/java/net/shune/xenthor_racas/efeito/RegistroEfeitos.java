package net.shune.xenthor_racas.efeito;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.shune.xenthor_racas.ModPrincipal;

public class RegistroEfeitos {

    public static final DeferredRegister<MobEffect> EFEITOS =
            DeferredRegister.create(Registries.MOB_EFFECT, ModPrincipal.ID_MOD);

    public static final DeferredHolder<MobEffect, EfeitoRespiracaoTerrestre> RESPIRACAO_TERRESTRE =
            EFEITOS.register("respiracao_terrestre", EfeitoRespiracaoTerrestre::new);

    public static void registrar(IEventBus barramentoMod) {
        EFEITOS.register(barramentoMod);
    }
}