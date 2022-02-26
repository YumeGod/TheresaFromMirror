package cn.loli.client.utils;

import cn.loli.client.Main;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Session;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.InputStream;
import java.net.Proxy;
import java.util.Random;

public class Utils {
    private static final Random RANDOM = new Random();
    public static final Minecraft mc = Minecraft.getMinecraft();

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

    /*
     * By DarkStorm
     */
    public static Point calculateMouseLocation() {
        Minecraft minecraft = Minecraft.getMinecraft();
        int scale = minecraft.gameSettings.guiScale;
        if (scale == 0)
            scale = 1000;
        int scaleFactor = 0;
        while (scaleFactor < scale && minecraft.displayWidth / (scaleFactor + 1) >= 320 && minecraft.displayHeight / (scaleFactor + 1) >= 240)
            scaleFactor++;
        return new Point(Mouse.getX() / scaleFactor, minecraft.displayHeight / scaleFactor - Mouse.getY() / scaleFactor - 1);
    }

    public static String stripColorCodes(String original) {
        return original.replaceAll("/\u00a7[0-9A-FK-OR]+/i", "");
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2875D;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0D + 0.2D * (double) (Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static double getJumpBoostModifier(double baseJumpHeight) {
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            baseJumpHeight += (float) (amplifier + 1) * 0.1F;
        }

        return baseJumpHeight;
    }

    public static double getYaw(boolean strafing) {
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

    public static Color getRainbow(int offset, int speed, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offset) % speed) / (float) speed;
        return Color.getHSBColor(hue, saturation, brightness);
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


    Color getTeamColor(EntityPlayer player) {
        ScorePlayerTeam scoreplayerteam = (ScorePlayerTeam) ((EntityPlayer) player).getTeam();
        int i = 16777215;

        if (scoreplayerteam != null && scoreplayerteam.getColorPrefix() != null) {
            String s = FontRenderer.getFormatFromString(scoreplayerteam.getColorPrefix());
            if (s.length() >= 2) {
                if (mc.getRenderManager().getFontRenderer() != null && mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1)) != 0)
                    i = mc.getRenderManager().getFontRenderer().getColorCode(s.charAt(1));
            }
        }
        final float f1 = (float) (i >> 16 & 255) / 255.0F;
        final float f2 = (float) (i >> 8 & 255) / 255.0F;
        final float f = (float) (i & 255) / 255.0F;

        return new Color(f1, f2, f);
    }

    boolean isTeam(EntityPlayer player, EntityPlayer player2) {
        return player.getTeam() != null && player2.getTeam() != null && (player.getTeam().isSameTeam(player2.getTeam()) || getTeamColor(player).getRGB() == getTeamColor(player2).getRGB());
    }

}
