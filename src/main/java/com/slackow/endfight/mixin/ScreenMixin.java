package com.slackow.endfight.mixin;

import com.slackow.endfight.gui.core.TooltipRenderer;
import com.slackow.endfight.gui.widget.TooltipButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements TooltipRenderer {
    @Shadow protected List buttons;

    @Shadow protected abstract void renderTooltip(List text, int x, int y);

    @Shadow public int height;

    @Override
    public void renderTooltipsFromButtons() {
        for (Object button: this.buttons) {
            if ((button instanceof TooltipButtonWidget)) {
                TooltipButtonWidget buttonWidget = (TooltipButtonWidget) button;
                if (buttonWidget.isHovered()) {
                    List<String> tooltip = buttonWidget.getTooltip();
                    this.renderTooltip(tooltip, 0, height);
                }
            }
        }
    }
}
