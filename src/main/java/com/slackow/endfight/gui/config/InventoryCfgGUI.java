package com.slackow.endfight.gui.config;

import com.slackow.endfight.EndFightMod;
import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Kit;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.ControllablePlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class InventoryCfgGUI extends Screen {
    private final Screen from;
    private final Kit obj;

    // If you are reading this code I deeply apologize to you personally
    public InventoryCfgGUI(Screen from, Kit obj) {
        this.from = from;
        this.obj = obj;
        ItemStack[] raw = Arrays.stream(obj.contents).mapToObj(EndFightMod::intToItem).toArray(ItemStack[]::new);
        items = new ItemStack[36];
        System.arraycopy(raw, 0, items, 0, 36);
        armor = new ItemStack[4];
        System.arraycopy(raw, 36, armor, 0, 4);
    }

    private final ItemStack[] items;
    private final ItemStack[] armor;

    @SuppressWarnings("unchecked")
    @Override
    public void init() {
        buttons.add(new ButtonWidget(0, width / 2 - 100, height / 6 + 150, 100, 20, "Set Current"));
        buttons.add(new ButtonWidget(1, width / 2, height / 6 + 150, 100, 20, "Get Current"));
        buttons.add(new ButtonWidget(2, width / 2 - 100, height / 6 + 174, 200, 20, I18n.translate("gui.done")));
        super.init();
    }

    protected static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    protected int backgroundWidth = 176;
    protected int backgroundHeight = 166;

    @Override
    public void render(int mouseX, int mouseY, float tickDelta) {
        renderBackground();
        drawCenteredString(textRenderer, "Inventory", width / 2, height / 6 - 2, 0xFFFFFF);
        drawCenteredString(textRenderer, BigConfig.getSelectedConfig().getName(), width / 2, height / 6 + 10, 0xFFFFFF);
        super.render(mouseX, mouseY, tickDelta);
        client.getTextureManager().bindTexture(INVENTORY_TEXTURE);
        int var4 = (this.width - this.backgroundWidth) / 2;
        int var5 = (this.height / 3 + 200 - this.backgroundHeight) / 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexture(var4, var5 - 50 + 80, 0, 80, this.backgroundWidth, this.backgroundHeight - 80);
        this.drawTexture(var4, var5 - 50 + 76, 0, 0, this.backgroundWidth, 4);
        DiffuseLighting.enable();
        for (int i = 0; i < items.length; i++) {

            int x = width / 2 + (i % 9 - 4) * 18 - 8;
            int y = height / 6 + 100 + (i / 9) * 18 + (i < 9 ? 76 : 0) - 67;
            if (items[i] != null) {
                // draw item
                itemRenderer.method_5762(textRenderer,
                        client.getTextureManager(),
                        items[i],
                        x,
                        y);
                // draw count
                itemRenderer.method_1549(textRenderer,
                        client.getTextureManager(),
                        items[i],
                        x,
                        y);
            }
        }
        for (int i = 0; i < armor.length; i++) {
            int x = width / 2 + (i - 2) * 18;
            int y = height / 6 + 100 + 76 - 67 - 80;
            if (armor[i] != null) {
                // draw item
                itemRenderer.method_5762(textRenderer,
                        client.getTextureManager(),
                        armor[i],
                        x,
                        y);
                // draw count
                itemRenderer.method_1549(textRenderer,
                        client.getTextureManager(),
                        armor[i],
                        x,
                        y);
            }
        }
        DiffuseLighting.disable();

    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        switch (button.id) {
            case 0:
                ControllablePlayerEntity thePlayer = MinecraftClient.getInstance().field_3805;
                if (thePlayer != null) {
                    EndFightMod.setInventory(thePlayer, obj);
                    client.setScreen(new InventoryCfgGUI(from, obj));
                    BigConfig.save();
                }
                break;
            case 1:
                MinecraftServer server = MinecraftServer.getServer();
                if (server != null) {
                    ServerPlayerEntity player = server.getPlayerManager().getPlayer(MinecraftClient.getInstance().getSession().getUsername());
                    if (player != null) {
                        EndFightMod.giveInventory(player, obj);
                        client.setScreen(null);
                    }
                }
                break;
            case 2:
                client.setScreen(from);
                break;
        }
    }
}
