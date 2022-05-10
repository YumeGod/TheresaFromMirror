package cn.loli.client.events;

import dev.xix.event.Event;
import dev.xix.event.EventType;

public class TickEvent extends Event {
    private final EventType eventType;

    public TickEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public EventType getEventType() {
        return eventType;
    }
}
