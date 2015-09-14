package com.hczhang.hummingbird.cloud.lifecycle;


import com.hczhang.hummingbird.cloud.Cloud;

/**
 * This is lifecycle listener, which will be notified if the lifecycle has been changed.
 * Created by steven on 5/21/14.
 */
public interface LifecycleAware {

    /**
     * This method will be invoked by framework before Object initialize.
     * @param ctx the ctx
     */
    void preInit(Cloud ctx);

    /**
     * This method will be invoked by framework after Object has been initialized.
     * @param ctx the ctx
     */
    void postInit(Cloud ctx);

    /**
     * This method will be invoked by framework before object starte.
     * @param ctx the ctx
     */
    void preStart(Cloud ctx);

    /**
     * This method will be invoked by framework after object has been started.
     * @param ctx the ctx
     */
    void postStart(Cloud ctx);

    /**
     * This method will be invoked by framework before object close.
     * @param ctx the ctx
     */
    void preClose(Cloud ctx);

    /**
     * This method will be invoked by framework after object has been closed.
     * @param ctx the ctx
     */
    void postClose(Cloud ctx);
}
