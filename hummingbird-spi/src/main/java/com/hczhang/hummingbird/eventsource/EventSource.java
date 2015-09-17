package com.hczhang.hummingbird.eventsource;


import com.hczhang.hummingbird.event.Event;

import java.util.Queue;

/**
 * An interface for EventSource. An EventSource should represent a data source of event.
 * Created by steven on 4/9/14.
 */
public interface EventSource {

    /**
     * Save an Event to EventSource
     * @param event IEvent.
     */
    void save(Event event);

    /**
     * Save a queue of Event to EventSource
     * @param eventStream A queue of events
     */
    void save(Queue<Event> eventStream);

    /**
     * Load all events from EventSource related to id
     * @param id Aggregate ID
     * @return An queue of Event
     */
    Queue<Event> loadEvents(Object id);

    /**
     * Get event stream since specific version (not include, %3E {@code sinceVersion})
     * @param id Aggregate ID
     * @param sinceVersion After this version will be return.
     * @return A stream of events.
     */
    Queue<Event> loadEvents(Object id, long sinceVersion);

    /**
     * Load events, which version is belong to ({@code startVersion}, {@code endVersion} ].
     *
     * @param aid the aid
     * @param startVersion the start version
     * @param endVersion the end version
     * @return  A stream of events
     */
    Queue<Event> loadEvents(Object aid, long startVersion, long endVersion);

    /**
     * Drop events which is greater than target version (not include {@code version}).
     *
     * <p>
     *     <b>Warning: </b> the action cannot be rollback.
     *
     *
     * @param aid the aid
     * @param version the version
     */
    void dropEvents(Object aid, long version);
}
