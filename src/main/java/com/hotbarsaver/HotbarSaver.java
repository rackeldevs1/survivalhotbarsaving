package com.hotbarsaver;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.io.File;

public class HotbarSaver implements ClientModInitializer {

    private static final int[] DIGIT_KEYS = {
            GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_3,
            GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_5, GLFW.GLFW_KEY_6,
            GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_9
    };

    private static KeyBinding openConfigKey;
    private static final boolean[] saveTriggered = new boolean[9];
    private static final boolean[] loadTriggered = new boolean[9];

    @Override
    public void onInitializeClient() {
        HotbarConfig.load();

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.hotbarsaver.openconfig",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.hotbarsaver"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.currentScreen != null) return;

            while (openConfigKey.wasPressed()) {
                client.setScreen(new HotbarConfigScreen(null));
            }

            long window = client.getWindow().getHandle();
            int saveGlfw = HotbarConfig.letterToGlfw(HotbarConfig.get().saveModifier);
            int loadGlfw = HotbarConfig.letterToGlfw(HotbarConfig.get().loadModifier);

            boolean saveHeld = GLFW.glfwGetKey(window, saveGlfw) == GLFW.GLFW_PRESS;
            boolean loadHeld = GLFW.glfwGetKey(window, loadGlfw) == GLFW.GLFW_PRESS;

            for (int i = 0; i < 9; i++) {
                boolean digitPressed = GLFW.glfwGetKey(window, DIGIT_KEYS[i]) == GLFW.GLFW_PRESS;

                if (saveHeld && digitPressed) {
                    if (!saveTriggered[i]) {
                        saveTriggered[i] = true;
                        saveHotbar(client, i);
                    }
                } else {
                    saveTriggered[i] = false;
                }

                if (loadHeld && digitPressed) {
                    if (!loadTriggered[i]) {
                        loadTriggered[i] = true;
                        loadHotbar(client, i);
                    }
                } else {
                    loadTriggered[i] = false;
                }
            }
        });
    }

    public static void reloadKeys() {
        // Keys are read live from config each tick — nothing to rebuild
    }

    private static void saveHotbar(MinecraftClient client, int slot) {
        try {
            File hotbarFile = getHotbarFile(client);
            NbtCompound root;
            if (hotbarFile.exists()) {
                root = NbtIo.read(hotbarFile.toPath());
                if (root == null) root = new NbtCompound();
            } else {
                root = new NbtCompound();
            }

            var registryOps = client.player.getRegistryManager().getOps(NbtOps.INSTANCE);
            NbtList hotbarList = new NbtList();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                NbtElement encoded = ItemStack.CODEC.encodeStart(registryOps, stack).getOrThrow();
                hotbarList.add(encoded);
            }

            root.put(String.valueOf(slot), hotbarList);
            NbtIo.write(root, hotbarFile.toPath());

            client.player.sendMessage(
                    Text.literal("✔ Hotbar saved to slot " + (slot + 1)
                            + " (" + HotbarConfig.get().saveModifier + "+" + (slot + 1)
                            + ") — Made by rackeldevs").formatted(Formatting.GREEN),
                    true);

        } catch (Exception e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to save: " + e.getMessage()).formatted(Formatting.RED), true);
            e.printStackTrace();
        }
    }

    private static void loadHotbar(MinecraftClient client, int slot) {
        try {
            File hotbarFile = getHotbarFile(client);

            if (!hotbarFile.exists()) {
                client.player.sendMessage(Text.literal("✘ No saved hotbars found. Use "
                        + HotbarConfig.get().saveModifier + "+1~9 to save first.")
                        .formatted(Formatting.YELLOW), true);
                return;
            }

            NbtCompound root = NbtIo.read(hotbarFile.toPath());
            if (root == null || !root.contains(String.valueOf(slot))) {
                client.player.sendMessage(Text.literal("✘ Nothing saved in slot " + (slot + 1) + ".")
                        .formatted(Formatting.YELLOW), true);
                return;
            }

            var registryOps = client.player.getRegistryManager().getOps(NbtOps.INSTANCE);
            NbtList hotbarList = root.getList(String.valueOf(slot)).orElse(new NbtList());
            int count = Math.min(hotbarList.size(), 9);

            for (int i = 0; i < count; i++) {
                NbtElement itemNbt = hotbarList.get(i);
                ItemStack stack = ItemStack.CODEC.parse(registryOps, itemNbt).getOrThrow();
                client.player.getInventory().setStack(i, stack);
            }

            client.player.playerScreenHandler.sendContentUpdates();

            client.player.sendMessage(
                    Text.literal("✔ Slot " + (slot + 1) + " restored! ("
                            + HotbarConfig.get().loadModifier + "+" + (slot + 1)
                            + ") — Made by rackeldevs").formatted(Formatting.GREEN),
                    true);

        } catch (Exception e) {
            client.player.sendMessage(
                    Text.literal("✘ Failed to load: " + e.getMessage()).formatted(Formatting.RED), true);
            e.printStackTrace();
        }
    }

    private static File getHotbarFile(MinecraftClient client) {
        return new File(client.runDirectory, "hotbar.nbt");
    }
}
