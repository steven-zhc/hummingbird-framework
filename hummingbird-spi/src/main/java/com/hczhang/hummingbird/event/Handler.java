package com.hczhang.hummingbird.event;

/**
 * Created by steven on 3/21/14.
 * @param <E>  the type parameter
 */
public interface Handler<E extends Event> {
    /**
     * Event handler
     * @param event the event
     */
    void handle(E event);

    // TODO: remove this method, get generic type from class

    /**
     * Get Event type
     * @return Event type
     */
    Class getEventType();
}
