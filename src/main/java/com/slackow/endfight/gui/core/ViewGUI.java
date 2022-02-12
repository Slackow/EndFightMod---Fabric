package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import static net.minecraft.util.Formatting.RED;

public class ViewGUI<T extends Renameable> extends Screen {
    final ListGUI<T> from;
    private final T obj;
    private boolean selected;

    public ViewGUI(ListGUI<T> from, T obj, boolean selected) {
        this.from = from;
        this.obj = obj;
        this.selected = selected;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        this.buttons.clear();
        ButtonWidget select = new ButtonWidget(0, width / 2 - 100, height / 2 - 30, 100, 20, "Select");
        select.active = !selected && from.isSelectable();
        this.buttons.add(select);
        this.buttons.add(new ButtonWidget(1, width / 2, height / 2 - 30, 100, 20, "Edit"));
        this.buttons.add(new ButtonWidget(2, width / 2 - 100, height / 2 - 10, 100, 20 , "Rename"));
        this.buttons.add(new ButtonWidget(3, width / 2, height / 2 - 10, 100, 20, RED + "Remove"));
        this.buttons.add(new ButtonWidget(4, width / 2 - 100, height / 2 + 10, 200, 20, "Done"));
        super.init();
        from.init();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                selected = true;
                from.select(obj);
                init();
                break;
            case 1:
                // EDIT BUTTON?!?!?
                from.getEditObj().accept(this, obj);
                break;
            case 2:
                MinecraftClient.getInstance().openScreen(new RenameGUI<>(this, obj));
                break;
            case 3:
                MinecraftClient.getInstance().openScreen(new RemoveGUI<>(this));
                break;
            case 4:
                MinecraftClient.getInstance().openScreen(from);
                break;
        }
        super.buttonClicked(button);
    }

    public void remove() {
        from.remove(obj);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        from.render(-1, -1, tickDelta);
        if (mouseX >= 0 || mouseY >= 0) {
            renderBackground();
        }
        drawCenteredString(textRenderer, obj.getName(), width / 2, height / 2 - 80, 0xFFFFFF);

        super.render(mouseX, mouseY, tickDelta);
    }
}
