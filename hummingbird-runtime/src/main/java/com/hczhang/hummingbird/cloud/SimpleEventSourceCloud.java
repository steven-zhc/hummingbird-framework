package com.hczhang.hummingbird.cloud;

import com.hczhang.hummingbird.eventbus.SimpleEventRouter;
import com.hczhang.hummingbird.model.AggregateRoot;
import com.hczhang.hummingbird.repository.AggregateRepository;

/**
 * A simple implementation of GenericEventSourceCloud.
 * You could think of this, SimpleEventSourceCloud provide lots of default implementations for cloud.
 *
 * <dl>
 *     <dt>NullFogManager</dt>
 *     <dd>The Simple EventSource Cloud doesn't have fog</dd>
 *     <dt>SimpleEventRouter</dt>
 *     <dd>The cloud has a simple event router</dd>
 *     <dt>QXSyncEventLog</dt>
 *     <dd>Setup a QX sync function. Please don't forget to set aggregate repository</dd>
 * </dl>
 *
 * For configuration details, please refer to GenericEventSourceCloud class.
 *
 * <p>
 * The remaining jobs you need to do:
 *
 * <h3>Step 1: Setup EventSource</h3>
 * <pre>
 *     cloud.setEventSourceRepository(EventSource);
 * </pre>
 * </p>
 *
 * <p>
 * <h3>Step 2: Register Aggregate/Aggregate Repository/Domain Service</h3>
 * <pre>
 *     // Add a domain service object
 *     cloud.addCrystal(domainServiceObject);
 *
 *     // repository could be null if you don't need QX or legacy system.
 *     cloud.registerAggregate(ClassOfAggregate, AggregateRepository);
 * </pre>
 * </p>
 *
 * <p>
 * <h3>Step 3 Optinal: setup QX system</h3>
 * Note: this step is optional if you don't want to have the synchronizer for query system (QX).
 * <pre>
 *     this.addEventLog(new QXSyncEventLog());
 * </pre>
 *
 * </p>
 *
 * Created by steven on 9/15/14.
 */
public class SimpleEventSourceCloud extends GenericEventSourceCloud {

    @Override
    public void config() {
        this.setEventRouter(new SimpleEventRouter());

    }

    public void registerAggregate(Class<? extends AggregateRoot> aggregateType, AggregateRepository repository) {
        this.registerDewPrototype(aggregateType);
        this.addRepository(repository, aggregateType);
    }
}
