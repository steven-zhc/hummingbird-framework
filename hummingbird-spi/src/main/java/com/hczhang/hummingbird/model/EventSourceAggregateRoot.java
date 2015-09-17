package com.hczhang.hummingbird.model;



import com.hczhang.hummingbird.event.Event;

import java.util.Queue;

/**
 * The class exteds IEventSourceModel means the model is based on Event Source strategy.
 * It should provide methods to save Events, load Events and publish Events.
 *
 * Created by steven on 3/24/14.
 * @param <ID>  the type parameter
 */
public interface EventSourceAggregateRoot<ID> extends AggregateRoot<ID> {

    /**
     * Clear un-committed events. This method will clean up the uncommitted events queue in aggregate root.
     * In other words, it will remove all of un-committed events.
     */
    void purgeEvents();

    /**
     * Get all un-committed event. Those events will not send out to Event Router,
     * but have applied to current model object.
     *
     * @return A un-committed evens stream
     *
     */
    Queue<Event> getUncommittedEvents();

    /**
     * Apply Event to current model.
     * This method push event to uncommitted events stream
     * and invoke the event handler method in the same model class.
     * @param event will be applied.
     *
     */
    void applyEvent(Event event);

    /**
     * This is for internal use.
     * The client shouldn't call this method directly.
     * @param v the v
     */
    void updateVersion(long v);

}
