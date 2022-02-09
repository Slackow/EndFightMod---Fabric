package com.slackow.endfight.gui.core;

import com.slackow.endfight.util.Renameable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ListGUI<T extends Renameable> extends Screen {
    private final List<T> data = new ArrayList<>();
    private final Supplier<T> getNewItem;
    private int page = 0;
    int selected;
    public ListGUI(List<T> data, Supplier<T> getNew) {
       this.data.addAll(data);
       this.selected = 0;
       this.getNewItem = getNew;
    }
    @SuppressWarnings("unchecked")
    public void init() {
        this.buttons.clear();
        boolean hasPages = data.size() > 5;
        List<T> displayed = hasPages ? data.subList(page * 5, Math.min(page * 5 + 5, data.size())) : data;
        for (int i = 0; i < displayed.size(); i++) {
            String buttonMsg = displayed.get(i).getName();
            if (i + page * 5 == selected) {
                buttonMsg = "> " + buttonMsg + " <";
            }
            int textWidth = Math.max(100, textRenderer.getStringWidth(buttonMsg) + 20);
            buttons.add(new ButtonWidget(i, width / 2 - textWidth / 2, height / 2 + i * 24 - displayed.size() * 12, textWidth, 20, buttonMsg));
        }
        int homeRow = height / 2 + displayed.size() * 12;
        buttons.add(new ButtonWidget(5, width / 2 - 10, homeRow, 20, 20, "+"));
        ButtonWidget left = new ButtonWidget(6, width / 2 - 32, homeRow, 20, 20, "<");
        left.active = page > 0;
        buttons.add(left);
        ButtonWidget right = new ButtonWidget(7, width / 2 + 12, homeRow, 20, 20, ">");
        right.active = (data.size() - 1) / 5 > page;
        buttons.add(right);
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        if (mouseX >= 0 || mouseY >= 0) {
            renderBackground();
        }
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
            MinecraftClient.getInstance().openScreen(new ViewGUI<>(this, data.get(index), index == selected));
        } else if (button.id == 6) {
            page--;
            reinit();
        } else if (button.id == 7) {
            page++;
            reinit();
        } else if (button.id == 5) {
            T obj = getNewItem.get();
            if (obj != null) {
                data.add(obj);
                selected = data.size() - 1;
                reinit();
            }
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


}
