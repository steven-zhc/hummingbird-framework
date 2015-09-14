package com.hczhang.hummingbird.model;

/**
 * Created by steven on 8/22/14.
 * @param <T>  the type parameter
 */
public interface Specification<T> {

    /**
     * Is satisfied by.
     *
     * @param model the model
     * @return the boolean
     */
    boolean isSatisfiedBy(T model);
}
