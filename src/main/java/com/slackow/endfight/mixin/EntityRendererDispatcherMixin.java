package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static net.minecraft.client.render.entity.EntityRenderDispatcher.field_5192;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererDispatcherMixin {
    @Redirect(method = "method_6913", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z"))
    private boolean render(Entity d){
        if (field_5192 && d instanceof EndCrystalEntity && BigConfig.getSelectedConfig().arrowHelp) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerInventory inv = client.field_3805.inventory;
            ItemStack itemStack = inv.main[inv.selectedSlot];
            if (itemStack != null && itemStack.getItem() instanceof BowItem && client.field_3805.method_3192() > 0) {
                return true;
            }
        }
        return d.isInvisible();
    }
}
