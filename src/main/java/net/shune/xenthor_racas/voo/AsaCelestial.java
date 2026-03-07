package net.shune.xenthor_racas.voo;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public class AsaCelestial extends ElytraItem {

    public AsaCelestial(Properties propriedades) {
        super(propriedades
                .durability(0)
                .stacksTo(1));
    }

    @Override
    public boolean isValidRepairItem(ItemStack pilha, ItemStack material) {
        return false;
    }

    @Override
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return true;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.CHEST;
    }

    public boolean canBeHurtBy(net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }
}