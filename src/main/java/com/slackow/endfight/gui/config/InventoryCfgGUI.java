package com.slackow.endfight.gui.config;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.gui.core.ViewGUI;
import com.slackow.endfight.util.Kit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class InventoryCfgGUI extends Screen {
    private final ViewGUI<Kit> from;
    private final Kit obj;

    // If you are reading this code I deeply apologize to you personally
    public InventoryCfgGUI(ViewGUI<Kit> from, Kit obj) {
        this.from = from;
        this.obj = obj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.add(new ButtonWidget(0, width / 2 - 50, height / 6, 100, 20, "Set Current"));
        buttons.add(new ButtonWidget(1, width / 2 - 50, height / 6 + 25, 100, 20, "Get Current"));
        buttons.add(new ButtonWidget(2, width / 2 - 50, height / 6 + 50, 100, 20, "Done"));
        super.init();
    }

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        from.render(-1, -1, tickDelta);
        renderBackground();
        super.render(mouseX, mouseY, tickDelta);
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                EndFightMod.setInventory(MinecraftClient.getInstance().field_3805, obj);
                break;
            case 1:
                MinecraftServer server = MinecraftServer.getServer();
                if (server != null) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(MinecraftClient.getInstance().getSession().getUsername());
                    if (player != null) {
                        EndFightMod.giveInventory(player, obj);
                    }
                }
                break;
            case 2:
                MinecraftClient.getInstance().openScreen(from);
                break;
        }
    }
}
