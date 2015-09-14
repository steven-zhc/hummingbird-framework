package com.hczhang.hummingbird.eventbus;

/**
 * Created by steven on 5/16/14.
 */
public class EventRouterException extends RuntimeException {

    /**
     * The constant STUB.
     */
    public static final String STUB = "\\{\\}";

    /**
     * Instantiates a new Event router exception.
     *
     * @param message the message
     */
    public EventRouterException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Event router exception.
     *
     * @param message the message
     * @param args the args
     */
    public EventRouterException(String message, Object... args) {
        super(String.format(message.replaceAll(STUB, "%s"), args));
    }
}
