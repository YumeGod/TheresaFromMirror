package cn.loli.client.module.modules.combat;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.PlayerUtils;
import cn.loli.client.utils.WorldUtil;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
    private final NumberValue<Integer> pre = new NumberValue<>("Move Fix", 10, 5, 20);
    private final BooleanValue newmode = new BooleanValue("Y Counter", false);

    public static ArrayList<EntityLivingBase> attackList = new ArrayList<>();
    public static ArrayList<EntityLivingBase> targets = new ArrayList<>();
    public static int currentTarget;

    public BowAimbot() {
        super("BowAimbot", "Slient Rotate your head to make every arrow shot", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


    @EventTarget
    public void onPre(MotionUpdateEvent event) {
        final List<EntityLivingBase> targets = WorldUtil.getLivingEntities().stream().filter(this::canAttack)
                .sorted(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e)))
                .collect(Collectors.toList());

        if (targets.size() <= 0)
            return;

        if (mc.thePlayer != null && targets.get(0) != null && canAttack(targets.get(0)) && mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem().getItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
            int bowCurrentCharge = mc.thePlayer.getItemInUseDuration();
            float bowVelocity = (bowCurrentCharge / 20.0f);
            bowVelocity = (bowVelocity * bowVelocity + bowVelocity * 2.0f) / 3.0f;
            bowVelocity = MathHelper.clamp_float(bowVelocity, 0.0F, 1.0F);

            double v = bowVelocity * 3.0F;
            double g = 0.05000000074505806D;
            if (bowVelocity < 0.1)
                return;

            if (bowVelocity > 1.0f)
                bowVelocity = 1.0f;


            final double xDistance = targets.get(0).posX - mc.thePlayer.posX + (targets.get(0).posX - targets.get(0).prevPosX) * (bowVelocity * pre.getObject());
            final double zDistance = targets.get(0).posZ - mc.thePlayer.posZ + (targets.get(0).posZ - targets.get(0).prevPosZ) * (bowVelocity * pre.getObject());

            final float yaw = (float) (Math.atan2(zDistance, xDistance) * 180.0 / Math.PI) - 90.0f;
            final float pitch = (float) -Math.toDegrees(getLaunchAngle(targets.get(0), v, g)) - 3;

            if (yaw <= 360 && pitch <= 360) {
                if (slient.getObject()) {
                    event.setYaw(mc.thePlayer.rotationYawHead = yaw);
                    event.setPitch(pitch);
                } else {
                    mc.thePlayer.rotationYaw = yaw;
                    mc.thePlayer.rotationPitch = pitch;
                }

                if (mc.thePlayer.getItemInUseDuration() > 20 && this.auto.getObject()) {
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                }
            }


        }

    }

    @Override
    public void onDisable() {
        super.onDisable();
        targets.clear();
        attackList.clear();
        currentTarget = 0;
    }

    private float getLaunchAngle(EntityLivingBase targetEntity, double v, double g) {
        double yDif =
                newmode.getObject() ?  targetEntity.posY + targetEntity.getEyeHeight() / 2.0F - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - mc.thePlayer.motionY) :
                targetEntity.posY + targetEntity.getEyeHeight() / 2.0F - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());


        double xDif = targetEntity.posX - mc.thePlayer.posX;
        double zDif = targetEntity.posZ - mc.thePlayer.posZ;

        double xCoord = Math.sqrt(xDif * xDif + zDif * zDif);

        return theta(v + 2, g, xCoord, yDif);
    }

    private float theta(double v, double g, double x, double y) {
        double yv = 2.0D * y * (v * v);
        double gx = g * (x * x);
        double g2 = g * (gx + yv);
        double insert = v * v * v * v - g2;
        double sqrt = Math.sqrt(insert);

        double numerator = v * v + sqrt;
        double numerator2 = v * v - sqrt;

        double atan1 = Math.atan2(numerator, g * x);
        double atan2 = Math.atan2(numerator2, g * x);

        return (float) Math.min(atan1, atan2);
    }


    private boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getObject()) return false;
            if (target instanceof EntityAnimal && !animals.getObject()) return false;
            if (target instanceof EntityMob && !mobs.getObject()) return false;
            if (target instanceof INpc && !villagers.getObject()) return false;
        }

        if (target instanceof EntityArmorStand && !armorStand.getObject()) return false;
        if (PlayerUtils.isOnSameTeam(target) && !team.getObject()) return false;
        if (target.isInvisible() && !invisibles.getObject()) return false;
        if (!isInFOV(target, fov.getObject())) return false;
        if (!target.canEntityBeSeen(mc.thePlayer)) return false;

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

    public final float[] rotationsToEntityWithBow(Entity entity) {
        double d1 = Math.sqrt(mc.thePlayer.getDistanceToEntity(entity) * mc.thePlayer.getDistanceToEntity(entity)) / 1.5D;
        double d2 = entity.posX + (entity.posX - entity.prevPosX) * d1 - mc.thePlayer.posX;
        double d3 = entity.posZ + (entity.posZ - entity.prevPosZ) * d1 - mc.thePlayer.posZ;
        double d4 = entity.posY + (entity.posY - entity.prevPosY) + mc.thePlayer.getDistanceToEntity(entity) * mc.thePlayer.getDistanceToEntity(entity) / 300.0F + entity.getEyeHeight() - mc.thePlayer.posY - mc.thePlayer.getEyeHeight() - mc.thePlayer.motionY;
        return new float[]{(float) Math.toDegrees(Math.atan2(d3, d2)) - 90.0F, (float) -Math.toDegrees(Math.atan2(d4, Math.hypot(d2, d3)))};
    }
}
