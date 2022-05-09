package dev.xix.event.bus;

public interface IEventListener<T> {
    void call(T event);
}
