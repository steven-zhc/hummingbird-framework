package com.hczhang.hummingbird.spring;


import com.hczhang.hummingbird.cloud.CloudRuntimeException;

/**
 * Created by steven on 8/13/15.
 */
public class SpringExtensionError extends CloudRuntimeException {
    public SpringExtensionError(String message) {
        super(message);
    }

    public SpringExtensionError(String message, Object... args) {
        super(message, args);
    }
}
