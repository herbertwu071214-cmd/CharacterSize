package hackcraft.character_size.mixin;

import hackcraft.character_size.IScaleHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IScaleHolder {
    @Unique
    private double characterSize$scale = 1.0;

    @Override
    public double characterSize$getScale() {
        return characterSize$scale;
    }

    @Override
    public void characterSize$setScale(double scale) {
        characterSize$scale = scale;
        ((PlayerEntity) (Object) this).calculateDimensions();
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readScale(ReadView view, CallbackInfo ci) {
        characterSize$scale = view.getDouble("character_size.scale", 1.0);
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeScale(WriteView view, CallbackInfo ci) {
        view.putDouble("character_size.scale", characterSize$scale);
    }

    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyBuildRange(CallbackInfoReturnable<Double> cir) {
        if (!((PlayerEntity) (Object) this).isSpectator()) {
            cir.setReturnValue(cir.getReturnValue() * characterSize$scale);
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyEntityInteractRange(CallbackInfoReturnable<Double> cir) {
        if (!((PlayerEntity) (Object) this).isSpectator()) {
            cir.setReturnValue(cir.getReturnValue() * characterSize$scale);
        }
    }
}
