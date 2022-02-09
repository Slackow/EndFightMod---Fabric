package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class RenameGUI<T extends Renameable> extends Screen {
    private final ViewGUI<T> from;
    private TextFieldWidget textField;
    private final T obj;
    private String cached;

    public RenameGUI(ViewGUI<T> from, T obj) {
        this.from = from;
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        textField = new TextFieldWidget(textRenderer, width / 2 - 48, height / 2 - 22, 96, 20);
        if (cached == null) {
            cached = obj.getName();
        }
        textField.setText(cached);
        textField.setFocused(true);
        buttons.add(new ButtonWidget(0, width / 2 - 50, height / 2, 100, 20, "Done"));
        super.init();
        from.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        from.render(-1, -1, tickDelta);
        renderBackground();
        textField.render();
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void keyPressed(char character, int code) {
        textField.keyPressed(character, code);
        cached = textField.getText();
        super.keyPressed(character, code);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (cached != null && !cached.trim().isEmpty())
        obj.setName(cached.trim());
        MinecraftClient.getInstance().openScreen(from);
        super.buttonClicked(button);
    }

    @Override
    public void tick() {
        textField.tick();
        super.tick();
    }
}
