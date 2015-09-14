package com.hczhang.hummingbird.eventlog;

import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.model.AggregateRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple event log implements. just log event data.
 *
 * Created by steven on 1/5/15.
 */
public class SimpleEventLog extends AbstractEventLog {

    private static Logger logger = LoggerFactory.getLogger(SimpleEventLog.class);

    @Override
    public void recordEvent(Event event, AggregateRoot root) {

        logger.debug("Fired a event: {}.", event.toString());

    }

}
