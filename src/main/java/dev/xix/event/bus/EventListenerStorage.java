package dev.xix.event.bus;

public final class EventListenerStorage<T> {
    private final Object owner;
    private final IEventListener<T> callback;

    public EventListenerStorage(Object owner, IEventListener<T> callback) {
        this.owner = owner;
        this.callback = callback;
    }

    public Object owner() {
        return owner;
    }

    public IEventListener<T> callback() {
        return callback;
    }
}