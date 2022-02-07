package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class Spoofer extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Forge", "Forge", "Lunar", "LabyMod", "PVP-L", "C-B", "Geyser");

    public Spoofer() {
        super("Spoofer", "Spoof Other Client", ModuleCategory.MISC);
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
    private void onPacket(PacketEvent event) {
        if (event.getPacket() instanceof S3FPacketCustomPayload) {
            if (((S3FPacketCustomPayload) event.getPacket()).getChannelName().equalsIgnoreCase("REGISTER")) {
                event.setCancelled(true);
            }

            if (((S3FPacketCustomPayload) event.getPacket()).getChannelName().equalsIgnoreCase("MC|Brand")) {
                switch (modes.getCurrentMode()) {
                    case "Forge": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML")));
                        break;
                    }
                    case "Lunar": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer())).writeString("Lunar-Client")));
                        break;
                    }
                    case "LabyMod": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("LMC")));
                        break;
                    }
                    case "PVP-L": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("PLC18")));
                        break;
                    }
                    case "C-B": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("CB")));
                        break;
                    }
                    case "Geyser": {
                        mc.getNetHandler().getNetworkManager()
                                .sendPacket(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Geyser")));
                    }
                }
                ChatUtils.info("Succeed");
                event.setCancelled(true);
            }
        }
    }
}
