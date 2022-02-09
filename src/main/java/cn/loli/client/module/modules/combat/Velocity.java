package cn.loli.client.module.modules.combat;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.implementations.IS27PacketExplosion;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Velocity extends Module {

    private final Queue<TimestampedPacket> packets = new ConcurrentLinkedDeque<>();

    private final NumberValue<Integer> horizon = new NumberValue<>("Horizon", 80, 0, 100);
    private final NumberValue<Integer> vertical = new NumberValue<>("Vertical", 80, 0, 100);
    private final BooleanValue explosion = new BooleanValue("Explosion", true);
    private final BooleanValue legit = new BooleanValue("Jump", false);
    private final BooleanValue choke = new BooleanValue("Choke", false);
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
        }

        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();
            if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                // ChatUtils.send("Received A KB Packet");
                event.setCancelled(true);

                if (horizon.getObject() == 0 && vertical.getObject() == 0)
                    return;


                if (debug.getObject())
                    ChatUtils.info(String.valueOf(Math.abs(packet.getMotionX()) + Math.abs(packet.getMotionZ()) + Math.abs(packet.getMotionY())));

                if (choke.getObject() &&
                        Math.abs(packet.getMotionX()) + Math.abs(packet.getMotionZ()) + Math.abs(packet.getMotionY()) < limit.getObject())
                    addPackets(new TimestampedPacket(event.getPacket(), System.currentTimeMillis()), event);
                else
                    mc.thePlayer.setVelocity(packet.getMotionX() / 8000d * hor, packet.getMotionY() / 8000d * ver, packet.getMotionZ() / 8000d * hor);

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
}
