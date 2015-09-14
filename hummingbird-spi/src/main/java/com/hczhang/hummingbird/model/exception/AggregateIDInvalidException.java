package com.hczhang.hummingbird.model.exception;

/**
 * Created by steven on 8/19/14.
 */
public class AggregateIDInvalidException extends ModelRuntimeException {

    /**
     * Instantiates a new Aggregate iD invalid exception.
     *
     * @param message the message
     */
    public AggregateIDInvalidException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Aggregate iD invalid exception.
     *
     * @param message the message
     * @param args the args
     */
    public AggregateIDInvalidException(String message, Object... args) {
        super(message, args);
    }
}
