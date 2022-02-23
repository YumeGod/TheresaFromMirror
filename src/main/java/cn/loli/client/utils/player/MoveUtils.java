package cn.loli.client.utils.player;

import cn.loli.client.events.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

public class MoveUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void setMotion(PlayerMoveEvent event, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            event.setX(mc.thePlayer.motionX = 0);
            event.setZ(mc.thePlayer.motionZ = 0);
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else
                if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else
                if (forward < 0.0D) {
                    forward = -1;
                }
            }
            event.setX(mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.31d;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.195 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public double[] getSpeed(double speed, float yaw, boolean direction) {
        final double motionX = -Math.sin(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        final double motionZ = Math.cos(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        return new double[] {motionX, motionZ};
    }

    public static void setSpeed(double speed) {
        setSpeed(speed, mc.thePlayer.rotationYaw);
    }

    public static void setSpeed(double speed, float yaw) {
        setSpeed(speed, yaw, true);
    }

    public static void setSpeed(double speed, float yaw, boolean direction) {
        mc.thePlayer.motionX = -Math.sin(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
    }

    public static float getDirection(float rotationYaw) {
        float left = Minecraft.getMinecraft().gameSettings.keyBindLeft.isPressed() ? 
                mc.gameSettings.keyBindBack.isPressed() ? 45 : mc.gameSettings.keyBindForward.isPressed() ? -45 : -90 : 0;
        float right = Minecraft.getMinecraft().gameSettings.keyBindRight.isPressed() ?
                mc.gameSettings.keyBindBack.isPressed() ? -45 : mc.gameSettings.keyBindForward.isPressed() ? 45 : 90 : 0;
        float back = Minecraft.getMinecraft().gameSettings.keyBindBack.isPressed() ? + 180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }


    public static void addMotion(double speed, float yaw) {
        mc.thePlayer.motionX -= (MathHelper.sin((float) Math.toRadians(yaw)) * speed);
        mc.thePlayer.motionZ += (MathHelper.cos((float) Math.toRadians(yaw)) * speed);
    }

    public static int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }
}
