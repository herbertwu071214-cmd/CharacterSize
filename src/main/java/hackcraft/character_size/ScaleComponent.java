package hackcraft.character_size.client.character_size;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;

public class ScaleComponent implements IScaleHolder {
    private double scale = 1.0;
    private final PlayerEntity player;

    public ScaleComponent(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public void setScale(double scale) {
        this.scale = Math.clamp(scale, 0.5, 3.0);
        if (!player.getWorld().isClient) {
            Character_size.updatePlayerAttributes((net.minecraft.server.network.ServerPlayerEntity) player, this.scale);
        }
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putDouble("Scale", scale);
    }

    public void readNbt(NbtCompound nbt) {
        scale = nbt.getDouble("Scale");
        if (scale == 0) scale = 1.0;
    }
}