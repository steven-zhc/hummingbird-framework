package com.hczhang.hummingbird.repository;


import com.hczhang.hummingbird.eventsource.EventSource;
import com.hczhang.hummingbird.model.EventSourceAggregateRoot;

/**
 * Any class implement this interface will use EventSource as a persistent strategy.
 * And model can be re-serializable from events come from EventSource.
 *
 * Created by steven on 4/9/14.
 */
public interface EventSourceRepository extends EventSource, Repository {

    /**
     * Load a specific object by aggregate {@code id} and {@code type} of aggregate. <br/>
     * The {@link Repository#load(Object id)} provide the same function and more easily use.
     * But that will cause a little bit performance cost. Because it need to do more to
     * identify the concrete type of aggregate. That's why we need this method if you know
     * what's the specific type.
     * @param <T>  aggregate class type
     * @param id aggregate id
     * @param type aggregate class type
     * @return aggregate t
     * @see
     */
    <T extends EventSourceAggregateRoot> T load(Object id, Class<T> type);

    /**
     * Try to load a specific version of Aggregate.<br/>.
     *
     * @param <T>   the type parameter
     * @param aid the aid
     * @param type the aggregate type
     * @param version the version
     * @return the t
     */
    <T extends EventSourceAggregateRoot> T load(Object aid, Class<T> type, long version);


    /**
     * Update target {@code model} to latest status.
     * @param model will be updated.
     * @return updated model.
     */
    EventSourceAggregateRoot refresh(EventSourceAggregateRoot model);

    /**
     * Get the latest version of model
     * @param aggid Aggregate ID
     * @return AggregateRoot version
     */
    long getModelVersion(Object aggid);

    /**
     * Take a snapshot
     * @param model the model
     */
    void takeSnapshot(EventSourceAggregateRoot model);

    /**
     * Retrieve snapshot according aggregate id
     * @param id aggregate id
     * @return Aggregate root
     */
    EventSourceAggregateRoot retrieveSnapshot(Object id);

    /**
     * Retrieve specific version of snapshot, which has the max version
     * TODO update return type using Generic Type
     * but less than or equals to {@code version}.
     * @param aid the aid
     * @param version the limit version
     * @return event source aggregate root
     */
    EventSourceAggregateRoot retrieveSnapshot(Object aid, long version);

    /**
     * Drop snapshot which is greater than target version (not include {@code version}).
     * <p>
     *     <b>Warning: </b> the action cannot be rollback.
     * </p>
     *
     * @param aid the aid
     * @param version the version
     */
    void dropSnapshot(Object aid, long version);
}
