package com.hczhang.hummingbird.repository;


import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * The repository provides an abstraction of the storage of aggregates.
 * It is a interface between Object and persistent strategy.
 * AggregateRoot doesn't care about the details how to serialize, persistent and what kinds of persistent.
 *
 * Created by steven on 3/20/14.
 * @param <T>  the type parameter
 */
public interface Repository<T extends AggregateRoot> {

    /**
     * Load a specific object by aggregate {@code id}.
     * @param id Aggregate id of object.
     * @return A new object which loaded from repository
     */
    T load(Object id);

}
