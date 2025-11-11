package com.slackow.endfight.mixin;

import com.slackow.endfight.EndFightMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public class ArrowFiredMixin {
    @Inject(method = "onUseStopped", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/AbstractArrowEntity;<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;F)V"))
    private void incremementArrowsUsed(ItemStack itemStack, World world, PlayerEntity playerEntity, int remainingTicks, CallbackInfo ci) {
        if (!world.isClient) EndFightMod.arrowsUsed++;
    }
}
