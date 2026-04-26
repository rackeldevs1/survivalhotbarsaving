package com.hotbarsaver;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class HotbarConfigScreen extends Screen {

    private final Screen parent;

    private TextFieldWidget saveField;
    private TextFieldWidget loadField;

    private String saveError = "";
    private String loadError = "";

    public HotbarConfigScreen(Screen parent) {
        super(Text.literal("Hotbar Saver Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Save modifier field
        saveField = new TextFieldWidget(
                this.textRenderer,
                centerX - 20, startY + 20,
                40, 20,
                Text.literal("Save Key")
        );
        saveField.setMaxLength(1);
        saveField.setText(HotbarConfig.get().saveModifier);
        saveField.setChangedListener(text -> validateFields());
        this.addDrawableChild(saveField);

        // Load modifier field
        loadField = new TextFieldWidget(
                this.textRenderer,
                centerX - 20, startY + 70,
                40, 20,
                Text.literal("Load Key")
        );
        loadField.setMaxLength(1);
        loadField.setText(HotbarConfig.get().loadModifier);
        loadField.setChangedListener(text -> validateFields());
        this.addDrawableChild(loadField);

        // Save button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), btn -> {
            if (applyAndSave()) {
                this.client.setScreen(parent);
            }
        }).dimensions(centerX - 80, startY + 110, 75, 20).build());

        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> {
            this.client.setScreen(parent);
        }).dimensions(centerX + 5, startY + 110, 75, 20).build());
    }

    private void validateFields() {
        saveError = "";
        loadError = "";

        String saveText = saveField.getText().toUpperCase();
        String loadText = loadField.getText().toUpperCase();

        if (saveText.isEmpty() || !saveText.matches("[A-Z]")) {
            saveError = "Must be a single letter (A-Z)";
        }
        if (loadText.isEmpty() || !loadText.matches("[A-Z]")) {
            loadError = "Must be a single letter (A-Z)";
        }
        if (!saveError.isEmpty() && !loadError.isEmpty()) return;
        if (saveText.equals(loadText)) {
            saveError = "Save and Load keys must differ!";
            loadError = "Save and Load keys must differ!";
        }
    }

    private boolean applyAndSave() {
        validateFields();
        if (!saveError.isEmpty() || !loadError.isEmpty()) return false;

        HotbarConfig config = HotbarConfig.get();
        config.saveModifier = saveField.getText().toUpperCase();
        config.loadModifier = loadField.getText().toUpperCase();
        HotbarConfig.save();

        // Tell the mod to rebuild its key listeners with new keys
        HotbarSaver.reloadKeys();

        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background
        this.renderBackground(context, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        // Title
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("✦ Hotbar Saver Config ✦"),
                centerX, startY - 10, 0xFFFFAA00
        );

        // Save label
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Save modifier key (hold + 1~9 to save):"),
                centerX - 120, startY + 7, 0xFFFFFFFF
        );

        // Load label
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("Load modifier key (hold + 1~9 to load):"),
                centerX - 120, startY + 57, 0xFFFFFFFF
        );

        // Preview lines
        String saveKey = saveField.getText().isEmpty() ? "?" : saveField.getText().toUpperCase();
        String loadKey = loadField.getText().isEmpty() ? "?" : loadField.getText().toUpperCase();

        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("→ " + saveKey + "+1  " + saveKey + "+2  ...  " + saveKey + "+9  saves hotbar slots"),
                centerX - 120, startY + 43, 0xFF55FF55
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.literal("→ " + loadKey + "+1  " + loadKey + "+2  ...  " + loadKey + "+9  loads hotbar slots"),
                centerX - 120, startY + 93, 0xFF55FFFF
        );

        // Error messages
        if (!saveError.isEmpty()) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal("⚠ " + saveError),
                    centerX - 120, startY + 30, 0xFFFF5555
            );
        }
        if (!loadError.isEmpty()) {
            context.drawTextWithShadow(
                    this.textRenderer,
                    Text.literal("⚠ " + loadError),
                    centerX - 120, startY + 80, 0xFFFF5555
            );
        }

        // Made by line
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.literal("Made by rackeldevs"),
                centerX, startY + 140, 0xFF888888
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
