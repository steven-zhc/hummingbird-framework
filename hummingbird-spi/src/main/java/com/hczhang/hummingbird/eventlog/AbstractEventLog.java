package com.hczhang.hummingbird.eventlog;

import com.codahale.metrics.Timer;
import com.hczhang.hummingbird.event.Event;
import com.hczhang.hummingbird.filter.FilterManager;
import com.hczhang.hummingbird.metrics.MetricsManager;
import com.hczhang.hummingbird.model.AggregateRoot;

import java.util.Queue;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by steven on 2/7/15.
 */
public abstract class AbstractEventLog implements EventLog {

    /**
     * The Filter manager.
     */
    protected FilterManager filterManager;

    /**
     * Record event.
     *
     * @param event the event
     * @param root the root
     */
    public abstract void recordEvent(Event event, AggregateRoot root);

    // Metrics
    private final Timer timer = MetricsManager.metrics.timer(name(EventLog.class, "timer"));

    @Override
    public void log(Event event, AggregateRoot root) {

        if (filterManager == null || filterManager.apply(event)) {

            Timer.Context context = MetricsManager.getTimerContext(timer);

            try {
                recordEvent(event, root);
            } finally {
                MetricsManager.stopTimerContext(context);
            }
        }
    }

    @Override
    public void log(Queue<Event> eventStream, AggregateRoot root) {

        for (Event e : eventStream) {
            this.log(e, root);
        }

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
