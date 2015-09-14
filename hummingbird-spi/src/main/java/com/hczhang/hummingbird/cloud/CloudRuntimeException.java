package com.hczhang.hummingbird.cloud;

import com.hczhang.hummingbird.util.DDDException;

/**
 * The type Cloud runtime exception.
 */
public class CloudRuntimeException extends DDDException {

    /**
     * Instantiates a new Cloud runtime exception.
     *
     * @param message the message
     */
    public CloudRuntimeException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Cloud runtime exception.
     * <pre>
     *     CloudRuntimeException e = new CloudRuntimeException("test {} - {}", "1", "2");
     *     String msg = e.getMessage(); // test 1 - 2
     * </pre>
     *
     * @param message the message
     * @param args the args
     */
    public CloudRuntimeException(String message, Object... args) {
        super(message, args);
    }
}
