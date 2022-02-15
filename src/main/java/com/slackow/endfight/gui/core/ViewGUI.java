package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.stream.Stream;

import static net.minecraft.util.Formatting.RED;

public class ViewGUI<T extends Renameable> extends Screen {
    final ListGUI<T> from;
    private final T obj;
    private boolean isNew;

    public ViewGUI(ListGUI<T> from, T obj) {
        this.from = from;
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        this.buttons.clear();
        ButtonWidget edit = new ButtonWidget(1, width / 2 - 100, height / 2 - 30, 200, 20, "Edit");
        this.buttons.add(edit);
        isNew = obj.getName().isEmpty();
        this.buttons.add(new ButtonWidget(2, width / 2 - 100, height / 2 - 10, 100, 20 , isNew ? "Name" : "Rename"));
        ButtonWidget remove = new ButtonWidget(3, width / 2, height / 2 - 10, 100, 20, RED + "Remove");
        this.buttons.add(remove);
        this.buttons.add(new ButtonWidget(4, width / 2 - 100, height / 2 + 10, 200, 20, isNew ? "Cancel" : I18n.translate("gui.done")));
        edit.active = !isNew;
        remove.active = !isNew;
        super.init();
        from.init();
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
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
                if (obj.getName().isEmpty()) {
                    remove();
                }
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

        renderBackground();
        if (isNew) {
            drawCenteredString(textRenderer, "Please name this new " + from.title.substring(0, from.title.length() - 1), width / 2, height / 2 - 80, 0xFFFFFF);
        } else {
            drawCenteredString(textRenderer, obj.getName(), width / 2, height / 2 - 80, 0xFFFFFF);
        }

        super.render(mouseX, mouseY, tickDelta);
    }
}
