package com.slackow.endfight.util;

import com.mojang.blaze3d.platform.GLX;
import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.slackow.endfight.EndFightCommand;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Box;
import org.lwjgl.opengl.GL11;

import java.nio.*;
import java.util.List;

import static com.slackow.endfight.speedrunigt.EndFightCategory.END_FIGHT_CATEGORY;

/**
 * A Hacky way of transferring data between client and server, don't depend on anything useful actually being here
 * it's more of an "If it's present, take it" kinda deal. I'm not too experienced with not mixing these two, but
 * I know I shouldn't do it, so I figured maybe it'd be better if I just put it all in one place because I don't know
 * how proxies work lmao.
 */
public class Medium {
    public static double targetX;
    public static double targetY;
    public static double targetZ;
    public static List<EndFightCommand> commandMap;
    private static boolean switched = false;

    // method_4328
    // I didn't know where else to place this method it doesn't really fit here
    public static void drawBox(int color, Box box) {
        GL11.glDepthMask(false);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        method_6886(box, color);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }
    // Taken from 1.7.10
    public static void method_6886(Box box, int i) {
        Tessellator var2 = Tessellator.INSTANCE;
        var2.method_1408(3);
        if (i != -1) {
            var2.method_1413(i);
        }

        var2.method_1398(box.minX, box.minY, box.minZ);
        var2.method_1398(box.maxX, box.minY, box.minZ);
        var2.method_1398(box.maxX, box.minY, box.maxZ);
        var2.method_1398(box.minX, box.minY, box.maxZ);
        var2.method_1398(box.minX, box.minY, box.minZ);
        var2.method_1396();
        var2.method_1408(3);
        if (i != -1) {
            var2.method_1413(i);
        }

        var2.method_1398(box.minX, box.maxY, box.minZ);
        var2.method_1398(box.maxX, box.maxY, box.minZ);
        var2.method_1398(box.maxX, box.maxY, box.maxZ);
        var2.method_1398(box.minX, box.maxY, box.maxZ);
        var2.method_1398(box.minX, box.maxY, box.minZ);
        var2.method_1396();
        var2.method_1408(1);
        if (i != -1) {
            var2.method_1413(i);
        }

        var2.method_1398(box.minX, box.minY, box.minZ);
        var2.method_1398(box.minX, box.maxY, box.minZ);
        var2.method_1398(box.maxX, box.minY, box.minZ);
        var2.method_1398(box.maxX, box.maxY, box.minZ);
        var2.method_1398(box.maxX, box.minY, box.maxZ);
        var2.method_1398(box.maxX, box.maxY, box.maxZ);
        var2.method_1398(box.minX, box.minY, box.maxZ);
        var2.method_1398(box.minX, box.maxY, box.maxZ);
        var2.method_1396();
    }

    @SuppressWarnings("ALL")
    @Environment(EnvType.CLIENT)
    public static class Tessellator {
        private ByteBuffer field_1948;
        private IntBuffer field_1949;
        private FloatBuffer field_1950;
        private ShortBuffer field_1951;
        private int[] field_1952;
        private int field_1953;
        private double field_1954;
        private double field_1955;
        private int field_1956;
        private int field_1957;
        private boolean field_1958;
        private boolean field_1959;
        private boolean field_1960;
        private boolean field_1961;
        private int field_1962;
        private int field_1963;
        private boolean field_1964;
        private int field_1965;
        private double field_1966;
        private double field_1967;
        private double field_1968;
        private int field_1969;
        public static final Tessellator INSTANCE = new Tessellator(2097152);
        private boolean field_1970;
        private int field_1944;

        private Tessellator(int bufferCapacity) {
            this.field_1944 = bufferCapacity;
            this.field_1948 = GlAllocationUtils.allocateByteBuffer(bufferCapacity * 4);
            this.field_1949 = this.field_1948.asIntBuffer();
            this.field_1950 = this.field_1948.asFloatBuffer();
            this.field_1951 = this.field_1948.asShortBuffer();
            this.field_1952 = new int[bufferCapacity];
        }

        public int method_1396() {
            if (!this.field_1970) {
                throw new IllegalStateException("Not tesselating!");
            } else {
                this.field_1970 = false;
                if (this.field_1953 > 0) {
                    this.field_1949.clear();
                    this.field_1949.put(this.field_1952, 0, this.field_1962);
                    this.field_1948.position(0);
                    this.field_1948.limit(this.field_1962 * 4);
                    if (this.field_1959) {
                        this.field_1950.position(3);
                        GL11.glTexCoordPointer(2, 32, this.field_1950);
                        GL11.glEnableClientState(32888);
                    }

                    if (this.field_1960) {
                        GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
                        this.field_1951.position(14);
                        GL11.glTexCoordPointer(2, 32, this.field_1951);
                        GL11.glEnableClientState(32888);
                        GLX.gl13ClientActiveTexture(GLX.textureUnit);
                    }

                    if (this.field_1958) {
                        this.field_1948.position(20);
                        GL11.glColorPointer(4, true, 32, this.field_1948);
                        GL11.glEnableClientState(32886);
                    }

                    if (this.field_1961) {
                        this.field_1948.position(24);
                        GL11.glNormalPointer(32, this.field_1948);
                        GL11.glEnableClientState(32885);
                    }

                    this.field_1950.position(0);
                    GL11.glVertexPointer(3, 32, this.field_1950);
                    GL11.glEnableClientState(32884);
                    GL11.glDrawArrays(this.field_1965, 0, this.field_1953);
                    GL11.glDisableClientState(32884);
                    if (this.field_1959) {
                        GL11.glDisableClientState(32888);
                    }

                    if (this.field_1960) {
                        GLX.gl13ClientActiveTexture(GLX.lightmapTextureUnit);
                        GL11.glDisableClientState(32888);
                        GLX.gl13ClientActiveTexture(GLX.textureUnit);
                    }

                    if (this.field_1958) {
                        GL11.glDisableClientState(32886);
                    }

                    if (this.field_1961) {
                        GL11.glDisableClientState(32885);
                    }
                }

                int var1 = this.field_1962 * 4;
                this.method_1412();
                return var1;
            }
        }

//        public class_1863 method_6903(float f, float g, float h) {
//            int[] var4 = new int[this.field_1962];
//            PriorityQueue var5 = new PriorityQueue(this.field_1962, new class_1861(this.field_1952, f + (float)this.field_1966, g + (float)this.field_1967, h + (float)this.field_1968));
//            byte var6 = 32;
//
//            int var7;
//            for(var7 = 0; var7 < this.field_1962; var7 += var6) {
//                var5.add(var7);
//            }
//
//            for(var7 = 0; !var5.isEmpty(); var7 += var6) {
//                int var8 = (Integer)var5.remove();
//
//                for(int var9 = 0; var9 < var6; ++var9) {
//                    var4[var7 + var9] = this.field_1952[var8 + var9];
//                }
//            }
//
//            System.arraycopy(var4, 0, this.field_1952, 0, var4.length);
//            return new class_1863(var4, this.field_1962, this.field_1953, this.field_1959, this.field_1960, this.field_1961, this.field_1958);
//        }
//
//        public void method_6904(class_1863 arg) {
//            System.arraycopy(arg.method_6905(), 0, this.field_1952, 0, arg.method_6905().length);
//            this.field_1962 = arg.method_6906();
//            this.field_1953 = arg.method_6907();
//            this.field_1959 = arg.method_6908();
//            this.field_1960 = arg.method_6909();
//            this.field_1958 = arg.method_6911();
//            this.field_1961 = arg.method_6910();
//        }

        private void method_1412() {
            this.field_1953 = 0;
            this.field_1948.clear();
            this.field_1962 = 0;
            this.field_1963 = 0;
        }

        public void method_1405() {
            this.method_1408(7);
        }

        public void method_1408(int i) {
            if (this.field_1970) {
                throw new IllegalStateException("Already tesselating!");
            } else {
                this.field_1970 = true;
                this.method_1412();
                this.field_1965 = i;
                this.field_1961 = false;
                this.field_1958 = false;
                this.field_1959 = false;
                this.field_1960 = false;
                this.field_1964 = false;
            }
        }

        public void method_1397(double d, double e) {
            this.field_1959 = true;
            this.field_1954 = d;
            this.field_1955 = e;
        }

        public void method_1411(int i) {
            this.field_1960 = true;
            this.field_1956 = i;
        }

        public void method_1400(float f, float g, float h) {
            this.method_1403((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F));
        }

        public void method_1401(float f, float g, float h, float i) {
            this.method_1404((int)(f * 255.0F), (int)(g * 255.0F), (int)(h * 255.0F), (int)(i * 255.0F));
        }

        public void method_1403(int i, int j, int k) {
            this.method_1404(i, j, k, 255);
        }

        public void method_1404(int i, int j, int k, int l) {
            if (!this.field_1964) {
                if (i > 255) {
                    i = 255;
                }

                if (j > 255) {
                    j = 255;
                }

                if (k > 255) {
                    k = 255;
                }

                if (l > 255) {
                    l = 255;
                }

                if (i < 0) {
                    i = 0;
                }

                if (j < 0) {
                    j = 0;
                }

                if (k < 0) {
                    k = 0;
                }

                if (l < 0) {
                    l = 0;
                }

                this.field_1958 = true;
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.field_1957 = l << 24 | k << 16 | j << 8 | i;
                } else {
                    this.field_1957 = i << 24 | j << 16 | k << 8 | l;
                }

            }
        }

        public void method_6902(byte b, byte c, byte d) {
            this.method_1403(b & 255, c & 255, d & 255);
        }

        public void method_1399(double d, double e, double f, double g, double h) {
            this.method_1397(g, h);
            this.method_1398(d, e, f);
        }

        public void method_1398(double d, double e, double f) {
            ++this.field_1963;
            if (this.field_1959) {
                this.field_1952[this.field_1962 + 3] = Float.floatToRawIntBits((float)this.field_1954);
                this.field_1952[this.field_1962 + 4] = Float.floatToRawIntBits((float)this.field_1955);
            }

            if (this.field_1960) {
                this.field_1952[this.field_1962 + 7] = this.field_1956;
            }

            if (this.field_1958) {
                this.field_1952[this.field_1962 + 5] = this.field_1957;
            }

            if (this.field_1961) {
                this.field_1952[this.field_1962 + 6] = this.field_1969;
            }

            this.field_1952[this.field_1962 + 0] = Float.floatToRawIntBits((float)(d + this.field_1966));
            this.field_1952[this.field_1962 + 1] = Float.floatToRawIntBits((float)(e + this.field_1967));
            this.field_1952[this.field_1962 + 2] = Float.floatToRawIntBits((float)(f + this.field_1968));
            this.field_1962 += 8;
            ++this.field_1953;
            if (this.field_1953 % 4 == 0 && this.field_1962 >= this.field_1944 - 32) {
                this.method_1396();
                this.field_1970 = true;
            }

        }

        public void method_1413(int i) {
            int var2 = i >> 16 & 255;
            int var3 = i >> 8 & 255;
            int var4 = i & 255;
            this.method_1403(var2, var3, var4);
        }

        public void method_1402(int i, int j) {
            int var3 = i >> 16 & 255;
            int var4 = i >> 8 & 255;
            int var5 = i & 255;
            this.method_1404(var3, var4, var5, j);
        }

        public void method_1409() {
            this.field_1964 = true;
        }

        public void method_1407(float f, float g, float h) {
            this.field_1961 = true;
            byte var4 = (byte)((int)(f * 127.0F));
            byte var5 = (byte)((int)(g * 127.0F));
            byte var6 = (byte)((int)(h * 127.0F));
            this.field_1969 = var4 & 255 | (var5 & 255) << 8 | (var6 & 255) << 16;
        }

        public void method_1406(double d, double e, double f) {
            this.field_1966 = d;
            this.field_1967 = e;
            this.field_1968 = f;
        }

        public void method_1410(float f, float g, float h) {
            this.field_1966 += (double)f;
            this.field_1967 += (double)g;
            this.field_1968 += (double)h;
        }
    }


    /**
     * I Need to make one of these methods anytime I use SRIGT classes inside a mixin, or you et an error. :/
     */
    public static void completeTimerIfEndFight() {
        InGameTimer timer = InGameTimer.getInstance();
        if (timer.getCategory() == END_FIGHT_CATEGORY && timer.isPlaying()) {
            InGameTimer.complete();
        }
    }


    public static void onGameJoinIGT() {
        InGameTimer timer = InGameTimer.getInstance();
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (!switched) {
            switched = true;
            timer.setCategory(END_FIGHT_CATEGORY, false);
        }
        if (timer.getCategory() == END_FIGHT_CATEGORY) {
            player.sendMessage(new LiteralText("Loaded End Fight Category w/ SpeedrunIGT"));
        } else {
            player.sendMessage(new LiteralText("Warning: End Fight Category disabled in SpeedrunIGT"));
        }
    }
}
