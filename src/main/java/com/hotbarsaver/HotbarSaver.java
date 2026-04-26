package com.hotbarsaver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class HotbarSaver implements ClientModInitializer {

    private static KeyBinding saveKey;
    private static KeyBinding loadKey;

    @Override
    public void onInitializeClient() {
        // Register keybindings
        saveKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hotbarsaver.save",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                "category.hotbarsaver"
        ));

        loadKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hotbarsaver.load",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                "category.hotbarsaver"
        ));

        // Tick event to detect key presses
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            while (saveKey.wasPressed()) {
                saveHotbar(client);
            }

            while (loadKey.wasPressed()) {
                loadHotbar(client);
            }
        });
    }

    /**
     * Saves the player's current hotbar (slots 0-8) to hotbar.nbt,
     * using the exact same format as vanilla creative mode hotbar saving.
     * Slot index 0 = first saved hotbar slot (like pressing X on slot 1 in creative).
     */
    private void saveHotbar(MinecraftClient client) {
        try {
            // Read existing hotbar.nbt or create fresh
            File hotbarFile = getHotbarFile(client);
            NbtCompound root;

            if (hotbarFile.exists()) {
                root = NbtIo.read(hotbarFile.toPath());
                if (root == null) root = new NbtCompound();
            } else {
                root = new NbtCompound();
            }

            // Build a NbtList of 9 item stacks (slots 0–8 of player inventory)
            NbtList hotbarList = new NbtList();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                NbtCompound itemTag = new NbtCompound();
                // encodeAllComponents writes the full item NBT including components
                stack.encode(client.player.getRegistryManager(), itemTag);
                hotbarList.add(itemTag);
            }

            // Vanilla stores saved hotbars as "0" through "8" (9 hotbar slots saved)
            // We save to slot "0" (the first saved hotbar) by default
            root.put("0", hotbarList);

            NbtIo.write(root, hotbarFile.toPath());

            client.player.sendMessage(
                    Text.literal("✔ Hotbar saved! (F7 to restore)")
                            .formatted(Formatting.GREEN),
                    true // action bar
            );

        } catch (IOException e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to save hotbar: " + e.getMessage())
                            .formatted(Formatting.RED),
                    true
            );
            e.printStackTrace();
        }
    }

    /**
     * Loads the hotbar from hotbar.nbt slot "0" and applies it
     * to the player's current hotbar inventory slots 0–8.
     */
    private void loadHotbar(MinecraftClient client) {
        try {
            File hotbarFile = getHotbarFile(client);

            if (!hotbarFile.exists()) {
                client.player.sendMessage(
                        Text.literal("✘ No saved hotbar found. Press F6 to save one first.")
                                .formatted(Formatting.YELLOW),
                        true
                );
                return;
            }

            NbtCompound root = NbtIo.read(hotbarFile.toPath());
            if (root == null || !root.contains("0")) {
                client.player.sendMessage(
                        Text.literal("✘ Saved hotbar data is empty or corrupt.")
                                .formatted(Formatting.RED),
                        true
                );
                return;
            }

            NbtList hotbarList = root.getList("0", 10); // 10 = NbtCompound type
            int count = Math.min(hotbarList.size(), 9);

            for (int i = 0; i < count; i++) {
                NbtCompound itemTag = hotbarList.getCompound(i);
                ItemStack stack = ItemStack.fromNbt(client.player.getRegistryManager(), itemTag)
                        .orElse(ItemStack.EMPTY);
                client.player.getInventory().setStack(i, stack);
            }

            // Sync inventory to server
            client.player.playerScreenHandler.sendContentUpdates();

            client.player.sendMessage(
                    Text.literal("✔ Hotbar restored!")
                            .formatted(Formatting.GREEN),
                    true
            );

        } catch (IOException e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to load hotbar: " + e.getMessage())
                            .formatted(Formatting.RED),
                    true
            );
            e.printStackTrace();
        }
    }

    /**
     * Returns the hotbar.nbt file path — same location vanilla uses:
     * .minecraft/hotbar.nbt
     */
    private File getHotbarFile(MinecraftClient client) {
        return client.runDirectory.resolve("hotbar.nbt").toFile();
    }
}
