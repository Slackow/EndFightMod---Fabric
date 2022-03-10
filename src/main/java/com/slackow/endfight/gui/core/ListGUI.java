package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ListGUI<T extends Renameable> extends Screen {
    private final List<T> data;
    private final Supplier<T> getNewItem;
    private final Screen from;
    private final BiConsumer<ViewGUI<T>, T> editObj;
    private int page = 0;
    int selected;
    private final BiConsumer<List<T>, Integer> save;
    final String title;
    private int y;

    public ListGUI(Screen from, List<T> data, int selected, Supplier<T> getNew, BiConsumer<ViewGUI<T>, T> editObj, BiConsumer<List<T>, Integer> save, String title) {
        this.from = from;
        this.editObj = editObj;
        this.data = new ArrayList<>(data);
        this.selected = selected;
        this.getNewItem = getNew;
        this.save = save;
        this.title = title;
    }
    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        boolean hasPages = data.size() > 5;
        List<T> displayed = hasPages ? data.subList(page * 5, Math.min(page * 5 + 5, data.size())) : data;
        for (int i = 0; i < displayed.size(); i++) {
            String buttonMsg = displayed.get(i).getName();
            if (isSelectable() && i + page * 5 == selected) {
                buttonMsg = "> " + buttonMsg + " <";
            }
            int textWidth = Math.max(100, textRenderer.getStringWidth(buttonMsg) + 20);
            buttons.add(new ButtonWidget(i, width / 2 - textWidth / 2, height / 6 + 30 + i * 24 - displayed.size() * 12, textWidth, 20, buttonMsg));
        }
        y = height / 6 + 30 - 24 - displayed.size() * 12;
        int homeRow = height / 6 + 30 + displayed.size() * 12;
        buttons.add(new ButtonWidget(5, width / 2 - 10, homeRow, 20, 20, "+"));
        ButtonWidget left = new ButtonWidget(6, width / 2 - 32, homeRow, 20, 20, "<");
        left.active = page > 0;
        buttons.add(left);
        ButtonWidget right = new ButtonWidget(7, width / 2 + 12, homeRow, 20, 20, ">");
        right.active = (data.size() - 1) / 5 > page;
        buttons.add(right);
        buttons.add(new ButtonWidget(8, width / 2 - 100, height / 6 + 150, 200, 20, I18n.translate("gui.done")));
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, title, width / 2, y, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
    }

    private int lastTick = 0;

    @Override
    protected void buttonClicked(ButtonWidget button) {
        if (lastTick == tick) {
            return;
        }
        lastTick = tick;
        if (button.id >= 0 && button.id < 5) {
            int index = button.id + page * 5;
            if (!isSelectable() || index == selected) {
                MinecraftClient.getInstance().openScreen(new ViewGUI<>(this, data.get(index)));
            } else {
                selected = index;
                reinit();
            }
        } else if (button.id == 6) {
            page--;
            reinit();
        } else if (button.id == 7) {
            page++;
            reinit();
        } else if (button.id == 5) {
            T obj = getNewItem.get();
            if (obj != null) {
                obj.setName("");
                data.add(obj);
                MinecraftClient.getInstance().openScreen(new ViewGUI<>(this, obj));
                reinit();
            }
        } else if (button.id == 8) {
            save.accept(data, selected);
            MinecraftClient.getInstance().openScreen(from);
        }
        super.buttonClicked(button);
    }

    private void reinit() {
        init(this.client, this.width, this.height);
    }

    private int tick = 0;

    @Override
    public void tick() {
        tick++;
        super.tick();
    }

    void select(T obj) {
        selected = data.indexOf(obj);
    }

    public void remove(T obj) {
        data.remove(obj);
        page = Math.min(page, (data.size() - 1) / 5);
        if (data.isEmpty()) {
            selected = -1;
        }
    }


    public BiConsumer<ViewGUI<T>, T> getEditObj() {
        return editObj;
    }

    public boolean isSelectable() {
        return true;
    }
}
