package com.hczhang.hummingbird.eventbus.stub;


import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.eventbus.AbstractEventBus;

/**
 * Created by steven on 2/2/15.
 */
public class NullEventBus extends AbstractEventBus {

    private Class<? extends Event> eventType;
    public NullEventBus(Class<? extends Event> eventType) {
        this.eventType = eventType;
    }

    @Override
    public Class getEventType() {
        return eventType;
    }

    @Override
    public void publish(Event event) {

    }

    @Override
    public boolean startup() {
        return true;
    }

    @Override
    public boolean shutdown() {
        return true;
    }

    @Override
    public long getBusSize() {
        return 0;
    }
}
