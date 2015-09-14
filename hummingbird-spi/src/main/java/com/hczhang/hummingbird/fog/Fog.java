package com.hczhang.hummingbird.fog;


import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * Created by steven on 2/5/15.
 */
public interface Fog {

    /**
     * Add dew.
     *
     * @param aggregateRoot the aggregate root
     */
    void addDew(AggregateRoot aggregateRoot);

    /**
     * Gets dew.
     *
     * @param aid the aid
     * @return the dew
     */
    AggregateRoot getDew(Object aid);
}
