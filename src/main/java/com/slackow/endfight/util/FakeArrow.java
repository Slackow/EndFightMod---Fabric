//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.slackow.endfight.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

@SuppressWarnings("ALL")
public class FakeArrow extends Entity implements Projectile {
    private final float upward;
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block block;
    private int blockData;
    private boolean inGround;
    public int pickup;
    public int shake;
    public Entity owner;
    private int life;
    private int field_4022;
    private double damage = 2.0D;
    private int punch;

    private boolean hitCrystal = false;

    public FakeArrow(World world, LivingEntity livingEntity, float f, float upward) {
        super(world);
        this.upward = upward;
        this.renderDistanceMultiplier = 10.0D;
        this.owner = livingEntity;
        if (livingEntity instanceof PlayerEntity) {
            this.pickup = 1;
        }

        this.setBounds(0.5F, 0.5F);
        this.refreshPositionAndAngles(livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight(), livingEntity.z, livingEntity.yaw, livingEntity.pitch);
        this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.y -= 0.10000000149011612D;
        this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.updatePosition(this.x, this.y, this.z);
        this.heightOffset = 0.0F;
        this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
        this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, f * 1.5F, 0.0F);
    }

    protected void initDataTracker() {
        this.dataTracker.track(16, (byte)0);
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        float var9 = MathHelper.sqrt(x * x + y * y + z * z);
        x /= (double)var9;
        y /= (double)var9;
        z /= (double)var9;
        x += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)divergence;
        y += upward * 0.007499999832361937D;
        z += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)divergence;
        x *= (double)speed;
        y *= (double)speed;
        z *= (double)speed;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float var10 = MathHelper.sqrt(x * x + z * z);
        this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0D / 3.1415927410125732D);
        this.prevPitch = this.pitch = (float)(Math.atan2(y, (double)var10) * 180.0D / 3.1415927410125732D);
        this.life = 0;
    }

    @Environment(EnvType.CLIENT)
    public void method_2488(double d, double e, double f, float g, float h, int i) {
        this.updatePosition(d, e, f);
        this.setRotation(g, h);
    }

    @Environment(EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var7 = MathHelper.sqrt(x * x + z * z);
            this.prevYaw = this.yaw = (float)(Math.atan2(x, z) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float)(Math.atan2(y, (double)var7) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            this.life = 0;
        }

    }

    public void tick() {
        super.tick();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float var1 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.prevYaw = this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float)(Math.atan2(this.velocityY, (double)var1) * 180.0D / 3.1415927410125732D);
        }

        Block var16 = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
        if (var16.getMaterial() != Material.AIR) {
            var16.onRender(this.world, this.blockX, this.blockY, this.blockZ);
            Box var2 = var16.getBoundingBox(this.world, this.blockX, this.blockY, this.blockZ);
            if (var2 != null && var2.contains(Vec3d.of(this.x, this.y, this.z))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int var18 = this.world.getBlockData(this.blockX, this.blockY, this.blockZ);
            if (var16 == this.block && var18 == this.blockData) {
                ++this.life;
                if (this.life == 1200) {
                    this.remove();
                }

            } else {
                this.inGround = false;
                this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
                this.life = 0;
                this.field_4022 = 0;
            }
        } else {
            ++this.field_4022;
            Vec3d var17 = Vec3d.of(this.x, this.y, this.z);
            Vec3d var3 = Vec3d.of(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            BlockHitResult var4 = this.world.rayTrace(var17, var3, false, true, false);
            var17 = Vec3d.of(this.x, this.y, this.z);
            var3 = Vec3d.of(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            if (var4 != null) {
                var3 = Vec3d.of(var4.pos.x, var4.pos.y, var4.pos.z);
            }

            Entity var5 = null;
            List var6 = this.world.getEntitiesIn(this, this.boundingBox.stretch(this.velocityX, this.velocityY, this.velocityZ).expand(1.0D, 1.0D, 1.0D));
            double var7 = 0.0D;

            int var9;
            float var11;
            for(var9 = 0; var9 < var6.size(); ++var9) {
                Entity var10 = (Entity)var6.get(var9);
                if (var10.collides() && (var10 != this.owner || this.field_4022 >= 5)) {
                    var11 = 0.3F;
                    Box var12 = var10.boundingBox.expand((double)var11, (double)var11, (double)var11);
                    BlockHitResult var13 = var12.method_585(var17, var3);
                    if (var13 != null) {
                        double var14 = var17.distanceTo(var13.pos);
                        if (var14 < var7 || var7 == 0.0D) {
                            var5 = var10;
                            var7 = var14;
                        }
                    }
                }
            }

            if (var5 != null) {
                var4 = new BlockHitResult(var5);
            }

            if (var4 != null && var4.entity instanceof PlayerEntity) {
                PlayerEntity var19 = (PlayerEntity)var4.entity;
                if (var19.abilities.invulnerable || this.owner instanceof PlayerEntity && !((PlayerEntity)this.owner).shouldDamagePlayer(var19)) {
                    var4 = null;
                }
            }

            float var20;
            float var26;
            if (var4 != null) {
                if (var4.entity != null) {
                    var20 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    int var21 = MathHelper.ceil((double)var20 * this.damage);
                    if (this.isCritical()) {
                        var21 += this.random.nextInt(var21 / 2 + 2);
                    }

                    DamageSource var22 = null;

                    if (this.isOnFire() && !(var4.entity instanceof EndermanEntity)) {
                        var4.entity.setOnFireFor(5);
                    }
                    if (var4.entity instanceof EndCrystalEntity) {
                        hitCrystal = true;
                    }

                    this.velocityX *= -0.10000000149011612D;
                    this.velocityY *= -0.10000000149011612D;
                    this.velocityZ *= -0.10000000149011612D;
                    this.yaw += 180.0F;
                    this.prevYaw += 180.0F;
                    this.field_4022 = 0;
                } else {
                    this.blockX = var4.x;
                    this.blockY = var4.y;
                    this.blockZ = var4.z;
                    this.block = this.world.getBlock(this.blockX, this.blockY, this.blockZ);
                    this.blockData = this.world.getBlockData(this.blockX, this.blockY, this.blockZ);
                    this.velocityX = (double)((float)(var4.pos.x - this.x));
                    this.velocityY = (double)((float)(var4.pos.y - this.y));
                    this.velocityZ = (double)((float)(var4.pos.z - this.z));
                    var20 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    this.x -= this.velocityX / (double)var20 * 0.05000000074505806D;
                    this.y -= this.velocityY / (double)var20 * 0.05000000074505806D;
                    this.z -= this.velocityZ / (double)var20 * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                    this.setCritical(false);
                    if (this.block.getMaterial() != Material.AIR) {
                        this.block.onEntityCollision(this.world, this.blockX, this.blockY, this.blockZ, this);
                    }
                }
            }

            if (this.isCritical()) {
                for(var9 = 0; var9 < 4; ++var9) {
                    this.world.spawnParticle("crit", this.x + this.velocityX * (double)var9 / 4.0D, this.y + this.velocityY * (double)var9 / 4.0D, this.z + this.velocityZ * (double)var9 / 4.0D, -this.velocityX, -this.velocityY + 0.2D, -this.velocityZ);
                }
            }

            this.x += this.velocityX;
            this.y += this.velocityY;
            this.z += this.velocityZ;
            var20 = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.yaw = (float)(Math.atan2(this.velocityX, this.velocityZ) * 180.0D / 3.1415927410125732D);

            for(this.pitch = (float)(Math.atan2(this.velocityY, (double)var20) * 180.0D / 3.1415927410125732D); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
            }

            while(this.pitch - this.prevPitch >= 180.0F) {
                this.prevPitch += 360.0F;
            }

            while(this.yaw - this.prevYaw < -180.0F) {
                this.prevYaw -= 360.0F;
            }

            while(this.yaw - this.prevYaw >= 180.0F) {
                this.prevYaw += 360.0F;
            }

            this.pitch = this.prevPitch + (this.pitch - this.prevPitch) * 0.2F;
            this.yaw = this.prevYaw + (this.yaw - this.prevYaw) * 0.2F;
            float var23 = 0.99F;
            var11 = 0.05F;
            if (this.isTouchingWater()) {
                for(int var25 = 0; var25 < 4; ++var25) {
                    var26 = 0.25F;
                    this.world.spawnParticle("bubble", this.x - this.velocityX * (double)var26, this.y - this.velocityY * (double)var26, this.z - this.velocityZ * (double)var26, this.velocityX, this.velocityY, this.velocityZ);
                }

                var23 = 0.8F;
            }

            if (this.tickFire()) {
                this.extinguish();
            }

            this.velocityX *= (double)var23;
            this.velocityY *= (double)var23;
            this.velocityZ *= (double)var23;
            this.velocityY -= (double)var11;
            this.updatePosition(this.x, this.y, this.z);
            this.checkBlockCollision();
        }
    }

    public boolean hasHitCrystal(){
        return hitCrystal;
    }

    public void writeCustomDataToNbt(NbtCompound tag) {
        tag.putShort("xTile", (short)this.blockX);
        tag.putShort("yTile", (short)this.blockY);
        tag.putShort("zTile", (short)this.blockZ);
        tag.putShort("life", (short)this.life);
        tag.putByte("inTile", (byte)Block.getIdByBlock(this.block));
        tag.putByte("inData", (byte)this.blockData);
        tag.putByte("shake", (byte)this.shake);
        tag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.putByte("pickup", (byte)this.pickup);
        tag.putDouble("damage", this.damage);
    }

    public void readCustomDataFromNbt(NbtCompound tag) {
        this.blockX = tag.getShort("xTile");
        this.blockY = tag.getShort("yTile");
        this.blockZ = tag.getShort("zTile");
        this.life = tag.getShort("life");
        this.block = Block.getById(tag.getByte("inTile") & 255);
        this.blockData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        if (tag.contains("damage", 99)) {
            this.damage = tag.getDouble("damage");
        }

        if (tag.contains("pickup", 99)) {
            this.pickup = tag.getByte("pickup");
        } else if (tag.contains("player", 99)) {
            this.pickup = tag.getBoolean("player") ? 1 : 0;
        }

    }

    public void onPlayerCollision(PlayerEntity player) {
        if (!this.world.isClient && this.inGround && this.shake <= 0) {
            boolean var2 = this.pickup == 1 || this.pickup == 2 && player.abilities.creativeMode;
            if (this.pickup == 1 && !player.inventory.insertStack(new ItemStack(Items.ARROW, 1))) {
                var2 = false;
            }

            if (var2) {
                this.playSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.sendPickup(this, 1);
                this.remove();
            }

        }
    }

    protected boolean canClimb() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public float method_2475() {
        return 0.0F;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setPunch(int punch) {
        this.punch = punch;
    }

    public boolean isAttackable() {
        return false;
    }

    public void setCritical(boolean critical) {
        byte var2 = this.dataTracker.getByte(16);
        if (critical) {
            this.dataTracker.setProperty(16, (byte)(var2 | 1));
        } else {
            this.dataTracker.setProperty(16, (byte)(var2 & -2));
        }

    }

    public boolean isCritical() {
        byte var1 = this.dataTracker.getByte(16);
        return (var1 & 1) != 0;
    }
}
