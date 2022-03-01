package cn.loli.client.utils.player.movement;

import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;

import java.util.Arrays;

public class MoveUtils extends Utils {

    private static MoveUtils utils;

    //From Zane's Old Move Utils
    public final double SPRINTING_MOD = 1.0 / 1.3F;
    public final double SNEAK_MOD = 0.3F;
    public final double ICE_MOD = 2.5F;
    public final double WALK_SPEED = 0.221;
    private final double SWIM_MOD = 0.115F / WALK_SPEED;
    private final double[] DEPTH_STRIDER_VALUES = {
            1.0F,
            0.1645F / SWIM_MOD / WALK_SPEED,
            0.1995F / SWIM_MOD / WALK_SPEED,
            1.0F / SWIM_MOD,
    };
    public final double MIN_DIST = 1.0E-3;

    private final double AIR_FRICTION = 0.98F;
    private final double WATER_FRICTION = 0.89F;
    private final double LAVA_FRICTION = 0.535F;
    public final double BUNNY_DIV_FRICTION = 160.0 - 1.0E-3;

    private final double[] SPEEDS = new double[3];

    public void setMotion(PlayerMoveEvent event, double speed) {
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
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            double sin = Math.sin(Math.toRadians(yaw + 90.0F));
            double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            event.setX(mc.thePlayer.motionX = forward * speed * cos + strafe * speed * sin);
            event.setZ(mc.thePlayer.motionZ = forward * speed * sin - strafe * speed * cos);
        }
    }

    public boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty();
    }

    public double getBaseMoveSpeed() {
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
        return new double[]{motionX, motionZ};
    }

    public void setSpeed(double speed) {
        setSpeed(speed, mc.thePlayer.rotationYaw);
    }

    public void setSpeed(double speed, float yaw) {
        setSpeed(speed, yaw, true);
    }

    public void setSpeed(double speed, float yaw, boolean direction) {
        mc.thePlayer.motionX = -Math.sin(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
        mc.thePlayer.motionZ = Math.cos(Math.toRadians(direction ? getDirection(yaw) : yaw)) * speed;
    }

    public float getDirection(float rotationYaw) {
        float left = Minecraft.getMinecraft().gameSettings.keyBindLeft.isPressed() ?
                mc.gameSettings.keyBindBack.isPressed() ? 45 : mc.gameSettings.keyBindForward.isPressed() ? -45 : -90 : 0;
        float right = Minecraft.getMinecraft().gameSettings.keyBindRight.isPressed() ?
                mc.gameSettings.keyBindBack.isPressed() ? -45 : mc.gameSettings.keyBindForward.isPressed() ? 45 : 90 : 0;
        float back = Minecraft.getMinecraft().gameSettings.keyBindBack.isPressed() ? +180 : 0;
        float yaw = left + right + back;
        return rotationYaw + yaw;
    }


    public void addMotion(double speed, float yaw) {
        mc.thePlayer.motionX -= (MathHelper.sin((float) Math.toRadians(yaw)) * speed);
        mc.thePlayer.motionZ += (MathHelper.cos((float) Math.toRadians(yaw)) * speed);
    }

    public int getSpeedEffect() {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1 : 0;
    }


    public double applyNCPFriction(final EntityPlayerSP player,
                                   final double moveSpeed,
                                   final double lastDist,
                                   final double baseMoveSpeedRef) {
        SPEEDS[0] = lastDist - (lastDist / BUNNY_DIV_FRICTION);
        SPEEDS[1] = lastDist - ((moveSpeed - lastDist) / 33.3D);
        double materialFriction = player.isInWater() ? WATER_FRICTION :
                player.isInLava() ? LAVA_FRICTION :
                        AIR_FRICTION;
        SPEEDS[2] = lastDist - (baseMoveSpeedRef * (1.0D - materialFriction));

        Arrays.sort(SPEEDS);

        return SPEEDS[0];
    }

    public double getJumpHeight(final EntityPlayerSP player) {
        final double base = 0.42F;
        final PotionEffect effect = player.getActivePotionEffect(Potion.jump);
        return effect == null ? base : base + ((effect.getAmplifier() + 1) * 0.1F);
    }

    public float getMinFallDist(final EntityPlayerSP player) {
        final float baseFallDist = 3.0F;
        final PotionEffect effect = player.getActivePotionEffect(Potion.jump);
        final int amp = effect != null ? effect.getAmplifier() + 1 : 0;
        return baseFallDist + amp;
    }

    public double calculateJumpDistance(final double baseMoveSpeedRef,
                                               double[] velocity, // Re-use velocity to store velocity
                                               double lastDist, // Re-use lastDist to store the lastDist
                                               final MotionModificationFunc motionModificationFunc) {
        double posY = 0.0;

        double totalDistance = 0.0;

        int tick = 0;

        do {
            // Part of vanilla logic
            if (Math.abs(velocity[0]) < 0.005) velocity[0] = 0.0;
            if (Math.abs(velocity[1]) < 0.005) velocity[1] = 0.0;
            if (Math.abs(velocity[2]) < 0.005) velocity[2] = 0.0;
            // Run the motion (x/y/z) modification
            motionModificationFunc.runSimulation(velocity, baseMoveSpeedRef, lastDist,
                    round(posY - (int) posY, 0.001), tick);
            // Accumulate position
            posY += velocity[1];
            // Calculate dist travelled this tick
            final double dist = Math.sqrt(velocity[0] * velocity[0] + velocity[2] * velocity[2]);
            // Store last dist
            lastDist = dist;
            // Accumulate total distance
            totalDistance += dist;
            // Vanilla gravity
            velocity[1] -= 0.08;
            velocity[1] *= 0.98F;
            // Each run is equivalent to a tick passing
            tick++;
        } while (posY > 0.0); // When posY is <= 0.0 that represents that you have reached the ground

        return totalDistance;
    }

    public void getDamage(final EntityPlayerSP player) {
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        float minDmgDist = getMinFallDist(player);

        double inc = 0.0625;

        while (minDmgDist > 0.0F) {
            double lo = Math.random() * 0.001F;
            double hi = inc - lo;
            player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + hi, z, false));
            player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + lo, z, false));
            minDmgDist -= hi - lo;
        }
        player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
    }

    public static MoveUtils getInstance() {
        if (utils == null) {
            utils = new MoveUtils();
        }
        return utils;
    }
}
