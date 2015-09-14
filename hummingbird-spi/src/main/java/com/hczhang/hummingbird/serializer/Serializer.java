package com.hczhang.hummingbird.serializer;

/**
 * Created by steven on 4/16/14.
 */
public interface Serializer {
    /**
     * Serialize byte [ ].
     *
     * @param object the object
     * @return the byte [ ]
     */
    public byte[] serialize(Object object);

    /**
     * Deserialize t.
     *
     * @param <T>  the type parameter
     * @param blob the blob
     * @param tClass the t class
     * @return the t
     */
    public <T> T deserialize(byte[] blob, Class<T> tClass);
}
