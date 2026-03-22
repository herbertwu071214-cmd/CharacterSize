package hackage.character_size.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import hackage.character_size.IScaleHolder;

@Mixin(ServerPlayerInteractionManager.class)
public class BlockBreakMixin {
    @Inject(method = "tryBreakBlock", at = @At("HEAD"))
    private void onTryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = ((ServerPlayerInteractionManager)(Object)this).player;
        double scale = ((IScaleHolder) player).getScale();
        if (scale > 1.0) {
            int radius = (int)Math.floor(scale);
            if (radius >= 1) {
                World world = player.getWorld();
                BlockState originalState = world.getBlockState(pos);
                for (int dx = -radius; dx <= radius; dx++) {
                    for (int dy = -radius; dy <= radius; dy++) {
                        for (int dz = -radius; dz <= radius; dz++) {
                            if (dx == 0 && dy == 0 && dz == 0) continue;
                            BlockPos neighbor = pos.add(dx, dy, dz);
                            BlockState neighborState = world.getBlockState(neighbor);
                            if (neighborState.getHardness(world, neighbor) >= 0 &&
                                    neighborState.getHardness(world, neighbor) <= originalState.getHardness(world, pos) + 1) {
                                world.breakBlock(neighbor, true, player);
                            }
                        }
                    }
                }
            }
        }
    }
}
