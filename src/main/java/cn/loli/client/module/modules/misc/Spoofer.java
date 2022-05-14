package cn.loli.client.module.modules.misc;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import dev.xix.property.impl.EnumProperty;
import io.netty.buffer.Unpooled;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class Spoofer extends Module {

    private enum MODE {
        NONE("Forge"), LUNAR("Lunar"), LM("LabyMod"), PVPL("PvP-L"), CHEATBREAKER("C-B"), GEYSER("Geyser");

        private final String name;

        MODE(String s) {
            this.name = s;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final EnumProperty modes = new EnumProperty<>("Mode", MODE.NONE);

    public Spoofer() {
        super("Spoofer", "Spoof Other Client", ModuleCategory.MISC);
    }


    public Packet getPacket() {
        switch (modes.getPropertyValue().toString()) {
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
