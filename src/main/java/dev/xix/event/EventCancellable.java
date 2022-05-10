package dev.xix.event;

public abstract class EventCancellable extends Event {

    private boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel(boolean state) {
        cancelled = state;
    }

    public void cancel() {
        cancelled = true;
    }
}