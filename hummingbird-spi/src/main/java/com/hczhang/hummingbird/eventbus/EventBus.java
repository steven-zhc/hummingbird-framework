package com.hczhang.hummingbird.eventbus;


import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.Handler;

/**
 * Created by steven on 3/21/14.
 */
public interface EventBus {

    /**
     * Get event type
     * @return the event type
     */
    Class getEventType();

    /**
     * Publish an event on event bus.
     * @param event the event
     */
    void publish(Event event);

    /**
     * Subscribe event handler on Event Bus. The handler will be notified
     * when the related event are fired.
     * @param handler the handler
     */
    void subscribe(Handler<? extends Event> handler);

    /**
     * Unsubscribe handler from event bus.
     * @param handler the handler
     */
    void unsubscribe(Handler<? extends Event> handler);

    /**
     * Initial event bus
     */
    void init();

    /**
     * Start event bus.
     * @return Boolean boolean
     */
    boolean startup();

    /**
     * Shutdown event bus
     * @return boolean boolean
     */
    boolean shutdown();

    /**
     * Gets bus size.
     *
     * @return bus size
     */
    long getBusSize();

}
