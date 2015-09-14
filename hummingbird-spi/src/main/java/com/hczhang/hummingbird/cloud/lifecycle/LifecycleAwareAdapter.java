package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;

/**
 * This is abstract class for LifecycleAware.
 * which keep all of methods in lifecycle empty.
 *
 * Extends this class and implement any methods you really concern.
 *
 * Created by steven on 10/3/14.
 */
public class LifecycleAwareAdapter implements LifecycleAware {

    @Override
    public void preInit(Cloud ctx) {

    }

    @Override
    public void postInit(Cloud ctx) {

    }

    @Override
    public void preStart(Cloud ctx) {

    }

    @Override
    public void postStart(Cloud ctx) {

    }

    @Override
    public void preClose(Cloud ctx) {

    }

    @Override
    public void postClose(Cloud ctx) {

    }
}
