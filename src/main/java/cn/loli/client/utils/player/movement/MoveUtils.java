package cn.loli.client.utils.player.movement;

import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.module.modules.combat.TargetStrafe;
import cn.loli.client.utils.Utils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.Arrays;

public class MoveUtils extends Utils {

    private static MoveUtils utils;

    //From Zane's Old Move Utils
    final double SPRINTING_MOD = 1.0 / 1.3F;
    final double SNEAK_MOD = 0.3F;
    final double ICE_MOD = 2.5F;
    final double WALK_SPEED = 0.221;
    final double SWIM_MOD = 0.115F / WALK_SPEED;
    final double[] DEPTH_STRIDER_VALUES = {
            1.0F,
            0.1645F / SWIM_MOD / WALK_SPEED,
            0.1995F / SWIM_MOD / WALK_SPEED,
            1.0F / SWIM_MOD,
    };
    final double MIN_DIST = 1.0E-3;

    final double AIR_FRICTION = 0.98F;
    final double WATER_FRICTION = 0.89F;
    final double LAVA_FRICTION = 0.535F;
    final double BUNNY_DIV_FRICTION = 160.0 - 1.0E-3;

    final double[] SPEEDS = new double[3];

    public final double[] LOW_HOP_Y_POSITIONS = {
            round(0.4, 0.001),
            round(0.71, 0.001),
            round(0.75, 0.001),
            round(0.55, 0.001),
            round(0.41, 0.001)
    };


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

    public double getBaseMoveSpeed(double speed, double v) {
        double baseSpeed = speed;
        if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + v * (amplifier + 1);
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

    public void setSpeed(final EntityPlayerSP player,
                                final PlayerMoveEvent event,
                                final TargetStrafe targetStrafe,
                                double speed) {
        if (targetStrafe.shouldStrafe()) {
            if (targetStrafe.shouldAdaptSpeed())
                speed = Math.min(speed, targetStrafe.getAdaptedSpeed());
            targetStrafe.setSpeed(event, speed);
            return;
        }

        setSpeed(event, speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }

    public void setSpeed(final PlayerMoveEvent moveEvent,
                                final double speed,
                                float forward,
                                float strafing,
                                float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return;

        yaw = getMovementDirection(forward, strafing, yaw);

        final double movementDirectionRads = Math.toRadians(yaw);
        final double x = -Math.sin(movementDirectionRads) * speed;
        final double z = Math.cos(movementDirectionRads) * speed;
        moveEvent.setX(x);
        moveEvent.setZ(z);
    }

    public float getMovementDirection(final float forward,
                                             final float strafing,
                                             float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return yaw;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafing > 0.0f)
            yaw -= strafingYaw;
        else if (strafing < 0.0f)
            yaw += strafingYaw;

        return yaw;
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

    public boolean simJumpShouldDoLowHop(final double baseMoveSpeedRef) {
        // Calculate the direction moved in
        final float direction = calculateYawFromSrcToDst(mc.thePlayer.rotationYaw,
                mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosZ,
                mc.thePlayer.posX, mc.thePlayer.posZ);
        final Vec3 start = new Vec3(mc.thePlayer.posX,
                mc.thePlayer.posY + LOW_HOP_Y_POSITIONS[2],
                mc.thePlayer.posZ);
        // Cast a ray at waist height in the direction moved in for 10 blocks
        final MovingObjectPosition rayTrace = mc.theWorld.rayTraceBlocks(start,
                getDstVec(start, direction, 0.0F, 8),
                false, true, true);
        // If did not hit anything just continue
        if (rayTrace == null) return true;
        if (rayTrace.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return true;
        if (rayTrace.hitVec == null) return true;

        // Check if player can fit above
        final AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox();
        if (mc.theWorld.checkBlockCollision(
                bb.offset(bb.minX - rayTrace.hitVec.xCoord,
                        bb.minY - rayTrace.hitVec.yCoord,
                        bb.minZ - rayTrace.hitVec.zCoord)))
            return false;

        // Distance to the block hit
        final double dist = start.distanceTo(rayTrace.hitVec);
        final double normalJumpDist = 4.0;
        return dist > normalJumpDist;
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
        if (player != null) {
            for (int i = 0; i < 101; i++) {
                player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(player.posX, player.posY + 0.03, player.posZ, false));
                player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(player.posX, player.posY, player.posZ, false));
            }
            player.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(player.posX, player.posY, player.posZ, true));
        }
    }

    public static MoveUtils getInstance() {
        if (utils == null) {
            utils = new MoveUtils();
        }
        return utils;
    }
}
