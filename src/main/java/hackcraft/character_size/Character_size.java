package hackcraft.character_size.client.character_size;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Character_size implements ModInitializer {
    public static final String MOD_ID = "character_size";
    public static final Identifier SCALE_SYNC_ID = new Identifier(MOD_ID, "scale_sync");

    @Override
    public void onInitialize() {
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (oldPlayer instanceof ServerPlayerEntity oldSp && newPlayer instanceof ServerPlayerEntity newSp) {
                double oldScale = ((IScaleHolder) oldSp).getScale();
                ((IScaleHolder) newSp).setScale(oldScale);
                updatePlayerAttributes(newSp, oldScale);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(SCALE_SYNC_ID, (server, player, handler, buf, responseSender) -> {
            double newScale = buf.readDouble();
            server.execute(() -> ((IScaleHolder) player).setScale(newScale));
        });
    }

    public static void updatePlayerAttributes(ServerPlayerEntity player, double scale) {
        EntityAttributeInstance speed = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(0.1 * scale);
        }
    }
}