package com.slackow.endfight.mixin;

import com.slackow.endfight.config.BigConfig;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static net.minecraft.util.Formatting.RED;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalMixin extends Entity {
    public EndCrystalMixin(World world) {
        super(world);
    }


    private static char state = 'c';
    /**
     * @author Slackow
     */
    @Inject(method = "damage", at = @At("RETURN"))
    private void damage(DamageSource amount, float par2, CallbackInfoReturnable<Boolean> cir){
        if (cir.getReturnValue()) {
            if (!world.isClient && BigConfig.getSelectedConfig().dGodCrystals) {

                //noinspection unchecked
                List<PlayerEntity> list = this.world.playerEntities;
                PlayerEntity player = list.get(0);
                player.sendMessage(new LiteralText("§" + (state = (char) ('c' + '4' - state)) + "*BOOM*"));
                world.spawnEntity(new EndCrystalEntity(world, x, y, z));
            }
        }
    }
}
