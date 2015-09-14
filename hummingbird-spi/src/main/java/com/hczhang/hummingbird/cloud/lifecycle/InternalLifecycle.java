package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.EventSourceCloud;
import com.hczhang.hummingbird.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This is internal lifecycle. it's only for framework use.
 * Note. the cloud must extend EventSourceCloud
 *
 * @since 1.0  Created by steven on 2/2/15.
 */
public class InternalLifecycle extends LifecycleAwareAdapter {

    private static Logger logger = LoggerFactory.getLogger(InternalLifecycle.class);

    @Override
    public void postStart(Cloud ctx) {

        logger.info("Start Event Buses.");
        EventSourceCloud cloud = (EventSourceCloud) ctx;
        Map<String, EventBus> bus = cloud.getEventRouter().getEventBus();

        for (EventBus eb : bus.values()) {
            eb.startup();
        }
    }
}
