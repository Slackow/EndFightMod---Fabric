package com.slackow.endfight.gui.config;

import com.slackow.endfight.gui.core.ViewGUI;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.lwjgl.input.Keyboard;

public class KeybindGUI extends Screen {
    private final ViewGUI<KeyBind> from;
    private final KeyBind obj;
    private TextFieldWidget textField;
    private boolean choosing;

    public KeybindGUI(ViewGUI<KeyBind> from, KeyBind obj) {
        this.from = from;
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        textField = new TextFieldWidget(textRenderer, width / 2 - 48, height / 2 - 22, 96, 20);
        textField.setText(obj.message);
        buttons.add((new ButtonWidget(0, width / 2 - 35, height / 2, 70, 20, choosing ? "> Key <" : ("Key: " + (obj.code != Keyboard.KEY_ESCAPE ? "None" : Keyboard.getKeyName(obj.code))))));
        ButtonWidget reset = new ButtonWidget(1, width / 2 + 40, height / 2, 50, 20, "Reset");
        reset.active = obj.code != Keyboard.KEY_ESCAPE;
        buttons.add(reset);
        buttons.add(new ButtonWidget(2, width / 2 - 50 , height / 2 + 22, 100, 20, "Done"));
        super.init();
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
        obj.message = textField.getText();
        if (choosing){
            obj.code = code;
            choosing = false;
        } else {
            super.keyPressed(character, code);
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        choosing = button.id == 0;
        if (button.id == 2) {
            MinecraftClient.getInstance().openScreen(from);
            return;
        }
        if (button.id == 1) {
            obj.code = Keyboard.KEY_ESCAPE;
        }
        init();
        super.buttonClicked(button);
    }

    @Override
    public void tick() {
        textField.tick();
        super.tick();
    }
}
