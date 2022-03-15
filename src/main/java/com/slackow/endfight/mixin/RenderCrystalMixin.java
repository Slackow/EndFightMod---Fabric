package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import com.slackow.endfight.util.FakeArrow;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EnderCrystalEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.slackow.endfight.util.Medium.drawBox;

@Mixin(EnderCrystalEntityRenderer.class)
public class RenderCrystalMixin {
    @Inject(method = "render(Lnet/minecraft/entity/EndCrystalEntity;DDDFF)V", at = @At("TAIL"))
    private void render(EndCrystalEntity d, double e, double f, double g, float h, float par6, CallbackInfo ci) {
        if (!EntityRenderDispatcher.field_5192 && BigConfig.getSelectedConfig().arrowHelp) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerInventory inv = client.field_3805.inventory;
            ItemStack itemStack = inv.main[inv.selectedSlot];
            if (itemStack != null && itemStack.getItem() instanceof BowItem) {

                if (client.field_3805.method_3192() > 0) {
                    FakeArrow arrow = new FakeArrow(client.world, inv.player, 2f, 0.8f);
                    FakeArrow arrow2 = new FakeArrow(client.world, inv.player, 2f, -0.8f);
                    double prevDist;
                    double dist = Double.MAX_VALUE;
                    double loops = 10000;

                    double prevDist2;
                    double dist2 = Double.MAX_VALUE;


                    double dx;
                    double dy;
                    double dz;
                    double bound = d.width / 2.0F;
                    do {
                        prevDist = dist;
                        dx = d.x - arrow.x;
                        dy = d.y + d.height * 0.5 - arrow.y;
                        dz = d.z - arrow.z;
                        dist = (dx * dx)*2 + (dy * dy) + (dz * dz)*2;

                        arrow.tick();
                        loops--;
                    } while (prevDist > dist && loops > 0);
                    do {
                        prevDist2 = dist2;
                        dx = d.x - arrow2.x;
                        dy = d.y + d.height * 0.5 - arrow2.y;
                        dz = d.z - arrow2.z;
                        dist2 = (dx * dx)*2 + (dy * dy) + (dz * dz)*2;

                        arrow2.tick();
                        loops--;
                    } while (prevDist2 > dist2 && loops > 0);
                    int p = Math.min(255, (int) (((arrow.hasHitCrystal() ? 0 : prevDist) + (arrow2.hasHitCrystal() ? 0 : prevDist2)) * (16.0)));
                    int color = ((p) << 16) | ((255 - p) << 8);
                    Box box = Box.of(e - bound, f, g - bound, e + bound, f + (double)d.height, g + bound);
                    drawBox(color, box);


//                    double x = arrow.x + e - d.x;
//                    double y = arrow.y + f - d.y;
//                    double z = arrow.z + g - d.z;
//                    Box arrowBox = Box.of(x, y, z, x, y, z).expand(0.5, 0.5, 0.5);
//                    drawBox(0x0000FF, arrowBox);
//                    x = arrow2.x + e - d.x;
//                    y = arrow2.y + f - d.y;
//                    z = arrow2.z + g - d.z;
//                    arrowBox = Box.of(x, y, z, x, y, z).expand(0.5, 0.5, 0.5);
//                    drawBox(0x0000FF, arrowBox);
                }
            }
        }
    }

    private double range(double delta, double range) {
        if (Math.abs(delta) < range) {
            return 0;
        }
        return delta > 0 ? delta - range : delta + range;
    }

    private double r(double x) {
        return Math.rint(x * 100) / 100.0;
    }
}
