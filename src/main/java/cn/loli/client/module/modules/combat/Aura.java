

package cn.loli.client.module.modules.combat;

import cn.loli.client.events.*;
import cn.loli.client.injection.implementations.IEntityPlayer;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.timer.TimeHelper;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import cn.loli.client.utils.render.RenderUtils;
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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Aura extends Module {
    private final NumberValue<Integer> minCps = new NumberValue<>("MinCPS", 8, 1, 20);
    private final NumberValue<Integer> maxCps = new NumberValue<>("MaxCPS", 12, 1, 20);

    private static final NumberValue<Integer> ticksExisted = new NumberValue<>("TicksExisted", 20, 0, 500);
    private static final NumberValue<Integer> fov = new NumberValue<>("FOV", 360, 0, 360);
    private static final NumberValue<Float> range = new NumberValue<>("Range", 3f, 1f, 6f);
    private static final NumberValue<Float> blockrange = new NumberValue<>("BlockRange", 2f, 0f, 3f);

    //   private static final NumberValue<Float> mouseSpeed = new NumberValue<>("Mouse Speed", 5f, 0f, 6f);
    private static final NumberValue<Float> inaccuracy = new NumberValue<>("Inaccuracy", 0f, 0f, 1f);


    private final ModeValue mode = new ModeValue("Priority", "Angle", "Armor", "Range", "Fov", "Angle", "Health", "Hurt Time");
    private final ModeValue esp = new ModeValue("Target ESP", "Box", "Box", "2D", "Icarus");

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

    private final BooleanValue noInv = new BooleanValue("No Inv", true);
    private final BooleanValue autoCloseInv = new BooleanValue("Auto Close Inv", false);
    private final BooleanValue clampYaw = new BooleanValue("Clamp", true);

    private final BooleanValue moveFix = new BooleanValue("Move Fix", false);
    private final BooleanValue silentMoveFix = new BooleanValue("Slient Fix", false);

    private final BooleanValue rayCast = new BooleanValue("Ray Cast", false);

    private final BooleanValue mouseFix = new BooleanValue("Mouse Fix", true);
    private final BooleanValue mouse_vl_fix = new BooleanValue("Mouse VL Fix", true);
    private final BooleanValue random = new BooleanValue("Random", false);
    private final BooleanValue instant = new BooleanValue("Instant", false);
    private final BooleanValue prediction = new BooleanValue("Prediction", false);
    private final BooleanValue bestVector = new BooleanValue("Resolver", false);

    private static final NumberValue<Integer> rotationSpeed = new NumberValue<>("Rotation Speed", 180, 0, 180);

    private final BooleanValue show = new BooleanValue("Show-Target", true);
    private final BooleanValue targetHud = new BooleanValue("Target-Hud", false);

    private final ColorValue espColor = new ColorValue("ESP-Color", Color.BLUE);

    public EntityLivingBase target;

    private final TimeHelper attacktimer = new TimeHelper();

    public static ArrayList<Entity> entities = new ArrayList<>();
    public static List<EntityLivingBase> targets = new ArrayList<>();

    int cps;
    static boolean isBlocking = false;

    final RotationUtils utils = RotationUtils.getInstance();
    float curYaw, curPitch;

    public Aura() {
        super("Aura", "Automatically attacks enemies around you.", ModuleCategory.COMBAT);
    }


    @Override
    public void onEnable() {
        curYaw = mc.thePlayer.rotationYaw;
        curPitch = mc.thePlayer.rotationPitch;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        targets.clear();
        target = null; // 清空目标 (AutoBlock动画修复)
        entities.clear();

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

        /*
         *  CPS 运算
         */
        if (target != null && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() &&
                attacktimer.hasReached(randomClickDelay(Math.min(minCps.getObject(), maxCps.getObject()), Math.max(minCps.getObject(), maxCps.getObject())))) {
            cps++;
            attacktimer.reset();
        }

        /*
         *  转头 运算
         */

        if (rotations.getObject()) {
            if (target != null) {
                float[] rots;
                rots = utils.facePlayer(target, mouse_vl_fix.getObject(), random.getObject(), !instant.getObject(), prediction.getObject(), mouseFix.getObject()
                        , bestVector.getObject(), inaccuracy.getObject(), clampYaw.getObject(), rotationSpeed.getObject(), range.getObject());

                curYaw = rots[0];
                curPitch = rots[1];
            } else {
                curYaw = mc.thePlayer.rotationYaw;
                curPitch = mc.thePlayer.rotationPitch;
            }
        }

        if (target != null && show.getObject()) {
            EntityLivingBase entity = target;

            switch (esp.getCurrentMode()) {
                case "Box":
                    RenderUtils.renderBox(entity, espColor.getObject().getRGB());
                    break;
                case "2D":
                    RenderUtils.draw2DESP(entity, espColor.getObject().getRGB());
                    break;
                case "Icarus":
                    RenderUtils.drawIcarusESP(entity, espColor.getObject(), true);
                    break;
            }

        }

    }

    @EventTarget
    public void onMoveFly(MoveFlyEvent event) {
        if (moveFix.getObject() && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() && target != null)
            event.setYaw(curYaw);
    }

    @EventTarget
    public void onJump(JumpEvent event) {
        if (moveFix.getObject() && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() && target != null)
            event.setYaw(curYaw);
    }

    @EventTarget
    public void onSlient(MovementStateEvent event) {
        if (moveFix.getObject() && target != null && mc.thePlayer.getDistanceToEntity(target) <= range.getObject() && silentMoveFix.getObject()) {
            event.setSilentMoveFix(true);
            event.setYaw(curYaw);
        }
    }

    @EventTarget
    private void onMotionUpdate(MotionUpdateEvent event) {
        if (event.getEventType() == EventType.PRE) {
            if (target == null) {
                curYaw = mc.thePlayer.rotationYaw;
                curPitch = mc.thePlayer.rotationPitch;

                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword
                        && autoBlock.getObject() && isBlocking) {
                    ((IEntityPlayer) mc.thePlayer).setItemInUseCount(0);
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                }
                return;
            }


            if (rotations.getObject()) {
                if (slient.getObject()) {
                    event.setYaw(mc.thePlayer.rotationYawHead = curYaw);
                    event.setPitch(curPitch);
                }
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

        }

        if (event.getEventType() == EventType.POST) {
            if (target == null) return;

            if (!slient.getObject()) {
                mc.thePlayer.rotationYaw = curYaw;
                mc.thePlayer.rotationPitch = curPitch;
            }

            if ((mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && autoBlock.getObject() || mc.thePlayer.isBlocking()) && !isBlocking) {
                ((IEntityPlayer) mc.thePlayer).setItemInUseCount(mc.thePlayer.getHeldItem().getMaxItemUseDuration());
                mc.getNetHandler().getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                isBlocking = true;
            }
        }
    }

    private void attack(Entity entity) {
        if (mc.currentScreen != null && noInv.getObject()) {
            if (autoCloseInv.getObject())
                mc.thePlayer.closeScreen();
            else return;
        }

        if (rayCast.getObject())
            entity = utils.rayCastedEntity(range.getObject(), curYaw, curPitch);

        if (entity != null) {
            mc.thePlayer.swingItem();
            mc.playerController.attackEntity(mc.thePlayer, entity);
        } else {
            //    ChatUtils.info("Miss");
            mc.thePlayer.swingItem();
        }

    }


    private void update() {
        // 初始化变量
        try {
            // 添加实体
            targets.removeIf(ent -> !canAttack(ent));
            targets = this.getTargets();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 拿实体
        if (targets.size() == 0)  // 实体数量为0停止攻击
            target = null;
        else
            target = targets.get(0);// 设置攻击的Target
    }


    //取实体
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

    //计算CPS
    private static long randomClickDelay(final double minCPS, final double maxCPS) {
        return (long) ((Math.random() * (1000 / minCPS - 1000 / maxCPS) + 1) + 1000 / maxCPS);
    }


    //限制
    private static boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getObject()) return false;
            if (target instanceof EntityAnimal && !animals.getObject()) return false;
            if (target instanceof EntityWither && boss.getObject())
                return mc.thePlayer.getDistanceToEntity(target) <= range.getObject() + blockrange.getObject(); // true
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


    //优先级
    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle3 = Math.abs((angle1 - angle2)) % 360.0f;
        if (angle3 > 180.0f) {
            angle3 = 0.0f;
        }
        return angle3;
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

    void resetRotations(float yaw, float pitch, boolean silent) {
        if (silent) {
            mc.thePlayer.rotationYaw = yaw - yaw % 360 + mc.thePlayer.rotationYaw % 360;
        } else {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }
}
