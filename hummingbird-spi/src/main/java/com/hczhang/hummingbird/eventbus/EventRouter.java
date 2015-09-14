package com.hczhang.hummingbird.eventbus;


import com.hczhang.hummingbird.event.Event;

import java.util.Map;
import java.util.Queue;

/**
 * Created by steven on 5/2/14.
 */
public interface EventRouter {

    /**
     * Propagate void.
     *
     * @param event the event
     */
    void propagate(Event event);

    /**
     * Propagate void.
     *
     * @param stream the stream
     */
    void propagate(Queue<Event> stream);

    /**
     * Gets event bus.
     *
     * @param eventType the event type
     * @return the event bus
     */
    EventBus getEventBus(Class<? extends Event> eventType);

    /**
     * Gets event bus.
     *
     * @return the event bus
     */
    Map<String, EventBus> getEventBus();

}
