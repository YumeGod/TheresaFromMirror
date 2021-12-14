package cn.loli.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;

public class RotationUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static float[] prevRotations = new float[2];


    public static float getYaw(Entity entity) {
        if (entity == null) return mc.thePlayer.rotationYaw;

        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;

        return mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
    }

    public static float getPitch(Entity entity) {
        if (entity == null) return mc.thePlayer.rotationPitch;

        double diffY;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }

        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

        float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

        return mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) + 1;
    }

    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation,
                                            final float turnSpeed) {
        final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
        final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

        return new Rotation(
                currentRotation.getYaw()
                        + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation.getPitch()
                        + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)));
    }

    public static Rotation convert(float[] rot) {
        return new Rotation(rot[0], rot[1]);
    }

    public static float[] convertBack(Rotation rotation) {
        return new float[]{rotation.getYaw(), rotation.getPitch()};
    }


    public static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = MathHelper.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-rotation.getPitch() * 0.017453292F);
        float pitchSin = MathHelper.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    public static float getRotationDifference(float current, float target) {
        return MathHelper.wrapAngleTo180_float(target - current);
    }

    private static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static double getRotationDifference(final Rotation rotation) {
        return prevRotations == null ? 0D
                : getRotationDifference(rotation,
                new Rotation(prevRotations[0], prevRotations[1]));
    }

    public static float[] getNeededRotations(Vec3 vec, Vec3 transected , boolean predict , double value) {
        Vec3 targetVector = new Vec3(vec.xCoord + (predict ? transected.xCoord : 0), vec.yCoord + (predict ? transected.yCoord : 0), vec.zCoord + (predict ? transected.zCoord : 0));
        Vec3 playerVector = new Vec3(mc.thePlayer.posX + mc.thePlayer.motionX * value * RandomUtils.nextFloat(0.8f, 1.2f),
                mc.thePlayer.getEntityBoundingBox().minY + (double) mc.thePlayer.getEyeHeight() + mc.thePlayer.motionY,
                mc.thePlayer.posZ + mc.thePlayer.motionZ * value * RandomUtils.nextFloat(0.8f, 1.2f));


        double y = targetVector.yCoord - playerVector.yCoord;
        double x = targetVector.xCoord - playerVector.xCoord;
        double z = targetVector.zCoord - playerVector.zCoord;

        double dff = Math.sqrt(x * x + z * z);

        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0F;
        float pitch = (float) (-Math.toDegrees(Math.atan2(y, dff)));

        return new float[]{MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch)};
    }

}
