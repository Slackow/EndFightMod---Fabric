package com.slackow.endfight.gui.widget;

import net.minecraft.client.gui.widget.ButtonWidget;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TooltipButtonWidget extends ButtonWidget {
    private List<String> tooltip;

    public TooltipButtonWidget(int id, int x, int y, int width, int height, String message, String tooltip) {
        super(id, x, y, width, height, message);
        this.setTooltip(tooltip);
    }

    public List<String> getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = Arrays.stream(tooltip.split("\n")).collect(Collectors.toList());
    }
}
