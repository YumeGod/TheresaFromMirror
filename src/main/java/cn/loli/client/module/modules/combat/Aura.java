

package cn.loli.client.module.modules.combat;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.PlayerMoveEvent;
import cn.loli.client.events.Render2DEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.injection.implementations.IEntityPlayer;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.*;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ColorValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Aura extends Module {
    private final NumberValue<Integer> minCps = new NumberValue<>("MinCPS", 8, 1, 20);
    private final NumberValue<Integer> maxCps = new NumberValue<>("MaxCPS", 12, 1, 20);

    private final NumberValue<Float> pitchoffset = new NumberValue<>("Pitch Offset", 0f, -1f, 1f);
    private final NumberValue<Float> turnspeed = new NumberValue<>("Turn Speed", 0.1f, 0.0f, 1.0f);

    private static final NumberValue<Integer> ticksExisted = new NumberValue<>("TicksExisted", 20, 0, 500);
    private static final NumberValue<Float> fov = new NumberValue<>("FOV", 360f, 0f, 360f);
    private static final NumberValue<Float> range = new NumberValue<>("Range", 3f, 1f, 6f);
    private static final NumberValue<Float> blockrange = new NumberValue<>("BlockRange", 2f, 1f, 3f);


    private final ModeValue mode = new ModeValue("Priority", "Angle", "Armor", "Range", "Fov", "Angle", "Health", "Hurt Time");

    private final BooleanValue autoBlock = new BooleanValue("AutoBlock", true);
    private static final BooleanValue invisibles = new BooleanValue("Invisibles", false);
    private static final BooleanValue players = new BooleanValue("Players", true);
    private static final BooleanValue animals = new BooleanValue("Animals", false);
    private static final BooleanValue mobs = new BooleanValue("Mobs", true);
    private static final BooleanValue boss = new BooleanValue("Boss", true);
    private static final BooleanValue villagers = new BooleanValue("Villagers", false);
    private static final BooleanValue team = new BooleanValue("Team", false);
    private final BooleanValue rotations = new BooleanValue("Rotations", false);

    private final BooleanValue slient = new BooleanValue("Slient", true);

    private final BooleanValue hresolver = new BooleanValue("H-Resolver", false);
    private final BooleanValue vresolver = new BooleanValue("V-Resolver", false);

    private final BooleanValue show = new BooleanValue("Show-Target", true);

    private final ColorValue espColor = new ColorValue("ESP-Color", Color.BLUE);


    public static Rotation serverRotation = new Rotation(0, 0);
    public EntityLivingBase target;
    private final TimeHelper attacktimer = new TimeHelper();

    public static ArrayList<Entity> entities = new ArrayList<>();
    public static ArrayList<Entity> sounds = new ArrayList<>();
    public static List<EntityLivingBase> targets = new ArrayList<>();

    int cps;
    float yaw, pitch, curYaw, curPitch;

    float[] rots;

    static boolean isBlocking = false;

    public Aura() {
        super("Aura", "Automatically attacks enemies around you.", ModuleCategory.COMBAT);
    }


    @Override
    public void onEnable() {
        try {
            curYaw = mc.thePlayer.rotationYaw;
            curPitch = mc.thePlayer.rotationPitch;
            rots = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        target = null; // 清空目标 (AutoBlock动画修复)
        entities.clear();
        sounds.clear();

        if (isBlocking) {
            ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }

        super.onDisable();
    }


    @EventTarget
    private void onRender(RenderEvent event) {
        try {
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (target != null && attacktimer.hasReached(randomClickDelay(Math.min(minCps.getObject(), maxCps.getObject()), Math.max(minCps.getObject(), maxCps.getObject())))) {
            cps++;
            attacktimer.reset();
        }

        if (target != null) {
            EntityLivingBase entity = target;

            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks
                    - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX();
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks
                    - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY();
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ((IAccessorMinecraft) mc).getTimer().renderPartialTicks
                    - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ();

            if (entity.hurtTime > 0)
                RenderUtils.drawWolframEntityESP(entity, (new Color(255, 102, 113)).getRGB(), x, y, z);
            else
                RenderUtils.drawWolframEntityESP(entity, espColor.getObject().getRGB(), x, y, z);
        }

    }

    @EventTarget
    public void onRender2D(Render2DEvent event) {
    }


    @EventTarget
    private void onMotionUpdate(MotionUpdateEvent event) {
        if (mc.thePlayer == null
                || mc.theWorld == null)
            return;

        if (event.getEventType() == EventType.PRE) {
            if (target == null) {
                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                        && autoBlock.getObject() && isBlocking) {
                    ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                }
                return;
            }

            //Sever-side
            serverRotation.setYaw(rots[0]);
            serverRotation.setPitch(rots[1]);

            //Save the last rotation
            float[] lastrots = rots;

            rots = RotationUtils.convertBack(RotationUtils.limitAngleChange(RotationUtils.convert(lastrots),
                    RotationUtils.convert(RotationUtils.getNeededRotations(getLocation(target.getEntityBoundingBox().expand(0.11, 0.11, 0.11),
                                    0, 0, hresolver.getObject(), vresolver.getObject(), pitchoffset.getObject()),
                            new Vec3(0, 0, 0), false, 0)), (turnspeed.getObject() * 180)));

            //Set Rotation --> Event Pre Motion;

            if (rotations.getObject()) {
                yaw = rots[0];
                pitch = rots[1];
            } else {
                yaw = mc.thePlayer.rotationYaw;
                pitch = mc.thePlayer.rotationPitch;
            }

            if (mc.thePlayer.isBlocking() || mc.thePlayer.getHeldItem() != null
                    && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getObject() && isBlocking) {
                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                isBlocking = false;
            }

            //Pre Attack
            if (mc.thePlayer.getDistanceToEntity(target) < range.getObject()) {
                while (cps > 0) {
                    attack(target);
                    cps--;
                }
            }

            //Slient Rotation
            event.setYaw(yaw);
            event.setPitch(pitch);

        }

        if (event.getEventType() == EventType.POST) {
            if (target == null) return;

            //Nah Slient Rotation
            if (!slient.getObject()) {
                mc.thePlayer.rotationYaw = yaw;
                mc.thePlayer.rotationPitch = pitch;
            }

            if (target != null && (mc.thePlayer.getHeldItem() != null &&
                    mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getObject() || mc.thePlayer.isBlocking())
                    && !isBlocking) {
                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());

                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                isBlocking = true;
            }
        }
    }

    private void attack(Entity entity) {
        mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, entity);
    }


    private void update() {
        // 初始化变量
        try {
            // 添加实体
            targets.removeIf(ent -> !canAttack(ent));
            targets = this.getTargets();
        } catch (Exception e) {
            targets = this.getTargets();
            e.printStackTrace();
        }

        // 拿实体
        if (targets.size() == 0) { // 实体数量为0停止攻击
            target = null;
        } else {
            target = targets.get(0);// 设置攻击的Target
        }
    }

    private List<EntityLivingBase> getTargets() {
        Stream<EntityLivingBase> stream = mc.theWorld.loadedEntityList.stream()
                .filter(entity -> entity instanceof EntityLivingBase)
                .map(entity -> (EntityLivingBase) entity)
                .filter(Aura::canAttack);

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

        return stream.collect(Collectors.toList());

    }


    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs((angle1 - angle2)) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
    }

    private static boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getObject()) return false;
            if (target instanceof EntityAnimal && !animals.getObject()) return false;
            if (target instanceof EntityWither && boss.getObject()) return true; // true
            if (target instanceof EntityMob && !mobs.getObject()) return false;
            if (target instanceof INpc && !villagers.getObject()) return false;
        }
        if (target instanceof EntityArmorStand) return false;
        if (PlayerUtils.isOnSameTeam(target) && team.getObject()) return false;
        if (target.isInvisible() && !invisibles.getObject()) return false;
        if (!isInFOV(target, fov.getObject())) return false;

        return target != mc.thePlayer && target.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() + blockrange.getObject() && target.ticksExisted > ticksExisted.getObject();
    }

    private static boolean isInFOV(EntityLivingBase entity, double angle) {
        angle *= .5D;
        double angleDiff = getAngleDifference(mc.thePlayer.rotationYaw, getRotations(entity.posX, entity.posY, entity.posZ)[0]);
        return (angleDiff > 0 && angleDiff < angle) || (-angle < angleDiff && angleDiff < 0);
    }

    private static float getAngleDifference(float dir, float yaw) {
        float f = Math.abs(yaw - dir) % 360F;
        return f > 180F ? 360F - f : f;
    }

    private static float[] getRotations(double x, double y, double z) {
        double diffX = x + .5D - mc.thePlayer.posX;
        double diffY = (y + .5D) / 2D - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = z + .5D - mc.thePlayer.posZ;

        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180D / Math.PI) - 90F;
        float pitch = (float) -(Math.atan2(diffY, dist) * 180D / Math.PI);

        return new float[]{yaw, pitch};
    }


    private static long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS + 1)) + 1000 / maxCPS);
    }

    public static Vec3 getLocation(AxisAlignedBB bb, int reverseValue, int mistakeValue, boolean Hres, boolean Vres, double pitchoffset) {
        Random rd1 = new Random();
        Random rd2 = new Random();
        double value = Math.random();

        boolean reverse = rd1.nextInt(100) < reverseValue;
        boolean mistake = rd2.nextInt(100) < mistakeValue;

        Vec3 resolve = null;

        if (Vres || Hres) resolve = searchCenter(bb, true).getVec3();


        double x = 0.5, z = 0.5;

        double pitch = 0.5 + (pitchoffset / 2);


        return new Vec3(Hres ? Objects.requireNonNull(resolve).xCoord : (bb.minX + (bb.maxX - bb.minX) * (reverse ? 1.0 - x : x)) * (mistake ? 1 + value * 0.1 : 1),
                Vres ? Objects.requireNonNull(resolve).yCoord : bb.minY + (bb.maxY - bb.minY) * pitch, Hres ? Objects.requireNonNull(resolve).zCoord : bb.minZ + (bb.maxZ - bb.minZ) * (reverse ? 1.0 - z : z) * (mistake ? 1 + value * 0.1 : 1));
    }

    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean predict) {
        VecRotation vecRotation = null;


        for (double xSearch = 0.0D; xSearch < 1.0D; xSearch += 0.1) {
            for (double ySearch = 0.0D; ySearch < 1.0D; ySearch += 0.1) {
                for (double zSearch = 0.0D; zSearch < 1.0D; zSearch += 0.1) {
                    final Vec3 vec3 = new Vec3(bb.minX + (bb.maxX - bb.minX) * xSearch,
                            bb.minY + (bb.maxY - bb.minY) * ySearch, bb.minZ + (bb.maxZ - bb.minZ) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);

                    final VecRotation currentVec = new VecRotation(vec3, rotation);

                    if (vecRotation == null || (getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                        vecRotation = currentVec;
                }
            }
        }

        return vecRotation;
    }

    public static double getRotationDifference(Rotation rotation) {
        return getRotationDifference(rotation, serverRotation);
    }


    public static double getRotationDifference(Rotation a, Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), (a.getPitch() - b.getPitch()));
    }

    public static Rotation toRotation(final Vec3 vec, final boolean predict) {
        final Vec3 eyesPos = new Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY +
                Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);

        if (predict)
            eyesPos.addVector(Minecraft.getMinecraft().thePlayer.motionX, Minecraft.getMinecraft().thePlayer.motionY, Minecraft.getMinecraft().thePlayer.motionZ);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    public static float[] getRotations(final Entity entity) {
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
}
