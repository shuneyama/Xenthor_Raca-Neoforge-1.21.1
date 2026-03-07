package net.shune.xenthor_racas.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.shune.xenthor_racas.voo.AsaCelestial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class MixinInventarioClick {

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void xenthor_bloquearRemoverAsa(int slotId, int button, ClickType clickType, Player jogador, CallbackInfo ci) {
        if (slotId < 0) return;

        AbstractContainerMenu menu = (AbstractContainerMenu)(Object)this;
        if (slotId >= menu.slots.size()) return;

        Slot slot = menu.slots.get(slotId);
        ItemStack itemNoSlot = slot.getItem();

        if (itemNoSlot.getItem() instanceof AsaCelestial) {
            ci.cancel();
        }
    }
}