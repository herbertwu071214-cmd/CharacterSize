package hackcraft.character_size.client.character_size.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;
import hackcraft.character_size.client.character_size.Character_size;
import hackcraft.character_size.client.character_size.IScaleHolder;

public class Character_sizeClient implements ClientModInitializer {
    private static KeyBinding increaseKey;
    private static KeyBinding decreaseKey;

    @Override
    public void onInitializeClient() {
        increaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.character_size.increase",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.character_size"
        ));
        decreaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.character_size.decrease",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.character_size"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (increaseKey.wasPressed()) {
                sendScaleChange(0.5);
            }
            while (decreaseKey.wasPressed()) {
                sendScaleChange(-0.5);
            }
        });
    }

    private void sendScaleChange(double delta) {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            double newScale = ((IScaleHolder) client.player).getScale() + delta;
            newScale = Math.clamp(newScale, 0.5, 3.0);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeDouble(newScale);
            ClientPlayNetworking.send(Character_size.SCALE_SYNC_ID, buf);
        }
    }
}