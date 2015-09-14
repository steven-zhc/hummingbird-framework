package com.hczhang.hummingbird.eventbus;


import com.hczhang.hummingbird.event.Event;

/**
 * Simple Event Router. It will maintain SimpleEventBus.
 * We could generate customize EventBus if you override <code>CreateEventBus</code> method
 * Created by steven on 5/13/14.
 */
public class SimpleEventRouter extends AbstractEventRouter {

    @Override
    protected EventBus createEventBus(Class<? extends Event> eventType) {
        return new SimpleEventBus(eventType);
    }

}
