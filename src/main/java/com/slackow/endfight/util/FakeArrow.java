//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.slackow.endfight.util;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.particle.ParticleType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EndCrystalEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("all")
public class FakeArrow extends Entity implements Projectile {
    private float upward;
    private int blockX = -1;
    private int blockY = -1;
    private int blockZ = -1;
    private Block block;
    private int blockData;
    private boolean inGround;
    public int pickup;
    public int shake;
    public Entity field_4026;
    private int lifeTicks;
    private int field_4022;
    private double damage = 2.0D;
    private int field_4024;

    private boolean hitCrystal = false;

    public FakeArrow(World world) {
        super(world);
        this.renderDistanceMultiplier = 10.0D;
        this.setBounds(0.5F, 0.5F);
    }

    public FakeArrow(World world, double x, double y, double z) {
        super(world);
        this.renderDistanceMultiplier = 10.0D;
        this.setBounds(0.5F, 0.5F);
        this.updatePosition(x, y, z);
    }

    public FakeArrow(World world, LivingEntity livingEntity, LivingEntity livingEntity2, float f, float g) {
        super(world);
        this.renderDistanceMultiplier = 10.0D;
        this.field_4026 = livingEntity;
        if (livingEntity instanceof PlayerEntity) {
            this.pickup = 1;
        }

        this.y = livingEntity.y + (double)livingEntity.getEyeHeight() - 0.10000000149011612D;
        double d = livingEntity2.x - livingEntity.x;
        double e = livingEntity2.getBoundingBox().minY + (double)(livingEntity2.height / 3.0F) - this.y;
        double h = livingEntity2.z - livingEntity.z;
        double i = (double)MathHelper.sqrt(d * d + h * h);
        if (!(i < 1.0E-7D)) {
            float j = (float)(MathHelper.atan2(h, d) * 180.0D / 3.1415927410125732D) - 90.0F;
            float k = (float)(-(MathHelper.atan2(e, i) * 180.0D / 3.1415927410125732D));
            double l = d / i;
            double m = h / i;
            this.refreshPositionAndAngles(livingEntity.x + l, this.y, livingEntity.z + m, j, k);
            float n = (float)(i * 0.20000000298023224D);
            this.setVelocity(d, e + (double)n, h, f, g);
        }
    }

    public FakeArrow(World world, LivingEntity livingEntity, float f, float upward) {
        super(world);
        this.upward = upward;
        this.renderDistanceMultiplier = 10.0D;
        this.field_4026 = livingEntity;
        if (livingEntity instanceof PlayerEntity) {
            this.pickup = 1;
        }

        this.setBounds(0.5F, 0.5F);
        this.refreshPositionAndAngles(livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight(), livingEntity.z, livingEntity.yaw, livingEntity.pitch);
        this.x -= (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.y -= 0.10000000149011612D;
        this.z -= (double)(MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.updatePosition(this.x, this.y, this.z);
        this.velocityX = (double)(-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityZ = (double)(MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.velocityY = (double)(-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
        this.setVelocity(this.velocityX, this.velocityY, this.velocityZ, f * 1.5F, 1.0F);
    }

    protected void initDataTracker() {
        this.dataTracker.track(16, (byte)0);
    }

    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        float f = MathHelper.sqrt(x * x + y * y + z * z);
        x /= (double)f;
        y /= (double)f;
        z /= (double)f;
        x += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)divergence;
        y += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)divergence;
        z += this.random.nextGaussian() * (double)(this.random.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)divergence;
        x *= (double)speed;
        y *= (double)speed;
        z *= (double)speed;
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        float g = MathHelper.sqrt(x * x + z * z);
        this.prevYaw = this.yaw = (float)(MathHelper.atan2(x, z) * 180.0D / 3.1415927410125732D);
        this.prevPitch = this.pitch = (float)(MathHelper.atan2(y, (double)g) * 180.0D / 3.1415927410125732D);
        this.lifeTicks = 0;
    }

    @Environment(EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.updatePosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Environment(EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float f = MathHelper.sqrt(x * x + z * z);
            this.prevYaw = this.yaw = (float)(MathHelper.atan2(x, z) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float)(MathHelper.atan2(y, (double)f) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch;
            this.prevYaw = this.yaw;
            this.refreshPositionAndAngles(this.x, this.y, this.z, this.yaw, this.pitch);
            this.lifeTicks = 0;
        }

    }

    public void tick() {
        super.tick();
        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
            float f = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.prevYaw = this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0D / 3.1415927410125732D);
            this.prevPitch = this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)f) * 180.0D / 3.1415927410125732D);
        }

        BlockPos blockPos = new BlockPos(this.blockX, this.blockY, this.blockZ);
        BlockState blockState = this.world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        if (block.getMaterial() != Material.AIR) {
            block.setBoundingBox(this.world, blockPos);
            Box box = block.getCollisionBox(this.world, blockPos, blockState);
            if (box != null && box.contains(new Vec3d(this.x, this.y, this.z))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int i = block.getData(blockState);
            if (block == this.block && i == this.blockData) {
                ++this.lifeTicks;
                if (this.lifeTicks >= 1200) {
                    this.remove();
                }
            } else {
                this.inGround = false;
                this.velocityX *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityY *= (double)(this.random.nextFloat() * 0.2F);
                this.velocityZ *= (double)(this.random.nextFloat() * 0.2F);
                this.lifeTicks = 0;
                this.field_4022 = 0;
            }

        } else {
            ++this.field_4022;
            Vec3d vec3d = new Vec3d(this.x, this.y, this.z);
            Vec3d vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            HitResult hitResult = this.world.rayTrace(vec3d, vec3d2, false, true, false);
            vec3d = new Vec3d(this.x, this.y, this.z);
            vec3d2 = new Vec3d(this.x + this.velocityX, this.y + this.velocityY, this.z + this.velocityZ);
            if (hitResult != null) {
                vec3d2 = new Vec3d(hitResult.pos.x, hitResult.pos.y, hitResult.pos.z);
            }

            Entity entity = null;
            List<Entity> list = this.world.getEntitiesIn(this, this.getBoundingBox().incrementAll(this.velocityX, this.velocityY, this.velocityZ).expand(1.0D, 1.0D, 1.0D));
            double d = 0.0D;

            int n;
            float q;
            for(n = 0; n < list.size(); ++n) {
                Entity entity2 = (Entity)list.get(n);
                if (entity2.collides() && (entity2 != this.field_4026 || this.field_4022 >= 5)) {
                    q = 0.3F;
                    Box box2 = entity2.getBoundingBox().expand((double)q, (double)q, (double)q);
                    HitResult hitResult2 = box2.method_585(vec3d, vec3d2);
                    if (hitResult2 != null) {
                        double e = vec3d.squaredDistanceTo(hitResult2.pos);
                        if (e < d || d == 0.0D) {
                            entity = entity2;
                            d = e;
                        }
                    }
                }
            }

            if (entity != null) {
                hitResult = new HitResult(entity);
            }

            if (hitResult != null && hitResult.entitiy != null && hitResult.entitiy instanceof PlayerEntity) {
                PlayerEntity playerEntity = (PlayerEntity)hitResult.entitiy;
                if (playerEntity.abilities.invulnerable || this.field_4026 instanceof PlayerEntity && !((PlayerEntity)this.field_4026).shouldDamagePlayer(playerEntity)) {
                    hitResult = null;
                }
            }

            float o;
            float l;
            if (hitResult != null) {
                if (hitResult.entitiy != null) {
                    o = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    int k = MathHelper.ceil((double)o * this.damage);
                    if (this.method_3227()) {
                        k += this.random.nextInt(k / 2 + 2);
                    }



                    if (this.isOnFire() && !(hitResult.entitiy instanceof EndermanEntity)) {
                        hitResult.entitiy.setOnFireFor(5);
                    }

                    if (hitResult.entitiy instanceof EndCrystalEntity) {
                        hitCrystal = true;
                    }


                    this.velocityX *= -0.10000000149011612D;
                    this.velocityY *= -0.10000000149011612D;
                    this.velocityZ *= -0.10000000149011612D;
                    this.yaw += 180.0F;
                    this.prevYaw += 180.0F;
                    this.field_4022 = 0;

                } else {
                    BlockPos blockPos2 = hitResult.getBlockPos();
                    this.blockX = blockPos2.getX();
                    this.blockY = blockPos2.getY();
                    this.blockZ = blockPos2.getZ();
                    BlockState blockState2 = this.world.getBlockState(blockPos2);
                    this.block = blockState2.getBlock();
                    this.blockData = this.block.getData(blockState2);
                    this.velocityX = (double)((float)(hitResult.pos.x - this.x));
                    this.velocityY = (double)((float)(hitResult.pos.y - this.y));
                    this.velocityZ = (double)((float)(hitResult.pos.z - this.z));
                    q = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityY * this.velocityY + this.velocityZ * this.velocityZ);
                    this.x -= this.velocityX / (double)q * 0.05000000074505806D;
                    this.y -= this.velocityY / (double)q * 0.05000000074505806D;
                    this.z -= this.velocityZ / (double)q * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                    this.method_3226(false);
                    if (this.block.getMaterial() != Material.AIR) {
                        this.block.onEntityCollision(this.world, blockPos2, blockState2, this);
                    }
                }
            }

            if (this.method_3227()) {
                for(n = 0; n < 4; ++n) {
                    this.world.addParticle(ParticleType.CRIT, this.x + this.velocityX * (double)n / 4.0D, this.y + this.velocityY * (double)n / 4.0D, this.z + this.velocityZ * (double)n / 4.0D, -this.velocityX, -this.velocityY + 0.2D, -this.velocityZ, new int[0]);
                }
            }

            this.x += this.velocityX;
            this.y += this.velocityY;
            this.z += this.velocityZ;
            o = MathHelper.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
            this.yaw = (float)(MathHelper.atan2(this.velocityX, this.velocityZ) * 180.0D / 3.1415927410125732D);

            for(this.pitch = (float)(MathHelper.atan2(this.velocityY, (double)o) * 180.0D / 3.1415927410125732D); this.pitch - this.prevPitch < -180.0F; this.prevPitch -= 360.0F) {
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
            float p = 0.99F;
            q = 0.05F;
            if (this.isTouchingWater()) {
                for(int r = 0; r < 4; ++r) {
                    l = 0.25F;
                    this.world.addParticle(ParticleType.BUBBLE, this.x - this.velocityX * (double)l, this.y - this.velocityY * (double)l, this.z - this.velocityZ * (double)l, this.velocityX, this.velocityY, this.velocityZ, new int[0]);
                }

                p = 0.6F;
            }

            if (this.tickFire()) {
                this.extinguish();
            }

            this.velocityX *= (double)p;
            this.velocityY *= (double)p;
            this.velocityZ *= (double)p;
            this.velocityY -= (double)q;
            this.updatePosition(this.x, this.y, this.z);
            this.checkBlockCollision();
        }
    }

    public void writeCustomDataToTag(CompoundTag tag) {
        tag.putShort("xTile", (short)this.blockX);
        tag.putShort("yTile", (short)this.blockY);
        tag.putShort("zTile", (short)this.blockZ);
        tag.putShort("life", (short)this.lifeTicks);
        Identifier identifier = (Identifier)Block.REGISTRY.getIdentifier(this.block);
        tag.putString("inTile", identifier == null ? "" : identifier.toString());
        tag.putByte("inData", (byte)this.blockData);
        tag.putByte("shake", (byte)this.shake);
        tag.putByte("inGround", (byte)(this.inGround ? 1 : 0));
        tag.putByte("pickup", (byte)this.pickup);
        tag.putDouble("damage", this.damage);
    }

    public void readCustomDataFromTag(CompoundTag tag) {
        this.blockX = tag.getShort("xTile");
        this.blockY = tag.getShort("yTile");
        this.blockZ = tag.getShort("zTile");
        this.lifeTicks = tag.getShort("life");
        if (tag.contains("inTile", 8)) {
            this.block = Block.get(tag.getString("inTile"));
        } else {
            this.block = Block.getById(tag.getByte("inTile") & 255);
        }

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
            boolean bl = this.pickup == 1 || this.pickup == 2 && player.abilities.creativeMode;
            if (this.pickup == 1 && !player.inventory.insertStack(new ItemStack(Items.ARROW, 1))) {
                bl = false;
            }

            if (bl) {
                this.playSound("random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.sendPickup(this, 1);
                this.remove();
            }

        }
    }

    protected boolean canClimb() {
        return false;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public void method_3222(int i) {
        this.field_4024 = i;
    }

    public boolean isAttackable() {
        return false;
    }

    public float getEyeHeight() {
        return 0.0F;
    }

    public void method_3226(boolean bl) {
        byte b = this.dataTracker.getByte(16);
        if (bl) {
            this.dataTracker.setProperty(16, (byte)(b | 1));
        } else {
            this.dataTracker.setProperty(16, (byte)(b & -2));
        }

    }

    public boolean method_3227() {
        byte b = this.dataTracker.getByte(16);
        return (b & 1) != 0;
    }

    public boolean hasHitCrystal() {
        return hitCrystal;
    }
}
