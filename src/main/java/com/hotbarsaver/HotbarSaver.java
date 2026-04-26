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

public class HotbarSaver implements ClientModInitializer {

    private static KeyBinding saveKey;
    private static KeyBinding loadKey;

    @Override
    public void onInitializeClient() {
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

    private void saveHotbar(MinecraftClient client) {
        try {
            File hotbarFile = getHotbarFile(client);
            NbtCompound root;

            if (hotbarFile.exists()) {
                root = NbtIo.read(hotbarFile.toPath());
                if (root == null) root = new NbtCompound();
            } else {
                root = new NbtCompound();
            }

            NbtList hotbarList = new NbtList();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                // FIX 1: toNbt() replaces encode() in 1.21.8
                NbtCompound itemTag = (NbtCompound) stack.toNbt(client.player.getRegistryManager());
                hotbarList.add(itemTag);
            }

            root.put("0", hotbarList);
            NbtIo.write(root, hotbarFile.toPath());

            client.player.sendMessage(
                    Text.literal("✔ Hotbar saved! (F7 to restore)").formatted(Formatting.GREEN),
                    true
            );

        } catch (IOException e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to save hotbar: " + e.getMessage()).formatted(Formatting.RED),
                    true
            );
            e.printStackTrace();
        }
    }

    private void loadHotbar(MinecraftClient client) {
        try {
            File hotbarFile = getHotbarFile(client);

            if (!hotbarFile.exists()) {
                client.player.sendMessage(
                        Text.literal("✘ No saved hotbar found. Press F6 to save one first.").formatted(Formatting.YELLOW),
                        true
                );
                return;
            }

            NbtCompound root = NbtIo.read(hotbarFile.toPath());
            if (root == null || !root.contains("0")) {
                client.player.sendMessage(
                        Text.literal("✘ Saved hotbar data is empty or corrupt.").formatted(Formatting.RED),
                        true
                );
                return;
            }

            // FIX 2: getList() no longer takes a type int argument in 1.21.8
            NbtList hotbarList = root.getList("0");
            int count = Math.min(hotbarList.size(), 9);

            for (int i = 0; i < count; i++) {
                // FIX 3: getCompound() returns Optional<NbtCompound> now
                NbtCompound itemTag = hotbarList.getCompound(i).orElse(new NbtCompound());
                // FIX 4: fromNbtOrEmpty() replaces fromNbt() in 1.21.8
                ItemStack stack = ItemStack.fromNbtOrEmpty(client.player.getRegistryManager(), itemTag);
                client.player.getInventory().setStack(i, stack);
            }

            client.player.playerScreenHandler.sendContentUpdates();

            client.player.sendMessage(
                    Text.literal("✔ Hotbar restored!").formatted(Formatting.GREEN),
                    true
            );

        } catch (IOException e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to load hotbar: " + e.getMessage()).formatted(Formatting.RED),
                    true
            );
            e.printStackTrace();
        }
    }

    private File getHotbarFile(MinecraftClient client) {
        // FIX 5: runDirectory is a File, not a Path — use new File() instead of .resolve()
        return new File(client.runDirectory, "hotbar.nbt");
    }
}
