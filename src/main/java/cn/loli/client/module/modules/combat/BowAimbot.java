package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.*;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.module.modules.misc.AntiBot;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.FormulaHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Comparator;
import java.util.stream.Stream;

public class BowAimbot extends Module {

    private final BooleanValue invisibles = new BooleanValue("Invisibles", false);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue mobs = new BooleanValue("Mobs", true);
    private final BooleanValue armorStand = new BooleanValue("Armor Stand", true);
    private final BooleanValue villagers = new BooleanValue("Villagers", false);
    private final BooleanValue team = new BooleanValue("Team", false);
    private final NumberValue<Integer> range = new NumberValue<>("Range", 50, 40, 120);
    private final NumberValue<Float> fov = new NumberValue<>("FOV", 360f, 0f, 360f);
    private final BooleanValue slient = new BooleanValue("Slient", true);
    private final BooleanValue auto = new BooleanValue("Auto Release", true);
    private final BooleanValue throughWalls = new BooleanValue("Through Walls", true);
    private final BooleanValue moveFix = new BooleanValue("Move Fix", false);
    private final BooleanValue clamp = new BooleanValue("Clamp", false);

    private final BooleanValue prediction = new BooleanValue("Prediction", false);

    private final ModeValue mode = new ModeValue("Priority", "Angle", "Armor", "Range", "Fov", "Angle", "Health", "Hurt Time");

    private Entity curEntity;
    float yaw, pitch;
    final TimeHelper timeHelper = new TimeHelper();

    public BowAimbot() {
        super("BowAimbot", "Slient Rotate your head to make every arrow shot", ModuleCategory.COMBAT);
    }


    @EventTarget
    private void onMoveFly(MoveFlyEvent event) {
        if (moveFix.getObject() && allowAiming(mc.thePlayer))
            event.setYaw(yaw);
    }

    @EventTarget
    private void onJump(JumpYawEvent event) {
        if (moveFix.getObject() && allowAiming(mc.thePlayer))
            event.setYaw(yaw);
    }

    @EventTarget
    private void onSlient(MovementStateEvent event) {
        if (moveFix.getObject() && allowAiming(mc.thePlayer)) {
            event.setYaw(yaw);
            event.setSilentMoveFix(true);
        }
    }

    @EventTarget
    private void onMotion(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            if (slient.getObject() && allowAiming(mc.thePlayer)) {
                if (!Float.isNaN(pitch)) {
                    event.setYaw(yaw);
                    event.setPitch(pitch);
                }
            }
        }
    }

    @EventTarget
    private void onTick(TickEvent event) {
        if (!slient.getObject() && allowAiming(mc.thePlayer)) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    @EventTarget
    private void onRender(RenderEvent event) {
        Entity entity = getClosestEntity();
        curEntity = entity;
        if (isUsing(mc.thePlayer)) {
            if (entity != null && entity != mc.thePlayer) {
                final double deltaX = entity.posX - mc.thePlayer.posX;
                double deltaY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
                final double deltaZ = entity.posZ - mc.thePlayer.posZ;

                if (!(entity instanceof EntityPlayer))
                    deltaY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());

                final double x = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ); //distance
                final double v = getVelocity();
                final double g = getGravity();

                float pitch = FormulaHelper.getProjectileMotion(v, g, x, deltaY);
                float[] rotations = rotationUtils.facePlayer(entity, false, false, false, prediction.getObject(), true, false, 0, clamp.getObject(), 180, 6);
                pitch = MathHelper.clamp_float(pitch, -90, 90);

                yaw = rotations[0];
                this.pitch = pitch;

                if (v == 1F && auto.getObject()) {
                    if (timeHelper.hasReached(200))
                        mc.playerController.onStoppedUsingItem(mc.thePlayer);
                } else if (auto.getObject()) {
                    timeHelper.reset();
                }

            }
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }


    private boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getObject()) return false;
            if (target instanceof EntityAnimal && !animals.getObject()) return false;
            if (target instanceof EntityMob && !mobs.getObject()) return false;
            if (target instanceof INpc && !villagers.getObject()) return false;
        }

        if (target instanceof EntityArmorStand && !armorStand.getObject()) return false;
        if (playerUtils.isOnSameTeam(target) && !team.getObject()) return false;
        if (target.isInvisible() && !invisibles.getObject()) return false;
        if (!isInFOV(target, fov.getObject())) return false;
        if (!throughWalls.getObject() && !target.canEntityBeSeen(mc.thePlayer)) return false;
        if (Main.INSTANCE.moduleManager.getModule(AntiBot.class).getState() && Main.INSTANCE.moduleManager.getModule(AntiBot.class).isBot(target))
            return false;

        return target != mc.thePlayer && target.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getObject();
    }

    private boolean isInFOV(EntityLivingBase entity, double angle) {
        angle *= .5D;
        double angleDiff = getAngleDifference(mc.thePlayer.rotationYaw, getRotations(entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0 && angleDiff < angle) || (-angle < angleDiff && angleDiff < 0);
    }

    private static float getAngleDifference(float dir, float yaw) {
        float f = Math.abs(yaw - dir) % 360F;
        return f > 180F ? 360F - f : f;
    }

    private float[] getRotations(double x, double y, double z) {
        double diffX = x + .5D - mc.thePlayer.posX;
        double diffY = (y + .5D) / 2D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = z + .5D - mc.thePlayer.posZ;

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI) - 90F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180D / Math.PI);

        return new float[]{yaw, pitch};
    }

    private static float[] getRotations(final Entity entity) {
        if (entity == null) {
            return null;
        }
        final double diffX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        final double diffZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double diffY;
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase elb = (EntityLivingBase) entity;
            diffY = elb.posY + (elb.getEyeHeight()) - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        }
        final double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        final float pitch = (float) (-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    private static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs((angle1 - angle2)) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    private double getVelocity() {
        int i = mc.thePlayer.getCurrentEquippedItem().getMaxItemUseDuration() - mc.thePlayer.getItemInUseCount();

        float f = (float) i / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;

        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    private boolean allowAiming(EntityPlayer player) {
        return isUsing(player) && curEntity != null;
    }

    private boolean isUsing(EntityPlayer player) {
        return player.isUsingItem() && mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow;
    }

    private double getGravity() {
        return 0.006;
    }

    private float simulateArrow(Entity entity) {
        float ticksInAir = 0;
        final Vec3 eyePosition = mc.thePlayer.getPositionEyes(1F);
        float motionX, motionY, motionZ, posX = (float) eyePosition.xCoord, posY = (float) eyePosition.yCoord, posZ = (float) eyePosition.zCoord;
        posX -= (double) (MathHelper.cos(yaw / 180.0F * (float) Math.PI) * 0.16F);
        posY -= 0.10000000149011612D;
        posZ -= (double) (MathHelper.sin(yaw / 180.0F * (float) Math.PI) * 0.16F);
        motionX = (-MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
        motionZ = (MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI));
        motionY = -MathHelper.sin(pitch / 180.0F * (float) Math.PI);

        do {
            ticksInAir++;
            float f4 = 0.99F;
            final float f6 = 0.05F;
            final Block block = mc.theWorld.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
            if (block instanceof BlockLiquid) {
                f4 = 0.6F;
            }
            posX += motionX;
            posY += motionY;
            posZ += motionZ;

            motionX *= (double) f4;
            motionY *= (double) f4;
            motionZ *= (double) f4;
            motionY -= (double) f6;

            if (entity.getDistance(posX, posY, posZ) <= 1)
                break;

        } while (mc.thePlayer.getDistance(posX, posY, posZ) <= mc.thePlayer.getDistanceToEntity(entity));
        return ticksInAir;
    }

    //取实体
    private EntityLivingBase getClosestEntity() {
        Stream<EntityLivingBase> stream = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(this::canAttack);

        switch (mode.getCurrentMode()) {
            case "Armor": {
                stream = stream.sorted(Comparator.comparingInt(o -> ((o instanceof EntityPlayer ? ((EntityPlayer) o).inventory.getTotalArmorValue() : (int) o.getHealth()))));
                break;
            }
            case "Range": {
                stream = stream.sorted((o1, o2) -> (int) (o1.getDistanceToEntity(mc.thePlayer) - o2.getDistanceToEntity(mc.thePlayer)));
                break;
            }
            case "Fov": {
                stream = stream.sorted(Comparator.comparingDouble(o -> getDistanceBetweenAngles(mc.thePlayer.rotationPitch, getRotations(o)[0])));
                break;
            }
            case "Angle": {
                stream = stream.sorted((o1, o2) -> {
                    float[] rot1 = getRotations(o1);
                    float[] rot2 = getRotations(o2);
                    return (int) (mc.thePlayer.rotationYaw - rot1[0] - (mc.thePlayer.rotationYaw - rot2[0]));
                });
                break;
            }
            case "Health": {
                stream = stream.sorted((o1, o2) -> (int) (o1.getHealth() - o2.getHealth()));
                break;
            }
            case "Hurt Time": {
                stream = stream.sorted(Comparator.comparingInt(o -> (20 - o.hurtResistantTime)));
            }
        }

        return stream.findFirst().orElse(null);
    }
}
