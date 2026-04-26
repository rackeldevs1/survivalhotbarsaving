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

        saveField = new TextFieldWidget(
                this.textRenderer, centerX - 20, startY + 20, 40, 20,
                Text.literal("Save Key"));
        saveField.setMaxLength(1);
        saveField.setText(HotbarConfig.get().saveModifier);
        saveField.setChangedListener(text -> validateFields());
        this.addDrawableChild(saveField);

        loadField = new TextFieldWidget(
                this.textRenderer, centerX - 20, startY + 70, 40, 20,
                Text.literal("Load Key"));
        loadField.setMaxLength(1);
        loadField.setText(HotbarConfig.get().loadModifier);
        loadField.setChangedListener(text -> validateFields());
        this.addDrawableChild(loadField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"), btn -> {
            if (applyAndSave()) this.client.setScreen(parent);
        }).dimensions(centerX - 80, startY + 110, 75, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> {
            this.client.setScreen(parent);
        }).dimensions(centerX + 5, startY + 110, 75, 20).build());
    }

    private void validateFields() {
        saveError = "";
        loadError = "";
        String s = saveField.getText().toUpperCase();
        String l = loadField.getText().toUpperCase();
        if (s.isEmpty() || !s.matches("[A-Z]")) saveError = "Must be a single letter (A-Z)";
        if (l.isEmpty() || !l.matches("[A-Z]")) loadError = "Must be a single letter (A-Z)";
        if (saveError.isEmpty() && loadError.isEmpty() && s.equals(l)) {
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
        HotbarSaver.reloadKeys();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        int centerX = this.width / 2;
        int startY = this.height / 2 - 60;

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("✦ Hotbar Saver Config ✦"), centerX, startY - 10, 0xFFFFAA00);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Save modifier key (hold + 1~9 to save):"),
                centerX - 120, startY + 7, 0xFFFFFFFF);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Load modifier key (hold + 1~9 to load):"),
                centerX - 120, startY + 57, 0xFFFFFFFF);

        String s = saveField.getText().isEmpty() ? "?" : saveField.getText().toUpperCase();
        String l = loadField.getText().isEmpty() ? "?" : loadField.getText().toUpperCase();

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("→ " + s + "+1  " + s + "+2  ...  " + s + "+9  saves hotbar slots"),
                centerX - 120, startY + 43, 0xFF55FF55);

        context.drawTextWithShadow(this.textRenderer,
                Text.literal("→ " + l + "+1  " + l + "+2  ...  " + l + "+9  loads hotbar slots"),
                centerX - 120, startY + 93, 0xFF55FFFF);

        if (!saveError.isEmpty())
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal("⚠ " + saveError), centerX - 120, startY + 30, 0xFFFF5555);

        if (!loadError.isEmpty())
            context.drawTextWithShadow(this.textRenderer,
                    Text.literal("⚠ " + loadError), centerX - 120, startY + 80, 0xFFFF5555);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Made by rackeldevs"), centerX, startY + 140, 0xFF888888);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return true;
    }
}
