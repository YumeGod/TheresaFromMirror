package dev.xix.event;

public class EventCancellable extends Event {

    private boolean cancelled;

    protected EventCancellable() {
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean state) {
        cancelled = state;
    }
}
