package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.Medium;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRendererDispatcherMixin {

    @Shadow public abstract boolean method_10203();

    @Redirect(method = "method_6913", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z"))
    private boolean render(Entity d){
        if (method_10203() && d instanceof EndCrystalEntity && BigConfig.getSelectedConfig().arrowHelp) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerInventory inv = client.player.inventory;
            ItemStack itemStack = inv.main[inv.selectedSlot];
            if (itemStack != null && itemStack.getItem() instanceof BowItem && client.player.method_3192() > 0) {
                return true;
            }
        }
        return d.isInvisible();
    }
}
