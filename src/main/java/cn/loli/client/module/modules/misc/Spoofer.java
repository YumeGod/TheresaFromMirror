package cn.loli.client.module.modules.misc;

import cn.loli.client.events.PacketEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.ChatUtils;
import cn.loli.client.value.ModeValue;
import com.darkmagician6.eventapi.EventTarget;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class Spoofer extends Module {

    private final ModeValue modes = new ModeValue("Mode", "Forge", "Forge", "Lunar", "LabyMod", "PVP-L", "C-B", "Geyser");

    public Spoofer() {
        super("Spoofer", "Spoof Other Client", ModuleCategory.MISC);
    }


    public Packet getPacket() {
        switch (modes.getCurrentMode()) {
            case "Forge": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("FML")));
            }
            case "Lunar": {
                return (new C17PacketCustomPayload("REGISTER", (new PacketBuffer(Unpooled.buffer())).writeString("Lunar-Client")));
            }
            case "LabyMod": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("LMC")));
            }
            case "PVP-L": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("PLC18")));
            }
            case "C-B": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("CB")));
            }
            case "Geyser": {
                return (new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString("Geyser")));
            }
        }

        return new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).writeString(ClientBrandRetriever.getClientModName()));
    }
}
