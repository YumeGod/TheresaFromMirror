

package cn.loli.client.module.modules.combat;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.RotationUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

/**
 * Credit: MintyCodes
 */
public class Aura extends Module {
    private final NumberValue<Integer> cps = new NumberValue<>("CPS", 8, 1, 15);
    private final NumberValue<Integer> ticksExisted = new NumberValue<>("TicksExisted", 20, 0, 500);
    private final NumberValue<Float> fov = new NumberValue<>("FOV", 360f, 0f, 360f);
    private final NumberValue<Float> range = new NumberValue<>("Range", 3f, 1f, 6f);
    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", true);
    private final BooleanValue invisibles = new BooleanValue("Invisibles", false);
    private final BooleanValue players = new BooleanValue("Players", true);
    private final BooleanValue animals = new BooleanValue("Animals", false);
    private final BooleanValue mobs = new BooleanValue("Mobs", true);
    private final BooleanValue villagers = new BooleanValue("Villagers", false);
    private final BooleanValue team = new BooleanValue("Team", false);
    private final ModeValue rotations = new ModeValue("Rotations", "Server", "None", "Client", "Server");

    private EntityLivingBase target;
    private long current, last;
    private float yaw, pitch;

    public Aura() {
        super("Aura", "Automatically attacks enemies around you.", ModuleCategory.COMBAT);
    }

    @EventTarget
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            target = getClosest(range.getObject());
            if (target == null) return;
            updateTime();
            if (rotations.getCurrentMode().equals("Client")) {
                yaw = RotationUtils.getYaw(target);
                pitch = RotationUtils.getPitch(target) + 1;
            } else {
                yaw = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;
            }
            boolean block = target != null && autoBlock.getObject() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
            if (block && target.getDistanceToEntity(mc.thePlayer) < range.getObject() + 1)
                mc.thePlayer.setItemInUse(mc.thePlayer.inventory.getCurrentItem(), 20);
            if (current - last > 1000 / cps.getObject()) {
                attack(target);
                resetTime();
            }
        }

        if (event.getEventType() == EventType.POST) {
            if (target == null) return;
            if (rotations.getCurrentMode().equals("Server")) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(yaw, pitch, mc.thePlayer.onGround));
            } else {
                mc.thePlayer.rotationYaw = yaw;
                mc.thePlayer.rotationPitch = pitch;
            }
        }
    }

    private void attack(Entity entity) {
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, entity);
    }

    private void updateTime() {
        current = (System.nanoTime() / 1000000L);
    }

    private void resetTime() {
        last = (System.nanoTime() / 1000000L);
    }

    private EntityLivingBase getClosest(double range) {
        double dist = range;
        EntityLivingBase target = null;

        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                if (canAttack(entityLivingBase)) {
                    double currentDist = mc.thePlayer.getDistanceToEntity(entityLivingBase);
                    if (currentDist <= dist) {
                        dist = currentDist;
                        target = entityLivingBase;
                    }
                }
            }
        }

        return target;
    }

    private boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getObject()) return false;
            if (target instanceof EntityAnimal && !animals.getObject()) return false;
            if (target instanceof EntityMob && !mobs.getObject()) return false;
            if (target instanceof INpc && !villagers.getObject()) return false;
        }

        if (target.isOnSameTeam(mc.thePlayer) && !team.getObject()) return false;
        if (target.isInvisible() && !invisibles.getObject()) return false;
        if (!isInFOV(target, fov.getObject())) return false;

        return target != mc.thePlayer && target.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() && target.ticksExisted > ticksExisted.getObject();
    }

    private boolean isInFOV(EntityLivingBase entity, double angle) {
        angle *= .5D;
        double angleDiff = getAngleDifference(mc.thePlayer.rotationYaw, getRotations(entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0 && angleDiff < angle) || (-angle < angleDiff && angleDiff < 0);
    }

    private float getAngleDifference(float dir, float yaw) {
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
}
