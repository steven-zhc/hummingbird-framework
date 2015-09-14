package com.hczhang.hummingbird.fog;

import com.codahale.metrics.Counter;
import com.codahale.metrics.RatioGauge;
import com.codahale.metrics.Timer;
import com.hczhang.hummingbird.filter.FilterManager;
import com.hczhang.hummingbird.metrics.MetricsManager;
import com.hczhang.hummingbird.model.AggregateRoot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codahale.metrics.MetricRegistry.name;

/**
 * Created by steven on 2/8/15.
 */
public abstract class AbstractFog implements Fog {

    private static Logger logger = LoggerFactory.getLogger(AbstractFog.class);

    /**
     * The Filter manager.
     */
    protected FilterManager filterManager;

    private final Counter hits = MetricsManager.metrics.counter(name(this.getClass(), "hit"));
    private final Timer timer = MetricsManager.metrics.timer(name(this.getClass(), "timer"));

    /**
     * Instantiates a new Abstract fog.
     */
    public AbstractFog() {

        MetricsManager.metrics.register(name(this.getClass(), "hit", "ratio"),
                new RatioGauge() {
                    @Override
                    protected Ratio getRatio() {
                        return Ratio.of(hits.getCount(), timer.getCount());
                    }
                });
    }

    @Override
    public void addDew(AggregateRoot aggregateRoot) {

        if (filterManager == null || filterManager.apply(aggregateRoot)) {
            registerAggregate(aggregateRoot);
        }
    }

    @Override
    public AggregateRoot getDew(Object aid) {

        Timer.Context context = MetricsManager.getTimerContext(timer);

        AggregateRoot ar = null;
        try {
            ar = this.getAggregate(aid);
        } finally {
            MetricsManager.stopTimerContext(context);
        }

        if (ar != null) {
            MetricsManager.inc(hits);
        }
        return ar;
    }

    /**
     * Gets aggregate.
     *
     * @param aid the aid
     * @return the aggregate
     */
    public abstract AggregateRoot getAggregate(Object aid);

    /**
     * Register aggregate.
     *
     * @param aggregateRoot the aggregate root
     */
    public abstract void registerAggregate(AggregateRoot aggregateRoot);

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
