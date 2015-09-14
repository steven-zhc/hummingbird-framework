package com.hczhang.hummingbird.util;

/**
 * Created by steven on 9/15/14.
 */
public class DDDException  extends RuntimeException {

    /**
     * The constant STUB.
     */
    public static final String STUB = "\\{\\}";

    /**
     * Instantiates a new DDD exception.
     *
     * @param message the message
     */
    public DDDException(String message) {
        super(message);
    }

    /**
     * Instantiates a new DDD exception.
     *
     * @param message the message
     * @param args the args
     */
    public DDDException(String message, Object... args) {
        super(String.format(message.replaceAll(STUB, "%s"), args));
    }
}
