package com.hczhang.hummingbird.serializer;

/**
 * Created by steven on 9/8/14.
 */
public interface SerializerFactory {

    /**
     * Gets serializer.
     *
     * @return the serializer
     */
    public Serializer getSerializer();
}
