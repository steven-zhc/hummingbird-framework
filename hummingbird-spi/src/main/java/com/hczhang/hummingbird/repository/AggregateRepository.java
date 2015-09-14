package com.hczhang.hummingbird.repository;


import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * Created by steven on 1/5/15.
 * @param <T>  the type parameter
 */
public interface AggregateRepository<T extends AggregateRoot> extends Repository<T> {

    /**
     * Exists boolean.
     *
     * @param id the id
     * @return the boolean
     */
    boolean exists(Object id);

    /**
     * Save void.
     *
     * @param obj the obj
     */
    void save(T obj);

    /**
     * Update void.
     *
     * @param obj the obj
     */
    void update(T obj);

    /**
     * Delete void.
     *
     * @param obj the obj
     */
    void delete(T obj);
}
