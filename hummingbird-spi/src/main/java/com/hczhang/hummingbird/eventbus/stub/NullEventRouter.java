package com.hczhang.hummingbird.eventbus.stub;


import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.eventbus.AbstractEventRouter;
import com.hczhang.hummingbird.eventbus.EventBus;

/**
 * Created by steven on 2/2/15.
 */
public class NullEventRouter extends AbstractEventRouter {

    @Override
    protected EventBus createEventBus(Class<? extends Event> eventType) {
        return new NullEventBus(eventType);
    }
}
