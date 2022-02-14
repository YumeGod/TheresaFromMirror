package cn.loli.client.module.modules.combat;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.implementations.IS27PacketExplosion;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

// TODO: Nearby Entity Check (this idea skid from "ExHiBitIoN" <- best client ngl)
public class Velocity extends Module {

    private final Queue<TimestampedPacket> packets = new ConcurrentLinkedDeque<>();

    private final NumberValue<Integer> horizon = new NumberValue<>("Horizon", 80, 0, 100);
    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 80, 0, 100);
    private final BooleanValue explosion = new BooleanValue("Explosion", true);
    private final BooleanValue legit = new BooleanValue("Jump", false);
    private final BooleanValue choke = new BooleanValue("Choke", false);
    private final BooleanValue reverseHorizon = new BooleanValue("Reverse Horizon", false);
    private final BooleanValue kbAlert = new BooleanValue("Alert", false);
    private final NumberValue<Integer> delay = new NumberValue<>("Choke Delay", 400, 0, 800);
    public final BooleanValue antifall = new BooleanValue("AntiFall", false);
    private final NumberValue<Integer> limit = new NumberValue<>("Choke Limit", 8000, 0, 25000);
    private final BooleanValue debug = new BooleanValue("Debug", false);

    private WorldClient lastWorld;
    private final TimeHelper timeHelper = new TimeHelper();


    public Velocity() {
        super("Velocity", "Reduce your knock-back", ModuleCategory.COMBAT);
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
    private void onUpdate(UpdateEvent e) {
//        ChatUtils.info("Hurttime: " + mc.thePlayer.hurtResistantTime + " | " + mc.thePlayer.velocityChanged);
        if (legit.getObject()) {
            if (mc.thePlayer.hurtTime == 10 && mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }
        } else
            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
    }

    @EventTarget
    private void onPacket(PacketEvent event) {
        float hor = horizon.getObject() / 100f;
        float ver = vertical.getObject() / 100f;

        if (reverseHorizon.getObject()) hor = -hor;

        if (legit.getObject())
            return;

        if (event.getPacket() instanceof S27PacketExplosion && explosion.getObject()) {
            S27PacketExplosion packet = (S27PacketExplosion) event.getPacket();
            if (horizon.getObject() == 0 && vertical.getObject() == 0) {
                event.setCancelled(true);
                return;
            }

            ((IS27PacketExplosion) packet).setX(packet.func_149149_c() * hor);
            ((IS27PacketExplosion) packet).setY(packet.func_149144_d() * ver);
            ((IS27PacketExplosion) packet).setZ(packet.func_149147_e() * hor);

            if (debug.getObject())
                ChatUtils.info("Giga " + (Math.abs(packet.func_149149_c()) + Math.abs(packet.func_149144_d()) + Math.abs(packet.func_149147_e())) * 8000);

            if (!event.isCancelled()) {
                if (choke.getObject() && (Math.abs(packet.func_149149_c()) + Math.abs(packet.func_149144_d()) + Math.abs(packet.func_149147_e())) * 8000 < limit.getObject()) {
                    event.setCancelled(true);
                    addPackets(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()), event);
                }
            }

            if (kbAlert.getObject())
                kbAlert(event);
        }

        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                // ChatUtils.send("Received A KB Packet");

                if (horizon.getObject() == 0 && vertical.getObject() == 0) {
                    event.setCancelled(true);
                }

                if (debug.getObject())
                    ChatUtils.info(String.valueOf(Math.abs(packet.getMotionX()) + Math.abs(packet.getMotionZ()) + Math.abs(packet.getMotionY())));

                if (choke.getObject() &&
                        Math.abs(packet.getMotionX()) + Math.abs(packet.getMotionZ()) + Math.abs(packet.getMotionY()) < limit.getObject()) {
                    addPackets(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()), event);
                    event.setCancelled(true);
                }

                if (kbAlert.getObject())
                    kbAlert(event);
            }
        }
    }


    private void addPackets(TimestampedPacket packet, PacketEvent eventReadPacket) {
        synchronized (packets) {
            if (blockPacket(packet.packet)) {
                packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (packets.size() > 0) {
            synchronized (packets) {
                try {
                    for (final TimestampedPacket timestampedPacket : packets) {
                        final long timestamp = timestampedPacket.timestamp;
                        if (Math.abs(timestamp - System.currentTimeMillis()) >= delay.getObject()) {
                            timestampedPacket.packet.processPacket(netHandler);
                            packets.remove(timestampedPacket);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private boolean blockPacket(Packet<?> packet) {
        return packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion;
    }

    private static class TimestampedPacket {
        private final Packet<INetHandler> packet;
        private final long timestamp;

        public TimestampedPacket(final Packet<INetHandler> packet, final long timestamp) {
            this.packet = packet;
            this.timestamp = timestamp;
        }
    }

    // KB Alert by belris2u
    private void kbAlert(PacketEvent e) {
        if (mc.thePlayer.hurtTime == 0 && mc.thePlayer.ticksExisted > 60) {
            // 收到击退包的tick 如果没有收到伤害 新建线程延迟1tick继续检测
            new Thread(() -> {
                try {
                    Thread.sleep(250);  // After 1 tick
                    if (mc.thePlayer.hurtTime == 0) {   // 如果还是没有收到任何伤害
                        if (e.getPacket() instanceof S12PacketEntityVelocity) { // handle S12
                            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                            ChatUtils.error("You may have been KB checked! S12 #" + mc.thePlayer.ticksExisted);
                            // 如果包被cancel就补充一个 不要把所有包都cancel掉然后用setVelocity, 这样我获取不到状态
                            if (e.isCancelled()) {
                                mc.thePlayer.setVelocity((double) packet.getMotionX() / 8000.0D, (double) packet.getMotionY() / 8000.0D, (double) packet.getMotionZ() / 8000.0D);
                            }
                        } else { // handle S27
                            S27PacketExplosion packet = (S27PacketExplosion) e.getPacket();
                            ChatUtils.info("You may have been KB checked! S27 #" + mc.thePlayer.ticksExisted);
                            if (e.isCancelled()) {
                                mc.thePlayer.setVelocity(packet.getX(), packet.getY(), packet.getZ());
                            }
                        }
                    }
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }).start();
        }
    }
}
