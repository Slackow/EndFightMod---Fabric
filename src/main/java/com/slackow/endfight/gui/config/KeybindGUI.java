package com.slackow.endfight.gui.config;

import com.slackow.endfight.gui.core.ViewGUI;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
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
        textField = new TextFieldWidget(textRenderer, width / 2 - 73, height / 2 - 22, 146, 20);
        textField.setText(obj.message);
        buttons.add((new ButtonWidget(0, width / 2 - 75, height / 2, 90, 20, choosing ? "> Key <" : ("Key: " + (obj.code == Keyboard.KEY_ESCAPE ? "None" : Keyboard.getKeyName(obj.code))))));
        ButtonWidget reset = new ButtonWidget(1, width / 2 + 20, height / 2, 60, 20, "Reset");
        reset.active = obj.code != Keyboard.KEY_ESCAPE;
        buttons.add(reset);
        buttons.add(new ButtonWidget(2, width / 2 - 75 , height / 2 + 22, 150, 20, I18n.translate("gui.done")));
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {

        renderBackground();
        drawCenteredString(textRenderer, "Keybind", width / 2, height / 6 - 2, 0xFFFFFF);
        drawCenteredString(textRenderer, obj.getName(), width / 2, height / 6 + 10, 0xFFFFFF);
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
            init();
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
        } else if (button.id == 1) {
            obj.code = Keyboard.KEY_ESCAPE;
        }
        super.buttonClicked(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        init();
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        textField.tick();
        super.tick();
    }
}
