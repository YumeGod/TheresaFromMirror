package cn.loli.client.utils;

import cn.loli.client.Main;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Vec3;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Proxy;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    private static final Random RANDOM = new Random();
    public static final Minecraft mc = Minecraft.getMinecraft();


    protected final double RAD_TO_DEG = 180.0 / Math.PI;
    protected final double DEG_TO_RAD = Math.PI / 180.0;

    /**
     * This function returns a random value between min and max
     * If <code>min >= max</code> the function will return min
     *
     * @param min Minimal
     * @param max Maximal
     * @return The value
     */
    public static int random(int min, int max) {
        if (max <= min) return min;

        return RANDOM.nextInt(max - min) + min;
    }

    public static Session createSession(String username, String password, @NotNull Proxy proxy) throws AuthenticationException {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(proxy, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);

        auth.setUsername(username);
        auth.setPassword(password);

        auth.logIn();
        return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
    }

    public static Session createOfflineSession(String username, @NotNull Proxy proxy) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(proxy, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);

        auth.logOut();
        return new Session(username, username, "0", "legacy");
    }

    /**
     * @return The direction of the player's movement in radians
     */
    public static double getDirection() {
        Minecraft mc = Minecraft.getMinecraft();

        float yaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0.0F) yaw += 180.0F;

        float forward = 1.0F;

        if (mc.thePlayer.moveForward < 0.0F) forward = -0.5F;
        else if (mc.thePlayer.moveForward > 0.0F) forward = 0.5F;

        if (mc.thePlayer.moveStrafing > 0.0F) yaw -= 90.0F * forward;

        if (mc.thePlayer.moveStrafing < 0.0F) yaw += 90.0F * forward;

        return Math.toRadians(yaw);
    }

    public static String stripColorCodes(String original) {
        return original.replaceAll("/\u00a7[0-9A-FK-OR]+/i", "");
    }

    public double getYaw(boolean strafing) {
        float rotationYaw = strafing ? Minecraft.getMinecraft().thePlayer.rotationYawHead : Minecraft.getMinecraft().thePlayer.rotationYaw;
        float forward = 1F;

        final double moveForward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        final double moveStrafing = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;

        if (moveForward < 0) {
            rotationYaw += 180F;
        }

        if (moveForward < 0) {
            forward = -0.5F;
        } else if (moveForward > 0) {
            forward = 0.5F;
        }

        if (moveStrafing > 0) {
            rotationYaw -= 90F * forward;
        } else if (moveStrafing < 0) {
            rotationYaw += 90F * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    public static InputStream getFileFromResourceAsStream(String path) {
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        if (inputStream == null) {
            throw new IllegalArgumentException("File not found! " + path);
        } else {
            return inputStream;
        }
    }

    public static boolean isEntityChild(Entity entity) {
        return entity instanceof EntityAgeable && ((EntityAgeable) entity).isChild();
    }

    public static String getHexStringFromColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        return String.format("#%02X%02X%02X%02X", r, g, b, a);
    }

    public static boolean between(int i, int minValueInclusive, int maxValueInclusive) {
        return (i >= minValueInclusive && i <= maxValueInclusive);
    }

    public int randomInRange(int min, int max) {
        if (min > max) {
            System.err.println("The minimal value cannot be higher than the max value");
            return min;
        }
        max -= min;
        return (int) Math.round(Math.random() * (max)) + min;
    }

    public double randomInRange(double min, double max) {
        if (min > max) {
            System.err.println("The minimal value cannot be higher than the max value");
            return min;
        }
        max -= min;
        return (Math.random() * (max)) + min;
    }


    public static <T extends Number> T clamp(T value, T minimum, T maximum) {
        if (value instanceof Integer) {
            if (value.intValue() > maximum.intValue()) {
                value = maximum;
            } else if (value.intValue() < minimum.intValue()) {
                value = minimum;
            }
        } else if (value instanceof Float) {
            if (value.floatValue() > maximum.floatValue()) {
                value = maximum;
            } else if (value.floatValue() < minimum.floatValue()) {
                value = minimum;
            }
        } else if (value instanceof Double) {
            if (value.doubleValue() > maximum.doubleValue()) {
                value = maximum;
            } else if (value.doubleValue() < minimum.doubleValue()) {
                value = minimum;
            }
        } else if (value instanceof Long) {
            if (value.longValue() > maximum.longValue()) {
                value = maximum;
            } else if (value.longValue() < minimum.longValue()) {
                value = minimum;
            }
        } else if (value instanceof Short) {
            if (value.shortValue() > maximum.shortValue()) {
                value = maximum;
            } else if (value.shortValue() < minimum.shortValue()) {
                value = minimum;
            }
        } else if (value instanceof Byte) {
            if (value.byteValue() > maximum.byteValue()) {
                value = maximum;
            } else if (value.byteValue() < minimum.byteValue()) {
                value = minimum;
            }
        }

        return value;
    }

    public Block getBlockUnderPlayer(float offsetY) {
        return getBlockUnderPlayer(mc.thePlayer, offsetY);
    }

    Block getBlockUnderPlayer(EntityPlayer player, float offsetY) {
        return mc.theWorld.getBlockState(new BlockPos(player.posX, player.posY - offsetY, player.posZ)).getBlock();
    }

    public boolean isKeyDown(int key) {
        if (key < 0) {
            int i = Mouse.getEventButton();
            return i - 100 == key;
        } else {
            return Keyboard.isKeyDown(key);
        }
    }

    public boolean isValidEntityName(Entity entity) {
        if (!(entity instanceof EntityPlayer))
            return true;
        final String name = getName((EntityPlayer) entity);
        return name.length() <= 16 && name.length() >= 3 && name.matches("[a-zA-Z0-9_]*");
    }

    String getName(EntityPlayer player) {
        return player.getGameProfile().getName();
    }


    final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    double getRandomDouble(double min, double max) {
        return threadLocalRandom.nextDouble(min, max);
    }

    int getRandomInteger(int min, int max) {
        return threadLocalRandom.nextInt(min, max);
    }

    public double getRandomGaussian(double average) {
        return threadLocalRandom.nextGaussian() * average;
    }

    float getRandomFloat(float min, float max) {
        return (float) threadLocalRandom.nextDouble(min, max);
    }

    public double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                        .doubleValue();
            else return new BigDecimal(floored)
                    .doubleValue();
        }
    }

    public double smooth (double max, double min, double time, boolean randomizing, double randomStrength) {
        min += 1;
        double radians = Math.toRadians((System.currentTimeMillis() * time % 360) - 180);
        double base = (Math.tanh(radians) + 1) / 2;
        double delta = max - min;
        delta *= base;
        double value = min + delta;
        if(randomizing)value *= ThreadLocalRandom.current().nextDouble(randomStrength,1);
        return Math.ceil(value *1000) / 1000;
    }


    /**
     * Adapted from {@link Entity#getVectorForRotation}
     */
    protected Vec3 getPointedVec(final float yaw,
                                 final float pitch) {
        final double theta = -Math.cos(-pitch * DEG_TO_RAD);

        return new Vec3(Math.sin(-yaw * DEG_TO_RAD - Math.PI) * theta,
                Math.sin(-pitch * DEG_TO_RAD),
                Math.cos(-yaw * DEG_TO_RAD - Math.PI) * theta);
    }


    protected Vec3 getDstVec(final Vec3 src,
                             final float yaw,
                             final float pitch,
                             final double reach) {
        final Vec3 rotationVec = getPointedVec(yaw, pitch);
        return src.addVector(rotationVec.xCoord * reach,
                rotationVec.yCoord * reach,
                rotationVec.zCoord * reach);
    }

    public float calculateYawFromSrcToDst(final float yaw,
                                          final double srcX,
                                          final double srcZ,
                                          final double dstX,
                                          final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        final float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0F;
        return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
    }

    protected double distance(final double srcX, final double srcY, final double srcZ,
                                  final double dstX, final double dstY, final double dstZ) {
        final double xDist = dstX - srcX;
        final double yDist = dstY - srcY;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
    }

    public double distance(final double srcX, final double srcZ,
                           final double dstX, final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }
}
