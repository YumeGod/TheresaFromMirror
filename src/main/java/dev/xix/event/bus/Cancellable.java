package dev.xix.event.bus;


public interface Cancellable {


    boolean isCancelled();

    /**
     * Sets the cancelled state of the event.
     *
     * @param state
     *         Whether the event should be cancelled or not.
     */
    void setCancelled(boolean state);

}
