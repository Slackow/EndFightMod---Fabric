package com.slackow.endfight.gui.config;

import com.slackow.endfight.util.Island;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.MinecraftServer;

public class IslandGUI extends Screen {
    private final Screen from;
    private final Island obj;
    private TextFieldWidget textField;

    public IslandGUI(Screen from, Island obj) {
        this.from = from;
        this.obj = obj;
    }

    @Override
    public void init() {
        textField = new TextFieldWidget(0, textRenderer, width / 2 - 73, height / 2 - 22, 146, 20);
        textField.setText(obj.getSeed() + "");
        buttons.add(new ButtonWidget(1, width / 2 - 75 , height / 2, 150, 20, "Copy Current"));
        buttons.add(new ButtonWidget(2, width / 2 - 75 , height / 2 + 22, 150, 20, I18n.translate("gui.done")));
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, "Island", width / 2, height / 6 - 2, 0xFFFFFF);
        drawCenteredString(textRenderer, obj.getName(), width / 2, height / 6 + 10, 0xFFFFFF);
        textField.render();
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    public void tick() {
        textField.tick();
        super.tick();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        textField.mouseClicked(mouseX, mouseY, button);
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 1) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                obj.setSeed(server.getWorld(0).getSeed());
                textField.setText(obj.getSeed() + "");
            }
        } else {
            long seed;
            try {
                seed = Long.parseLong(textField.getText().trim());
            } catch (NumberFormatException e) {
                seed = textField.getText().hashCode();
            }
            obj.setSeed(seed);
            client.setScreen(from);
        }
        super.buttonClicked(button);
    }
}
