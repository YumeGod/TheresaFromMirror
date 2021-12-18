package cn.loli.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class AimUtils {
    static final Minecraft mc = Minecraft.getMinecraft();

    static Vec3 getBestVector(Entity entity, float accuracy, float precision) {
        try {
            Vec3 playerVector = mc.thePlayer.getPositionEyes(1.0F);
            Vec3 nearestVector = new Vec3(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

            float height = entity.height;
            float width = entity.width * accuracy;

            for (float y = 0; y < height; y += precision) {
                for (float x = -width; x < width; x += precision) {
                    for (float z = -width; z < width; z += precision) {
                        Vec3 currentVector = new Vec3(entity.posX + x * width, entity.posY + (entity.getEyeHeight() / height) * y, entity.posZ + z * width);

                        if (playerVector.distanceTo(currentVector) < playerVector.distanceTo(nearestVector))
                            nearestVector = currentVector;
                    }
                }
            }
            return nearestVector;
        } catch (Exception e) {
            return entity.getPositionVector();
        }
    }



    public static float[] faceEntity(Entity entity, float currentYaw, float currentPitch, float accuracy, float precision, float predictionMultiplier ,float speed, boolean instant) {
        Vec3 rotations = getBestVector(entity, accuracy, precision);

        double x = rotations.xCoord - mc.thePlayer.posX;
        double y = rotations.yCoord - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = rotations.zCoord - mc.thePlayer.posZ;

        double xDiff = (entity.posX - entity.prevPosX) * predictionMultiplier;
        double zDiff = (entity.posZ - entity.prevPosZ) * predictionMultiplier;

        double distance = mc.thePlayer.getDistanceToEntity(entity);

        if (distance < 0.05)
            return new float[]{currentYaw, currentPitch};

        double angle = MathHelper.sqrt_double(x * x + z * z);
        float yawAngle = (float) (MathHelper.atan2(z + zDiff, x + xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitchAngle = (float) (-(MathHelper.atan2(y, angle) * 180.0D / Math.PI));
        float finalPitch = pitchAngle >= 90 ? 90 : pitchAngle;
        float f = mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
        float f1 = f * f * f * 1.5F;

        float f2 = (yawAngle - currentYaw) * f1;
        float f3 = (finalPitch - currentPitch) * f1;

        float difYaw = yawAngle - currentYaw;
        float difPitch = finalPitch - currentPitch;

        float yaw = updateRotation(currentYaw + f2, yawAngle, Math.abs(MathHelper.wrapAngleTo180_float(difYaw * speed)));
        float pitch = updateRotation(currentPitch + f3, finalPitch, Math.abs(MathHelper.wrapAngleTo180_float(difPitch * speed)));

        yaw -= yaw % f1;
        pitch -= pitch % f1;

        if (instant) {
            yaw = yawAngle;
            pitch = pitchAngle;
        }

        return new float[]{yaw, pitch};
    }


    static float updateRotation(float curRot, float destination, float speed) {
        float f = MathHelper.wrapAngleTo180_float(destination - curRot);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return curRot + f;
    }

    public static boolean canEntityBeSeenFixed(Entity entityIn) {
        return mc.thePlayer.worldObj.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(entityIn.posX, entityIn.posY + (double) entityIn.getEyeHeight(), entityIn.posZ)) == null
                || mc.thePlayer.worldObj.rayTraceBlocks(
                new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ),
                new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ)) == null;
    }

}
