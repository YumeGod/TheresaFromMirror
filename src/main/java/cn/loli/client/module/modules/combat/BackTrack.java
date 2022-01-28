package cn.loli.client.module.modules.combat;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.TimeHelper;
import cn.loli.client.value.BooleanValue;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;

import java.util.ArrayList;

public class BackTrack extends Module {

    private final ArrayList<Packet<INetHandler>> packets = new ArrayList<>();

    private final BooleanValue timer = new BooleanValue("Keep Alive", true);
    private final BooleanValue velocity = new BooleanValue("Velocity", true);

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
    }


    private void resetPackets(INetHandler netHandler) {
        if (this.packets.size() > 0) {
            synchronized (this.packets) {
                while (this.packets.size() != 0) {
                    try {
                        this.packets.get(0).processPacket(netHandler);
                    } catch (Exception ignored) {
                    }
                    this.packets.remove(this.packets.get(0));
                }

            }
        }
    }

    private void addPackets(Packet packet, PacketEvent eventReadPacket) {
        synchronized (this.packets) {
            if (this.blockPacket(packet)) {
                this.packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private boolean blockPacket(Packet<?> packet) {
        if (packet instanceof S03PacketTimeUpdate) {
            return this.timer.getObject();
        } else if (packet instanceof S00PacketKeepAlive) {
            return this.timer.getObject();
        } else if (packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) {
            return this.velocity.getObject();
        } else {
            return packet instanceof S32PacketConfirmTransaction || packet instanceof S14PacketEntity || packet instanceof S19PacketEntityStatus || packet instanceof S19PacketEntityHeadLook || packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
        }
    }
}
