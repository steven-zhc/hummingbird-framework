package com.hczhang.hummingbird.eventbus;

/**
 * Created by steven on 9/16/14.
 */
public interface EventRouterFactory {

    /**
     * Gets event router.
     *
     * @return the event router
     */
    EventRouter getEventRouter();
}
