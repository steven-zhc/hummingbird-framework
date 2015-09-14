package com.hczhang.hummingbird.model.exception;


import com.hczhang.hummingbird.util.DDDException;

/**
 * Created by steven on 8/19/14.
 */
public class ModelRuntimeException extends DDDException {

    /**
     * Instantiates a new Model runtime exception.
     *
     * @param message the message
     */
    public ModelRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Model runtime exception.
     *
     * @param message the message
     * @param args the args
     */
    public ModelRuntimeException(String message, Object... args) {
        super(message, args);
    }
}
