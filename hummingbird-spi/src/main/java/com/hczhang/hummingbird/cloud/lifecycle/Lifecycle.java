package com.hczhang.hummingbird.cloud.lifecycle;

/**
 * The interface could represent a lifecycle of object or context.
 * The basic cycle is: {@code init} -> {@code start} -> {@code stop}.
 * We also provide a method to identify if the object is running.
 *
 * Created by steven on 5/21/14.
 */
public interface Lifecycle {

    /**
     * Initial status of lifecycle.
     */
    void initializing();

    /**
     * Start status of lifecycle.
     */
    void starting();

    /**
     * Stop status of lifecycle.
     */
    void stopping();

    /**
     * Return if the object is running.
     * @return true if the object is running, otherwise return false.
     */
    boolean isRunning();
}
