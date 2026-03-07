package net.shune.xenthor_racas.mixin;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WalkAnimationState.class)
public interface AccessorWalkAnimationState {
    @Accessor("position")
    void xenthor_setPosition(float pos);

    @Accessor("speedOld")
    void xenthor_setSpeedOld(float speedOld);

    @Accessor("speedOld")
    float xenthor_getSpeedOld();
}