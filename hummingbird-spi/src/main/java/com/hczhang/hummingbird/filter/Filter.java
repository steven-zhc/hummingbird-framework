package com.hczhang.hummingbird.filter;

/**
 * Created by steven on 2/6/15.
 * @param <T>  the type parameter
 */
public interface Filter<T> {
    /**
     * Passes boolean.
     *
     * @param content the content
     * @return the boolean
     */
    boolean passes(T content);
}
