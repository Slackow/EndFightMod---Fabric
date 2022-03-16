package com.slackow.endfight.gui.config;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.gui.core.ViewGUI;
import com.slackow.endfight.util.KeyBind;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.input.Keyboard;

import static net.minecraft.util.Formatting.RESET;
import static net.minecraft.util.Formatting.YELLOW;

public class KeybindGUI extends Screen {
    private final ViewGUI<KeyBind> from;
    private final KeyBind obj;
    private TextFieldWidget textField;
    private boolean choosing;
    private ButtonWidget keyButton;
    private ButtonWidget reset;

    public KeybindGUI(ViewGUI<KeyBind> from, KeyBind obj) {
        this.from = from;
        this.obj = obj;
    }

    @Override
    public void init() {
        textField = new TextFieldWidget(-1, textRenderer, width / 2 - 73, height / 2 - 22, 146, 20);
        textField.setMaxLength(512);
        textField.setText(obj.message);
        keyButton = new ButtonWidget(0, width / 2 - 75, height / 2, 90, 20, "");
        updateKeyButton();
        buttons.add(keyButton);
        reset = new ButtonWidget(1, width / 2 + 20, height / 2, 55, 20, "Reset");
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
        drawCenteredString(textRenderer, "Key: ", width / 2 - 90, height / 2 + 6, 0xFFFFFF);
        drawCenteredString(textRenderer, "Command: ", width / 2 - 102, height / 2 + 6 - 20, 0xFFFFFF);
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
            updateKeyButton();
            BigConfig.save();
        } else {
            super.keyPressed(character, code);
        }
    }

    private void updateKeyButton() {
        String key = (obj.code == Keyboard.KEY_ESCAPE ? "None" : Keyboard.getKeyName(obj.code));
        keyButton.message = choosing ? "> " + YELLOW + key + RESET + " <" : key;
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
        if (choosing || button.id == 1) {
            updateKeyButton();
            reset.active = obj.code != Keyboard.KEY_ESCAPE;
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
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void tick() {
        textField.tick();
        super.tick();
    }
}
