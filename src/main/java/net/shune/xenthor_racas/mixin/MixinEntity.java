package net.shune.xenthor_racas.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.shune.xenthor_racas.ModPrincipal;
import net.shune.xenthor_racas.cliente.EspectralCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(method = "checkInsideBlocks", at = @At("HEAD"), cancellable = true)
    private void xenthor_ignorarColisaoBlocoEspectral(CallbackInfo ci) {
        if (xenthor_estaEspectral()) {
            ci.cancel();
        }
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    private void xenthor_semSufocamentoEspectral(CallbackInfoReturnable<Boolean> cir) {
        if (xenthor_estaEspectral()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private boolean xenthor_estaEspectral() {
        if (!((Object) this instanceof Player jogador)) return false;
        if (jogador.level().isClientSide) {
            return EspectralCache.estaEspectral(jogador.getUUID());
        }
        String racaId = jogador.getPersistentData().getString(ModPrincipal.TAG_RACA);
        if (!"espirito".equals(racaId)) return false;
        String tagEspectral = ModPrincipal.ID_MOD + ":espirito_espectral";
        return jogador.getPersistentData().getBoolean(tagEspectral);
    }
}