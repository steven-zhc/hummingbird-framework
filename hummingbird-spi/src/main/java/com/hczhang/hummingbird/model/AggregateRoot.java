package com.hczhang.hummingbird.model;

import java.io.Serializable;

/**
 * Created by steven on 9/2/14.
 * @param <ID>  the type parameter
 */
public interface AggregateRoot<ID> extends Serializable {

    /**
     * Get Aggregate ID of this model.
     * @return aggregate id.
     */
    ID getAggregateID();

    /**
     * AggregateRoot version.
     * @return model version.
     */
    long getVersion();

    /**
     * Indicate if the object has been deleted.
     * @return true, the model has been deleted. shouldn't use it ever. otherwise return true.
     */
    boolean isDeleted();

}
