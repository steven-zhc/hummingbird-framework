package com.hczhang.hummingbird.cloud.lifecycle;

/**
 * This is abstract class for LifecycleAware.
 *
 * Created by steven on 10/3/14.
 */
public class PackageLifecycleAware extends LifecycleAwareAdapter {

    private String basePackage;

    public PackageLifecycleAware(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
