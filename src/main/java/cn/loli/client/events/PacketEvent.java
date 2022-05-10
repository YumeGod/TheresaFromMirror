

package cn.loli.client.events;

import dev.xix.event.EventCancellable;
import dev.xix.event.EventType;
import net.minecraft.network.Packet;

public class PacketEvent extends EventCancellable {
    private final EventType eventType;
    private Packet packet;

    public PacketEvent(EventType eventType, Packet packet) {
        this.eventType = eventType;
        this.packet = packet;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
