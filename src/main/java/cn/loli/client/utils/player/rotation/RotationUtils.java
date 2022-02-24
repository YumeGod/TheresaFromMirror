package cn.loli.client.utils.player.rotation;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.utils.Utils;
import cn.loli.client.utils.misc.RandomUtil;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;

import java.util.List;
import java.util.Random;

public class RotationUtils extends Utils {

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


    public Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    /* Thx to DasNeueUpdate */
    public float[] calculateRotationDiff(float yaw, float yaw1) {
        float y = Math.abs(yaw - yaw1);
        if (y < 0) y += 360;
        if (y >= 360) y -= 360;
        float y1 = 360 - y;
        float oneoranother = 0;
        if (y > y1) oneoranother++;
        if (y > y1) y = y1;
        return new float[]{y, oneoranother};
    }

    public float getYaw(Vec3 vec) {
        double xD = (mc.thePlayer.posX) - (vec.xCoord);
        double zD = (mc.thePlayer.posZ) - (vec.zCoord);
        return (float) ((float) Math.atan2(zD, xD) / Math.PI * 180) - 90;
    }

    public float getYaw(Entity en, Entity en2) {
        Vec3 vec = new Vec3(en2.posX, en2.posY, en2.posZ);
        double xD = (en.posX) - (vec.xCoord + (en2.posX - en2.lastTickPosX));
        double zD = (en.posZ) - (vec.zCoord + (en2.posZ - en2.lastTickPosZ));
        return (float) ((float) Math.atan2(zD, xD) / Math.PI * 180) - 90;
    }


    public Vec3 getLook(float yaw, float pitch) {
        return getLook(yaw, pitch, 1F);
    }

    protected final Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public Vec3 getLook(float yaw, float pitch, float partialTicks) {
        return getVectorForRotation(pitch, yaw);
    }

    float updateRotation(float currentRotation, float nextRotation, float rotationSpeed) {
        float f = MathHelper.wrapAngleTo180_float(nextRotation - currentRotation);
        if (f > rotationSpeed) {
            f = rotationSpeed;
        }
        if (f < -rotationSpeed) {
            f = -rotationSpeed;
        }
        return currentRotation + f;
    }

    public float[] setAngles(float currentYaw, float currentPitch, float yaw, float pitch) {
        currentYaw = (float) ((double) currentYaw + (double) yaw * 0.15D);
        currentPitch = (float) ((double) currentPitch + (double) pitch * 0.15D);
        return new float[]{currentYaw, currentPitch};
    }

    public float[] applyMouseSensitivity(float yaw, float pitch, boolean a3) {
        float sensitivity = mc.gameSettings.mouseSensitivity;
        if (sensitivity == 0) {
            sensitivity = 0.0070422534F; //1% Sensitivity <- to fix 0.0 sensitivity
        }
        sensitivity = Math.max(0.1F, sensitivity);
        int deltaYaw = (int) ((yaw - RotationHook.yaw) / (sensitivity / 2));
        int deltaPitch = (int) ((pitch - RotationHook.pitch) / (sensitivity / 2)) * -1;

        if (a3) {
            deltaYaw -= deltaYaw % 0.5 + 0.25;
            deltaPitch -= deltaPitch % 0.5 + 0.25;
        }
        float f = sensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8F;
        float f2 = (float) deltaYaw * f1;
        float f3 = (float) deltaPitch * f1;

        float endYaw = (float) ((double) RotationHook.yaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) RotationHook.pitch - (double) f3 * 0.15);
        return new float[]{endYaw, endPitch};
    }

    public float[] facePlayer(Entity e, boolean a3, boolean heuristics, boolean smooth, boolean prediction, boolean mouseFix, double mouseFixSpeed, boolean bestVector, double inaccuracy, boolean clampYaw, float rotationSpeed, double range) {
        final RandomUtil randomUtil = RandomUtil.getInstance();

        final double eyeX = (mc.thePlayer.getEntityBoundingBox().minX + mc.thePlayer.getEntityBoundingBox().maxX) / 2;
        final double eyeY = mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight();
        final double eyeZ = (mc.thePlayer.getEntityBoundingBox().minZ + mc.thePlayer.getEntityBoundingBox().maxZ) / 2;

        double x = e.posX - eyeX;
        double y = e.posY + e.getEyeHeight() - eyeY;
        double z = e.posZ - eyeZ;

        if (bestVector) {
            final Vec3 bestVec = getBestVector(mc.thePlayer.getPositionEyes(((IAccessorMinecraft) mc).getTimer().renderPartialTicks), e.getEntityBoundingBox()).addVector(-inaccuracy / 10, -inaccuracy / 10, -inaccuracy / 10);
            x = bestVec.xCoord - eyeX;
            y = bestVec.yCoord - eyeY;
            z = bestVec.zCoord - eyeZ;
        }

        if (!(e instanceof EntityLivingBase)) {
            y = (e.getEntityBoundingBox().minY + e.getEntityBoundingBox().maxY) / 2.0D - (mc.thePlayer.getEntityBoundingBox().minY + (double) mc.thePlayer.getEyeHeight());
        }

        if (heuristics) {
            final float randomPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.015, 0.018);
            float randomizedPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.010, randomPitch);
            float randomizedYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.1, -0.3);
            x += randomUtil.getRandomDouble(-randomizedPitch, randomizedPitch);
            z += randomUtil.getRandomDouble(-randomizedPitch, randomizedPitch);
            y += randomUtil.getRandomDouble(randomizedYaw, -0.01);
        }

        if (prediction) {
            boolean sprinting = e.isSprinting();
            boolean sprintingPlayer = mc.thePlayer.isSprinting();

            float walkingSpeed = 0.10000000149011612f; //https://minecraft.fandom.com/wiki/Sprinting

            float sprintMultiplication = sprinting ? 1.25f : walkingSpeed;
            float sprintMultiplicationPlayer = sprintingPlayer ? 1.25f : walkingSpeed;

            float xMultiplication = (float) ((e.posX - e.prevPosX) * sprintMultiplication);
            float zMultiplication = (float) ((e.posZ - e.prevPosZ) * sprintMultiplication);

            float xMultiplicationPlayer = (float) ((mc.thePlayer.posX - mc.thePlayer.prevPosX) * sprintMultiplicationPlayer);
            float zMultiplicationPlayer = (float) ((mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * sprintMultiplicationPlayer);


            if (xMultiplication != 0.0f && zMultiplication != 0.0f || xMultiplicationPlayer != 0.0f && zMultiplicationPlayer != 0.0f) {
                x += xMultiplication + xMultiplicationPlayer;
                z += zMultiplication + zMultiplicationPlayer;
            }
        }

        double angle = MathHelper.sqrt_double(x * x + z * z);

        float yawAngle = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitchAngle = (float) (-(MathHelper.atan2(y, angle) * 180.0D / Math.PI));

        double rangeToEntity = mc.thePlayer.getDistanceToEntity(e);
        double rangeSetting = range + 1;

        double rotationDelta = Math.hypot(RotationHook.yaw - yawAngle, RotationHook.pitch - pitchAngle);
        double speed = rotationDelta * ((rangeSetting - rangeToEntity) / rangeSetting);

        float yaw = clampYaw ? yawAngle : updateRotation(RotationHook.yaw, yawAngle, smooth ? MathHelper.abs((float) speed) : rotationSpeed);
        float pitch = updateRotation(RotationHook.pitch, pitchAngle, smooth ? MathHelper.abs((float) speed) : rotationSpeed);


        if (!mouseFix)
            return new float[]{yaw, clampPitch(pitch)};
        final float[] mouseSensitivity = applyMouseSensitivity(yaw, pitch, a3);

        return new float[]{mouseSensitivity[0], clampPitch(mouseSensitivity[1])};
    }

    public float[] faceBlock(BlockPos pos, double yTranslation, boolean a3Fix, boolean mouseFix, boolean prediction, boolean randomAim, boolean randomizePitch, boolean clampYaw, float speed) {
        double x = (pos.getX() + (randomAim ? randomInRange(0.45D, 0.5D) : 0.5D)) - (mc.thePlayer.getEntityBoundingBox().minX + mc.thePlayer.getEntityBoundingBox().maxX) / 2 - (prediction ? mc.thePlayer.motionX : 0);
        double y = (pos.getY() - yTranslation) - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());
        double z = (pos.getZ() + (randomAim ? randomInRange(0.45D, 0.5D) : 0.5D)) - (mc.thePlayer.getEntityBoundingBox().minZ + mc.thePlayer.getEntityBoundingBox().maxZ) / 2 - (prediction ? mc.thePlayer.motionZ : 0);

        if (randomizePitch)
            y += randomInRange(-0.05, 0.05);

        double angle = MathHelper.sqrt_double(x * x + z * z);
        float yawAngle = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitchAngle = (float) -(MathHelper.atan2(y, angle) * 180.0D / Math.PI);

        float yaw = clampYaw ? yawAngle : updateRotation(RotationHook.yaw, yawAngle, speed);
        float pitch = updateRotation(RotationHook.pitch, pitchAngle, speed);

        if (!mouseFix)
            return new float[]{yaw, clampPitch(pitch)};
        final float[] mouseSensitivity = applyMouseSensitivity(yaw, pitch, a3Fix);
        return new float[]{mouseSensitivity[0], clampPitch(mouseSensitivity[1])};
    }

    public float clampPitch(float pitch) {
        return MathHelper.clamp_float(pitch, -90, 90);
    }

    public Entity rayCastedEntity(double range, float yaw, float pitch) {
        return rayCastedEntity(range, yaw, pitch, 1F);
    }

    public Entity rayCastedEntity(double range, float yaw, float pitch, float partialTicks) {
        final Entity entity = mc.getRenderViewEntity();
        Entity pointedEntity = null;
        if (entity != null && mc.theWorld != null) {
            MovingObjectPosition mouseOver = entity.rayTrace(range, partialTicks);
            Vec3 vec3 = entity.getPositionEyes(1F);
            boolean flag = false;
            double d1 = range;
            if (mouseOver != null) {
                d1 = mouseOver.hitVec.distanceTo(vec3);
            }

            Vec3 vec31 = getLook(yaw, pitch, partialTicks);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;
            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = (Entity) list.get(i);
                float f1 = entity1.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (range >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        range = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < range || range == 0.0D) {
                        boolean flag2 = false;

                        if (entity1 == entity.ridingEntity && !flag2) {
                            if (d2 == 0.0D) {
                                pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = entity1;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }
            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > range) {
                pointedEntity = null;
                mouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mouseOver = new MovingObjectPosition(pointedEntity, vec33);

                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    return pointedEntity;
                }
            }
            if (mouseOver != null && mouseOver.entityHit != null)
                return mouseOver.entityHit;
        }
        return pointedEntity;
    }

    public MovingObjectPosition rayTrace(float yaw, float pitch, float reach) {
        return rayTrace(mc.thePlayer, yaw, pitch, reach);
    }

    public MovingObjectPosition rayTrace(Entity entity, float yaw, float pitch, float reach) {
        Vec3 vec3 = entity.getPositionEyes(1F);
        Vec3 vec31 = getLook(yaw, pitch);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * reach, vec31.yCoord * reach, vec31.zCoord * reach);
        return mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }

    public MovingObjectPosition rayCastedBlock(float yaw, float pitch) {
        float range = mc.playerController.getBlockReachDistance();

        Vec3 vec31 = getLook(yaw, pitch);

        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);


        MovingObjectPosition ray = mc.theWorld.rayTraceBlocks(vec3, vec32, false, false, false);

        if (ray != null && ray.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            return ray;
        return null;
    }

}
