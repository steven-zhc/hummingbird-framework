package com.hczhang.hummingbird.model;

/**
 * Created by steven on 12/16/14.
 * @param <T>  the type parameter
 */
public interface AggregateFactory<T extends AggregateRoot> {
    /**
     * Load t.
     *
     * @param id the id
     * @return the t
     */
    T load(Object id);
}
