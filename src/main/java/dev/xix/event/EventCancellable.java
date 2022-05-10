package dev.xix.event;

import dev.xix.event.bus.Cancellable;

public abstract class EventCancellable extends Event implements Cancellable  {

    private boolean cancelled;

    protected EventCancellable() {
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean state) {
        cancelled = state;
    }
}