package cn.loli.client.module.modules.combat;

import cn.loli.client.Main;
import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.types.EventType;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.ArrayList;

public class BackTrack extends Module {

    private final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();

    private EntityLivingBase entity = null;

    private WorldClient lastWorld;

    private final BooleanValue timer = new BooleanValue("Keep Alive", true);
    private final BooleanValue velocity = new BooleanValue("Velocity", true);
    private final BooleanValue onlyWhenNeed = new BooleanValue("Auto Check", false);

    private final NumberValue<Integer> delay = new NumberValue<>("Delay", 400, 0, 5000);
    private final NumberValue<Integer> rage_check = new NumberValue<>("Rage Check", 6, 0, 8);
    private final NumberValue<Integer> range = new NumberValue<>("Detect Check", 6, 0, 8);

    private final TimeHelper timeHelper = new TimeHelper();

    public BackTrack() {
        super("BackTrack", "Back Track", ModuleCategory.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventTarget
    private void onProcess(PacketEvent e){
        if (mc.getNetHandler().getNetworkManager().getNetHandler()
                != null && mc.getNetHandler().getNetworkManager().getNetHandler() instanceof OldServerPinger) return;
        if (mc.theWorld != null)
            if (e.getEventType() == EventType.RECEIVE) {
                synchronized (this) {
                    if (e.getPacket() instanceof S14PacketEntity) {
                        final Entity entity = ((S14PacketEntity) e.getPacket()).getEntity(mc.theWorld);
                        if (entity instanceof EntityLivingBase) {
                            Main.INSTANCE.realPosX += ((S14PacketEntity) e.getPacket()).func_149062_c();
                            Main.INSTANCE.realPosY += ((S14PacketEntity) e.getPacket()).func_149061_d();
                            Main.INSTANCE.realPosZ += ((S14PacketEntity) e.getPacket()).func_149064_e();
                        }
                    }
                    if (e.getPacket() instanceof S18PacketEntityTeleport) {
                        final Entity entity = mc.theWorld.getEntityByID(((S18PacketEntityTeleport) e.getPacket()).getEntityId());
                        if (entity instanceof EntityLivingBase) {
                            Main.INSTANCE.realPosX = ((S18PacketEntityTeleport) e.getPacket()).getX();
                            Main.INSTANCE.realPosY = ((S18PacketEntityTeleport) e.getPacket()).getY();
                            Main.INSTANCE.realPosZ = ((S18PacketEntityTeleport) e.getPacket()).getZ();
                        }
                    }

                    entity = mc.theWorld.playerEntities.stream().filter(entityPlayer -> entityPlayer != mc.thePlayer
                                        && entityPlayer.isEntityAlive() && mc.thePlayer.getDistanceToEntity(entityPlayer) < range.getObject())
                                .min((o1, o2) -> Float.compare(mc.thePlayer.getDistanceToEntity(o1), mc.thePlayer.getDistanceToEntity(o2))).orElse(null);

                    if (entity == null) {
                        resetPackets(mc.getNetHandler().
                                getNetworkManager().getNetHandler());
                        return;
                    }
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        if (lastWorld != mc.theWorld) {
                            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                            lastWorld = mc.theWorld;
                            return;
                        }
                        addPackets((Packet<INetHandler>) e.getPacket(), e);
                    }
                    lastWorld = mc.theWorld;
                }
            }
    }

    @EventTarget
    private void onTick(TickEvent event){
        if (mc.getNetHandler().getNetworkManager().getNetHandler()
                != null && mc.getNetHandler().getNetworkManager().getNetHandler() instanceof OldServerPinger) return;

        if (entity != null && mc.thePlayer != null &&
                mc.getNetHandler().getNetworkManager().getNetHandler() != null && mc.theWorld != null) {
            double d0 = (double)  Main.INSTANCE.realPosX / 32.0D;
            double d1 = (double)  Main.INSTANCE.realPosY / 32.0D;
            double d2 = (double)  Main.INSTANCE.realPosZ / 32.0D;
            double d3 = (double) entity.serverPosX / 32.0D;
            double d4 = (double) entity.serverPosY / 32.0D;
            double d5 = (double) entity.serverPosZ / 32.0D;
            AxisAlignedBB alignedBB = new AxisAlignedBB(d3 - (double) entity.width, d4, d5 - (double) entity.width, d3 + (double) entity.width, d4 + (double) entity.height, d5 + (double) entity.width);
            Vec3 positionEyes = mc.thePlayer.getPositionEyes(((IAccessorMinecraft) mc).getTimer().renderPartialTicks);
            double currentX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB.minX, alignedBB.maxX);
            double currentY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB.minY, alignedBB.maxY);
            double currentZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB.minZ, alignedBB.maxZ);
            AxisAlignedBB alignedBB2 = new AxisAlignedBB(d0 - (double) entity.width, d1, d2 - (double) entity.width, d0 + (double) entity.width, d1 + (double) entity.height, d2 + (double) entity.width);
            double realX = MathHelper.clamp_double(positionEyes.xCoord, alignedBB2.minX, alignedBB2.maxX);
            double realY = MathHelper.clamp_double(positionEyes.yCoord, alignedBB2.minY, alignedBB2.maxY);
            double realZ = MathHelper.clamp_double(positionEyes.zCoord, alignedBB2.minZ, alignedBB2.maxZ);
            double distance = rage_check.getObject();
            if (!mc.thePlayer.canEntityBeSeen(entity)) {
                distance = distance > 3 ? 3 : distance;
            }
            double bestX = MathHelper.clamp_double(positionEyes.xCoord, entity.getEntityBoundingBox().minX, entity.getEntityBoundingBox().maxX);
            double bestY = MathHelper.clamp_double(positionEyes.yCoord, entity.getEntityBoundingBox().minY, entity.getEntityBoundingBox().maxY);
            double bestZ = MathHelper.clamp_double(positionEyes.zCoord, entity.getEntityBoundingBox().minZ, entity.getEntityBoundingBox().maxZ);
            boolean b = false;
            if (positionEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > 2.9 || (mc.thePlayer.hurtTime < 8 && mc.thePlayer.hurtTime > 1)) {
                b = true;
            }
            if (!onlyWhenNeed.getObject()) {
                b = true;
            }
            if (!(b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) + 0.05) || !(mc.thePlayer.getDistance(d0, d1, d2) < distance) || timeHelper.hasReached((long) delay.getObject())) {
                resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                timeHelper.reset();
            }
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (packets.size() > 0) {
            synchronized (packets) {
                while (packets.size() != 0) {
                    try {
                        packets.get(0).processPacket(netHandler);
                    } catch (Exception ignored) {
                    }
                    packets.remove(packets.get(0));
                }

            }
        }
    }

    private void addPackets(Packet<INetHandler> packet, PacketEvent eventReadPacket) {
        synchronized (packets) {
            if (blockPacket(packet)) {
                packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private boolean blockPacket(Packet<?> packet) {
        if (packet instanceof S03PacketTimeUpdate) {
            return timer.getObject();
        } else if (packet instanceof S00PacketKeepAlive) {
            return timer.getObject();
        } else if (packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) {
            return velocity.getObject();
        } else {
            return packet instanceof S32PacketConfirmTransaction || packet instanceof S14PacketEntity || packet instanceof S19PacketEntityStatus || packet instanceof S19PacketEntityHeadLook || packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
        }
    }
}
