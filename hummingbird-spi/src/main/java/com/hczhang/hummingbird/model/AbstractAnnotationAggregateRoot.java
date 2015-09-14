package com.hczhang.hummingbird.model;


import com.hczhang.hummingbird.cloud.GenericEventSourceCloud;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.util.HBAssert;

import java.lang.reflect.Field;

/**
 * Created by steven on 10/13/14.
 * @param <T>  the type parameter
 */
public abstract class AbstractAnnotationAggregateRoot<T> extends AbstractEventSourceAggregateRoot<T> {

    @Override
    public T getAggregateID() {

        Field f = GenericEventSourceCloud.getIDField(getClass());
        HBAssert.notNull(f, ModelRuntimeException.class, "Missing @AggregateID annotation or mis-configuration.");

        try {
            f.setAccessible(true);

            return (T) f.get(this);
        } catch (IllegalAccessException e) {
            throw new ModelRuntimeException("Cannot get value of Aggregate ID. Error MSG: [{}]", e.getMessage());
        }
    }
}
