package com.hczhang.hummingbird.cloud;


import com.hczhang.hummingbird.model.AggregateFactory;
import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * This is adaptor interface for legacy system.
 * The methods in the class provide a sort of ways to load data in legacy system.
 * The legacy system usually save data in RDBMS. And there isn't event source to record the status of data.
 *
 * Created by steven on 4/6/15.
 */
public interface LegacyAdaptor {

    /**
     * Add a Factory of aggregate root.
     * @param factory factory bean
     * @param aggregateType what class type of aggregate
     *                      root could be generate by
     */
    void addRadiator(AggregateFactory factory, Class<? extends AggregateRoot> aggregateType);
}
