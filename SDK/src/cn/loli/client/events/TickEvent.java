

package cn.loli.client.events;

import com.darkmagician6.eventapi.events.Event;

public class TickEvent implements Event {
    private final EventType eventType;

    public TickEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
