package com.hczhang.hummingbird.eventbus;

import com.codahale.metrics.Histogram;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.event.Handler;
import com.hczhang.hummingbird.metrics.MetricsManager;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by steven on 5/13/14.
 */
public abstract class AbstractEventBus implements EventBus {

    private static Logger logger = LoggerFactory.getLogger(AbstractEventBus.class);

    private Set<Handler> listeners;

    private Histogram histogram;

    /**
     * Instantiates a new Abstract event bus.
     */
    protected AbstractEventBus() {
        listeners = new CopyOnWriteArraySet();
    }

    @Override
    public void subscribe(Handler<? extends Event> handler) {

        Validate.notNull(handler, "Handler is null");

        if (histogram == null) {
            histogram = MetricsManager.getHistogram(name(EventBus.class, "queue", getEventType().getSimpleName(), "size"));
        }

        if (listeners.contains(handler)) {
            logger.warn("Re-subscribe handler: [{}]", handler.getClass().getSimpleName());
        } else {
            logger.debug("Subscribe handler: [{}]", handler.getClass().getSimpleName());
            listeners.add(handler);
        }
    }

    @Override
    public void unsubscribe(Handler<? extends Event> handler) {
        Validate.notNull(handler, "Handler is null");

        logger.debug("Unsubscribe handler: [{}]", handler.getClass().getSimpleName());

        if (listeners.contains(handler)) {
            listeners.remove(handler);
        }
    }

    /**
     * Handle void.
     *
     * @param event the event
     */
    protected void handle(Event event) {
        for (Handler h : getListeners()) {
            try {
                logger.info("Event handler [{}] is working on event [{}]",
                        h.getClass().getSimpleName(), event.getClass().getSimpleName());
                h.handle(event);
            } catch (Exception e) {
                logger.error("Got a exception when EventHandler[{}] was working on Event [{}].",
                        h.getClass().getSimpleName(), event.getClass().getSimpleName(), e);
            }
        }

        MetricsManager.updateHistogram(histogram, this.getBusSize());
    }

    /**
     * Gets listeners.
     *
     * @return the listeners
     */
    protected Set<Handler> getListeners() {
        return listeners;
    }

    @Override
    public void init() {

    }
}
