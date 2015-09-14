package com.hczhang.hummingbird.cloud;


import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.Handler;
import com.hczhang.hummingbird.eventbus.EventRouter;
import com.hczhang.hummingbird.eventlog.EventLog;
import com.hczhang.hummingbird.fog.FogManager;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.repository.EventSourceRepository;
import com.hczhang.hummingbird.transaction.TransactionContext;

import java.util.Set;

/**
 * This Cloud is based on Event Source principle.
 *
 * Created by steven on 5/23/14.
 *
 * @since 0.1
 */
public interface EventSourceCloud extends Cloud {

    /**
     * Get event source based repository object.
     *
     * @return Repository is subclass of IEventSourceRepository
     * @see
     */
    EventSourceRepository getEventSourceRepository();

    /**
     * Get event router.
     * @return A event router
     * @see
     */
    EventRouter getEventRouter();

    /**
     * Get an event logs.
     * @return event log service.
     */
    Set<EventLog> getEventLogs();

    /**
     * Get fog, which is aggregate pool.
     * @return object of Fog
     */
    FogManager getFogManager();

    /**
     * Create a event handler and listen to event bus
     * @param handlerType the handler type
     */
    void addEventHandler(Class<? extends Handler> handlerType);

    /**
     * Add a event handler instance, which will listen events from event bus.
     * @param handler the handler
     */
    void addEventHandler(Handler handler);

    /**
     * Spread a event to a aggregate class. the main steps this method does:
     * 1. get or create a concrete aggregate object.
     * 2. apply event on object created by step 1.
     * @param <T>   Concrete aggregate class.
     * @param context Transaction context
     * @param event Event.
     * @param type Aggregate class type
     * @return t t
     */
    <T extends AggregateRoot> T spread(TransactionContext context, Event event, Class<T> type);

    /**
     * Compare with the other event spread methods. this method will spread event to concrete object.
     *
     * @param <T>   the type parameter
     * @param context the context
     * @param event the event
     * @param obj the obj
     * @return t t
     */
    <T extends AggregateRoot> T spread(TransactionContext context, Event event, T obj);


    /**
     * Valide Event Source Cloud configuration.
     */
    void validate();
}
