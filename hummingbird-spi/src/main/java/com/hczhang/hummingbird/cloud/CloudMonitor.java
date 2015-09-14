package com.hczhang.hummingbird.cloud;


import com.hczhang.hummingbird.cloud.lifecycle.LifecycleAware;

/**
 * An interface to provide methods to monitor cloud behaviors.
 * Created by steven on 5/22/14.
 */
public interface CloudMonitor {

    /**
     * Add a listener to monitor the {@code ICould} lifecycle.
     * @param listener A Class extended
     * which has callback methods.
     */
    void addLifecycleListener(LifecycleAware listener);

    /**
     * Remove a lifecycle listener.
     * @param listener will be removed listener.
     */
    void removeLifecycleListener(LifecycleAware listener);

}
