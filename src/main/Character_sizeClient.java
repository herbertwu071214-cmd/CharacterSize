package hackcraft.character_size.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import hackcraft.character_size.Character_size;
import hackcraft.character_size.IScaleHolder;

public class Character_sizeClient implements ClientModInitializer {
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Character_size.SCALE_SYNC_ID);
    private static KeyBinding increaseKey;
    private static KeyBinding decreaseKey;

    @Override
    public void onInitializeClient() {
        increaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.character_size.increase",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                CATEGORY
        ));
        decreaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.character_size.decrease",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (increaseKey.wasPressed()) {
                changeScale(0.5);
            }
            while (decreaseKey.wasPressed()) {
                changeScale(-0.5);
            }
        });
    }

    private void changeScale(double delta) {
        var client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player != null) {
            double current = ((IScaleHolder) client.player).characterSize$getScale();
            double newScale = current + delta;
            newScale = MathHelper.clamp(newScale, 0.5, 3.0);
            ((IScaleHolder) client.player).characterSize$setScale(newScale);
            ClientPlayNetworking.send(new Character_size.ScalePayload(newScale));
        }
    }
}
