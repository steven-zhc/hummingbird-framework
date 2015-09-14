package com.hczhang.hummingbird.eventsource;


import com.hczhang.hummingbird.util.DDDException;

/**
 * Created by steven on 9/15/14.
 */
public class EventSourceException extends DDDException {

    /**
     * Instantiates a new Event source exception.
     *
     * @param message the message
     */
    public EventSourceException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Event source exception.
     *
     * @param message the message
     * @param args the args
     */
    public EventSourceException(String message, Object... args) {
        super(message, args);
    }
}
