package hackage.character_size.mixin;

import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import hackage.character_size.IScaleHolder;
import hackage.character_size.Character_size;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements IScaleHolder {
    private double scale = 1.0;

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = scale;
        PlayerEntity self = (PlayerEntity)(Object)this;
        self.calculateDimensions();
        if (!self.getWorld().isClient && self instanceof ServerPlayerEntity serverPlayer) {
            Character_size.updatePlayerAttributes(serverPlayer, scale);
        }
    }

    @Inject(method = "getAttackRange", at = @At("RETURN"), cancellable = true)
    private void modifyAttackRange(CallbackInfoReturnable<Double> cir) {
        if (!isSpectator()) {
            cir.setReturnValue(cir.getReturnValue() * scale);
        }
    }

    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyBuildRange(CallbackInfoReturnable<Double> cir) {
        if (!isSpectator()) {
            cir.setReturnValue(cir.getReturnValue() * scale);
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void modifyEntityInteractRange(CallbackInfoReturnable<Double> cir) {
        if (!isSpectator()) {
            cir.setReturnValue(cir.getReturnValue() * scale);
        }
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void modifyDimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        EntityDimensions original = cir.getReturnValue();
        cir.setReturnValue(original.scaled((float)scale));
    }

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void modifyEyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        float original = cir.getReturnValue();
        cir.setReturnValue(original * (float)scale);
    }

    private boolean isSpectator() {
        return ((PlayerEntity)(Object)this).isSpectator();
    }
}

