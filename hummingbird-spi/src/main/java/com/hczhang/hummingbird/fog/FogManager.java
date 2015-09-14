package com.hczhang.hummingbird.fog;


import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * Created by steven on 7/16/15.
 */
public interface FogManager {

    /**
     * Set Aggregate into Fogs. In other words, cache the Aggregate
     *
     * @param aggregateRoot the aggregate root
     */
    void set(AggregateRoot aggregateRoot);

    /**
     * Get aggregate root from Fogs. In other words, get Aggregate from cache.
     *
     * @param key the key
     * @return the aggregate root
     */
    AggregateRoot get(Object key);
}
