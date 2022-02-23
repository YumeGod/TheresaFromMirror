package cn.loli.client.utils.player.rotation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.apache.commons.lang3.RandomUtils;

public class RotationUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();


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


}
