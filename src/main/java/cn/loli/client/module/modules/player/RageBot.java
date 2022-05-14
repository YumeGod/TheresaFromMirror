package cn.loli.client.module.modules.player;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.utils.misc.WorldUtil;


import dev.xix.event.EventType;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.NumberProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RageBot extends Module {

    private Vec3 aimed;
    boolean aim;
    private final BooleanProperty invisibles = new BooleanProperty("Invisibles", false);
    private final BooleanProperty players = new BooleanProperty("Players", true);
    private final BooleanProperty animals = new BooleanProperty("Animals", false);
    private final BooleanProperty mobs = new BooleanProperty("Mobs", true);
    private final BooleanProperty armorStand = new BooleanProperty("Armor Stand", true);
    private final BooleanProperty villagers = new BooleanProperty("Villagers", false);
    private final BooleanProperty team = new BooleanProperty("Team", false);
    private final NumberProperty<Integer> range = new NumberProperty<>("Range", 50, 40, 120 , 1);
    private final NumberProperty<Float> fov = new NumberProperty<>("FOV", 360f, 0f, 360f , 1f);
    private final BooleanProperty headshot = new BooleanProperty("Shot Head", false);
    private final NumberProperty<Float> pre = new NumberProperty<>("Velocity", 0.8f, 0f, 2f , 0.1f);
    private final NumberProperty<Float> y = new NumberProperty<>("Y - Offset", 0f, -1f, 1f , 0.1f);

    public RageBot() {
        super("RageBot", "Make you attack entity easily", ModuleCategory.PLAYER);
    }


    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }


    List<EntityLivingBase> list = new ArrayList<>();
    ;

    private final IEventListener<MotionUpdateEvent> onUpdatePre = event ->
    {
        list.clear();
        final List<EntityLivingBase> targets = WorldUtil.getLivingEntities().stream()
                .filter(this::canAttack)
                .sorted(Comparator.comparing(e -> mc.thePlayer.getDistanceToEntity(e)))
                .collect(Collectors.toList());


        list.addAll(targets.stream().filter((entity) -> entity instanceof EntityGiantZombie || entity instanceof EntityWither).collect(Collectors.toList()));
        list.addAll(targets.stream().filter((entity) -> !(entity instanceof EntityGiantZombie || entity instanceof EntityWither)).collect(Collectors.toList()));


        if (list.size() <= 0) {
            aim = false;
            return;
        }

        aim = true;
        aimed = getFixedLocation(list.get(0), pre.getPropertyValue(), headshot.getPropertyValue());

        final float[] rotations = getRotationToLocation(aimed);
        if (event.getEventType() == EventType.PRE) {
            event.setYaw(mc.thePlayer.rotationYawHead = rotations[0]);
            event.setPitch(rotations[1]);
        } else {
            if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword || mc.thePlayer.getHeldItem().getItem() instanceof ItemBook))
                mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
        }
    };

    private final IEventListener<RenderEvent> onRender3D = event ->
    {
        if (!aim)
            return;

        double posX = this.aimed.xCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX();
        double posY = this.aimed.yCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY();
        double posZ = this.aimed.zCoord - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ();

        RenderUtils.drawBlockESP(posX - 0.5, posY - 0.5, posZ - 0.5, new Color(255, 0, 0, 100).getRGB(), new Color(0xFFE900).getRGB(), 0.4f, 0.1f);
    };


    private Vec3 getFixedLocation(final EntityLivingBase entity, final float velocity, final boolean head) {
        double x = entity.posX + ((entity.posX - entity.lastTickPosX) * velocity);
        double y = entity.posY + ((entity.posY - entity.lastTickPosY) * (velocity * 0.3)) + (head ? entity.getEyeHeight() : 1.0) + this.y.getPropertyValue();
        double z = entity.posZ + ((entity.posZ - entity.lastTickPosZ) * velocity);
        return new Vec3(x, y, z);
    }

    private boolean canAttack(EntityLivingBase target) {
        if (target instanceof EntityPlayer || target instanceof EntityAnimal || target instanceof EntityMob || target instanceof INpc) {
            if (target instanceof EntityPlayer && !players.getPropertyValue()) return false;
            if (target instanceof EntityAnimal && !animals.getPropertyValue()) return false;
            if (target instanceof EntityMob && !mobs.getPropertyValue()) return false;
            if (target instanceof INpc && !villagers.getPropertyValue()) return false;
        }

        if (target instanceof EntityArmorStand && !armorStand.getPropertyValue()) return false;
        if (target.isOnSameTeam(mc.thePlayer) && !team.getPropertyValue()) return false;
        if (target.isInvisible() && !invisibles.getPropertyValue()) return false;
        if (!isInFOV(target, fov.getPropertyValue())) return false;
        if (!target.canEntityBeSeen(mc.thePlayer)) return false;

        return target != mc.thePlayer && target.isEntityAlive() && mc.thePlayer.getDistanceToEntity(target) <= range.getPropertyValue();
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

    private static float[] getRotationToLocation(final Vec3 loc) {
        double xDiff = loc.xCoord - Minecraft.getMinecraft().thePlayer.posX;
        double yDiff = loc.yCoord - (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double zDiff = loc.zCoord - Minecraft.getMinecraft().thePlayer.posZ;

        double distance = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(yDiff, distance) * 180.0D / Math.PI));

        return new float[]{yaw, pitch};
    }
}
