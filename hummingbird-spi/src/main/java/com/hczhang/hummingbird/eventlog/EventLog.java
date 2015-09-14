package com.hczhang.hummingbird.eventlog;


import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.model.AggregateRoot;

import java.util.Queue;

/**
 * An interface for event log.
 * This is a log service for store event by order.
 * Created by steven on 1/2/15.
 */
public interface EventLog {
    /**
     * Save an Event Log
     * @param event IEvent.
     * @param root the root
     * @see
     */
    void log(Event event, AggregateRoot root);

    /**
     * Save an EventStream to Log
     * @param eventStream A queue of events
     * @param root the root
     * @see
     */
    void log(Queue<Event> eventStream, AggregateRoot root);
}
