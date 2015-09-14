package com.hczhang.hummingbird.cloud;


import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.repository.AggregateRepository;

/**
 * This is adaptor interface for QX system.
 * Based on CQRS concept, the CX data should be syncronized to QX system, which usually using RDBMS.
 * Register a sort of aggregate repository to help framework to know how to update QX system.
 *
 * Created by steven on 7/16/15.
 */
public interface QXAdaptor {
    /**
     * The framework want to manage the repository class.
     * That will help framework to persistent aggregate to result database.
     * @param repository instance of repository
     * @param aggregateType aggregate class type
     */
    void addRepository(AggregateRepository repository, Class<? extends AggregateRoot> aggregateType);
}
