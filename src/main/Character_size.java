package hackcraft.character_size;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class Character_size implements ModInitializer {
    public static final String MOD_ID = "character_size";
    public static final Identifier SCALE_SYNC_ID = Identifier.of(MOD_ID, "scale_sync");

    public record ScalePayload(double scale) implements CustomPayload {
        public static final Id<ScalePayload> ID = new Id<>(SCALE_SYNC_ID);
        public static final PacketCodec<RegistryByteBuf, ScalePayload> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeDouble(value.scale),
                buf -> new ScalePayload(buf.readDouble())
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    @Override
    public void onInitialize() {

        PayloadTypeRegistry.playS2C().register(ScalePayload.ID, ScalePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ScalePayload.ID, ScalePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ScalePayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();
            double newScale = payload.scale();
            context.server().execute(() -> {
                ((IScaleHolder) player).characterSize$setScale(newScale);
                var movementSpeed = player.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
                if (movementSpeed != null) {
                    movementSpeed.setBaseValue(0.1 * newScale);
                }
                player.calculateDimensions();
            });
        });
    }
}
