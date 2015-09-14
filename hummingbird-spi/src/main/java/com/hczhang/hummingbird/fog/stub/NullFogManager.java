package com.hczhang.hummingbird.fog.stub;


import com.hczhang.hummingbird.fog.FogManager;
import com.hczhang.hummingbird.model.AggregateRoot;

/**
 * Created by steven on 2/5/15.
 */
public class NullFogManager implements FogManager {


    @Override
    public void set(AggregateRoot aggregateRoot) {

    }

    @Override
    public AggregateRoot get(Object key) {
        return null;
    }
}
