package net.shune.xenthor_racas.mixin;

import net.minecraft.world.entity.ambient.Bat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Bat.class)
public interface AccessorBat {
    @Invoker("setupAnimationStates")
    void xenthor_setupAnimationStates();
}