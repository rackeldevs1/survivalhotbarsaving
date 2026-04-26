package com.hotbarsaver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.nio.file.Path;

public class HotbarConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("hotbarsaver.json");

    public String saveModifier = "X";
    public String loadModifier = "V";

    private static HotbarConfig instance;

    public static HotbarConfig get() {
        if (instance == null) load();
        return instance;
    }

    public static void load() {
        if (CONFIG_PATH.toFile().exists()) {
            try (Reader reader = new FileReader(CONFIG_PATH.toFile())) {
                instance = GSON.fromJson(reader, HotbarConfig.class);
                if (instance == null) instance = new HotbarConfig();
                if (instance.saveModifier == null || instance.saveModifier.length() != 1)
                    instance.saveModifier = "X";
                if (instance.loadModifier == null || instance.loadModifier.length() != 1)
                    instance.loadModifier = "V";
                instance.saveModifier = instance.saveModifier.toUpperCase();
                instance.loadModifier = instance.loadModifier.toUpperCase();
            } catch (Exception e) {
                instance = new HotbarConfig();
            }
        } else {
            instance = new HotbarConfig();
            save();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(instance, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int letterToGlfw(String letter) {
        if (letter == null || letter.isEmpty()) return GLFW.GLFW_KEY_X;
        return switch (letter.toUpperCase()) {
            case "A" -> GLFW.GLFW_KEY_A; case "B" -> GLFW.GLFW_KEY_B;
            case "C" -> GLFW.GLFW_KEY_C; case "D" -> GLFW.GLFW_KEY_D;
            case "E" -> GLFW.GLFW_KEY_E; case "F" -> GLFW.GLFW_KEY_F;
            case "G" -> GLFW.GLFW_KEY_G; case "H" -> GLFW.GLFW_KEY_H;
            case "I" -> GLFW.GLFW_KEY_I; case "J" -> GLFW.GLFW_KEY_J;
            case "K" -> GLFW.GLFW_KEY_K; case "L" -> GLFW.GLFW_KEY_L;
            case "M" -> GLFW.GLFW_KEY_M; case "N" -> GLFW.GLFW_KEY_N;
            case "O" -> GLFW.GLFW_KEY_O; case "P" -> GLFW.GLFW_KEY_P;
            case "Q" -> GLFW.GLFW_KEY_Q; case "R" -> GLFW.GLFW_KEY_R;
            case "S" -> GLFW.GLFW_KEY_S; case "T" -> GLFW.GLFW_KEY_T;
            case "U" -> GLFW.GLFW_KEY_U; case "V" -> GLFW.GLFW_KEY_V;
            case "W" -> GLFW.GLFW_KEY_W; case "X" -> GLFW.GLFW_KEY_X;
            case "Y" -> GLFW.GLFW_KEY_Y; case "Z" -> GLFW.GLFW_KEY_Z;
            default  -> GLFW.GLFW_KEY_X;
        };
    }
}
