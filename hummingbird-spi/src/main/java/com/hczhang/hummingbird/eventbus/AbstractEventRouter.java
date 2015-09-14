package com.hczhang.hummingbird.eventbus;

import com.codahale.metrics.Timer;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.filter.FilterManager;
import com.hczhang.hummingbird.metrics.MetricsManager;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by steven on 5/13/14.
 */
public abstract class AbstractEventRouter implements EventRouter {

    private static Logger logger = LoggerFactory.getLogger(AbstractEventRouter.class);

    /**
     * The Hub.
     */
    protected static Map<String, EventBus> hub;

    /**
     * The Filter manager.
     */
    protected FilterManager filterManager;

    // Metrics
    private final Timer timer = MetricsManager.metrics.timer(name(EventRouter.class, "timer"));

    /**
     * Instantiates a new Abstract event router.
     */
    protected AbstractEventRouter() {
        this.init();
    }

    /**
     * Init void.
     */
    protected void init() {
        if (hub == null) {
            hub = new ConcurrentHashMap();
        }
    }

    /**
     * Create event bus.
     *
     * @param eventType the event type
     * @return the event bus
     */
    protected abstract EventBus createEventBus(Class<? extends Event> eventType);

    @Override
    public void propagate(Event event) {

        Validate.notNull(event, "Event is null");

        EventBus bus = this.getEventBus(event.getClass());

        if (filterManager == null || filterManager.apply(event) ) {
            bus.publish(event);
        }
    }

    @Override
    public void propagate(Queue<Event> stream) {

        Validate.notNull(stream, "Event Stream is null");


        for (Event event : stream) {

            if (filterManager == null || filterManager.apply(event)) {
                // get event bus
                EventBus bus = this.getEventBus(event.getClass());

                Timer.Context context = MetricsManager.getTimerContext(timer);

                try {
                    // publish event
                    bus.publish(event);
                } finally {
                    MetricsManager.stopTimerContext(context);
                }
            }
        }

    }

    @Override
    public EventBus getEventBus(Class<? extends Event> eventType) {

        Validate.notNull(eventType, "EventType is null");

        if (hub == null) {
            String msg = "The EventRouter didn't initialize correctly.";
            logger.error(msg);
            throw new EventRouterException(msg);
        }

        EventBus bus = null;

        if (hub.containsKey(eventType.getName())) {
            bus = hub.get(eventType.getName());
        } else {
            bus = this.createEventBus(eventType);

            hub.put(eventType.getName(), bus);
        }

        return bus;
    }

    @Override
    public Map<String, EventBus> getEventBus() {
        return this.hub;
    }

    /**
     * Gets filter manager.
     *
     * @return the filter manager
     */
    public FilterManager getFilterManager() {
        return filterManager;
    }

    /**
     * Sets filter manager.
     *
     * @param filterManager the filter manager
     */
    public void setFilterManager(FilterManager filterManager) {
        this.filterManager = filterManager;
    }
}
