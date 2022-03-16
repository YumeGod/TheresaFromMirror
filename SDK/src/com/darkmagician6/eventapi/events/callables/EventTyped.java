package com.darkmagician6.eventapi.events.callables;

import com.darkmagician6.eventapi.events.Event;
import com.darkmagician6.eventapi.types.EventType;

public class EventTyped implements Event {
    private final EventType type;

    public EventTyped(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }
}
