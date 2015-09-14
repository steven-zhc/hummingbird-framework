package com.hczhang.hummingbird.cloud.metadata;

import java.lang.reflect.Method;

/**
 * Created by steven on 12/16/14.
 */
public class ServiceCommandMetadata {

    /**
     * The class type of command handler
     */
    protected Class classType;

    /**
     * The command handler, {@code java.lang.reflect.Method} or {@code java.lang.reflect.Constructor}
     */
    protected Method member;

    /**
     * Instantiates a new Service command metadata.
     */
    public ServiceCommandMetadata() {
    }

    /**
     * Create a meda data of service command
     * @param classType The class type of command handler
     * @param member The command handler,
     */
    public ServiceCommandMetadata(Class classType, Method member) {
        this.classType = classType;
        this.member = member;
    }

    /**
     * Gets class type.
     *
     * @return the class type
     */
    public Class getClassType() {
        return classType;
    }

    /**
     * Sets class type.
     *
     * @param classType the class type
     */
    public void setClassType(Class classType) {
        this.classType = classType;
    }

    /**
     * Gets member.
     *
     * @return the member
     */
    public Method getMember() {
        return member;
    }

    /**
     * Sets member.
     *
     * @param member the member
     */
    public void setMember(Method member) {
        this.member = member;
    }
}
