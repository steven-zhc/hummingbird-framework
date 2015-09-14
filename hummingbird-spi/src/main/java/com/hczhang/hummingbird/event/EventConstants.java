package com.hczhang.hummingbird.event;

/**
 * Created by steven on 6/4/14.
 */
public class EventConstants {

    /**
     * Disable event source. There is no version on model.
     */
    public static final long VERSION_NULL = Long.MIN_VALUE;

    /**
     * the key of model type on payload
     */
    public static final String META_MODEL_TYPE = "mt";
    /**
     * the key of event type on payload
     */
    public static final String META_EVENT_TYPE = "et";

    /**
     * Take snapshot for every 8 version of the model.
     */
    public static final long SNAPSHOT_FACTOR = 8l;

}
