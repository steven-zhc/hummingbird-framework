package com.hczhang.hummingbird.cloud.metadata;

import java.lang.reflect.Member;

/**
 * This is an internal used class. Store some metadata for cloud.
 * Created by steven on 12/19/14.
 */
public class AggregateCommandMetadata {

    /**
     * The class type of command handler
     */
    protected Class classType;

    /**
     * true, if the command will be handled by a constructor, otherwise method will do it.
     */
    protected boolean isConstructor;

    /**
     * The command handler, {@code java.lang.reflect.Method} or {@code java.lang.reflect.Constructor}
     */
    protected Member member;

    /**
     * Create a meda data of aggregate command
     * @param classType The class type of command handler
     * @param isConstructor true, if the command will be handled by a constructor, otherwise method will do it.
     * @param member The command handler,
     * or
     */
    public AggregateCommandMetadata(Class classType, boolean isConstructor, Member member) {
        this.classType = classType;
        this.isConstructor = isConstructor;
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
     * Is constructor.
     *
     * @return the boolean
     */
    public boolean isConstructor() {
        return isConstructor;
    }

    /**
     * Sets constructor.
     *
     * @param isConstructor the is constructor
     */
    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    /**
     * Gets member.
     *
     * @return the member
     */
    public Member getMember() {
        return member;
    }

    /**
     * Sets member.
     *
     * @param member the member
     */
    public void setMember(Member member) {
        this.member = member;
    }
}
