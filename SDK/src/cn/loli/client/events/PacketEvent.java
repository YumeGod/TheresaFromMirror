

package cn.loli.client.events;

import com.darkmagician6.eventapi.events.callables.EventCancellable;
import com.darkmagician6.eventapi.types.EventType;
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
