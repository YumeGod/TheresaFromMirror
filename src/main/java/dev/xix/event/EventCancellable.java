package dev.xix.event;

public abstract class EventCancellable extends Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean state) {
        cancelled = state;
    }

    public void cancel() {
        cancelled = true;
    }
}