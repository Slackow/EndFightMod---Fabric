package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import static net.minecraft.util.Formatting.RED;

public class RemoveGUI<T extends Renameable> extends Screen {

    private final ViewGUI<T> from;

    public RemoveGUI(ViewGUI<T> from) {
        this.from = from;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.add(new ButtonWidget(0, width / 2 - 100, height / 6 + 100 - 10, 100, 20, "Back"));
        buttons.add(new ButtonWidget(1, width / 2, height / 6 + 100 - 10, 100, 20, RED + "Remove"));
        super.init();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (button.id == 1) {
            from.remove();
            MinecraftClient.getInstance().setScreen(from.from);
        } else {
            MinecraftClient.getInstance().setScreen(from);
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        from.render(-1, -1, tickDelta);
        this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        super.render(mouseX, mouseY, tickDelta);
    }
}
