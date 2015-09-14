package com.hczhang.hummingbird.repository;


import com.hczhang.hummingbird.util.DDDException;

/**
 * Created by steven on 8/19/14.
 */
public class RepositoryRuntimeException extends DDDException {

    /**
     * Instantiates a new Repository runtime exception.
     *
     * @param message the message
     */
    public RepositoryRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Repository runtime exception.
     *
     * @param message the message
     * @param args the args
     */
    public RepositoryRuntimeException(String message, Object... args) {
        super(message, args);
    }
}
