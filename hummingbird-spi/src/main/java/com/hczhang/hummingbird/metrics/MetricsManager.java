package com.hczhang.hummingbird.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.hczhang.hummingbird.util.HBAssert;

/**
 * Created by steven on 2/10/15.
 */
public class MetricsManager {

    /**
     * The constant enable.
     */
    public static boolean enable = false;

    /**
     * The constant metrics.
     */
    public static final MetricRegistry metrics = new MetricRegistry();

    /**
     * Sets enable.
     *
     * @param value the value
     */
    public void setEnable(boolean value) {
        enable = value;
    }

    /**
     * Gets timer context.
     *
     * @param timer the timer
     * @return the timer context
     */
// ---------------- Timer -----------------------
    public static Timer.Context getTimerContext(Timer timer) {
        HBAssert.notNull(timer, "Timer cannot be null.");

        Timer.Context context = null;
        if (MetricsManager.enable) {
            context = timer.time();
        }

        return context;
    }

    /**
     * Stop timer context.
     *
     * @param timer the timer
     */
    public static void stopTimerContext(Timer.Context timer) {
        if (MetricsManager.enable && timer != null) {
            timer.stop();
        }
    }

    /**
     * Gets counter.
     *
     * @param name the name
     * @return the counter
     */
// ---------------- Counter -----------------------
    public static Counter getCounter(String name) {
        if (enable == false) {
            return null;
        } else {
            return metrics.counter(name);
        }
    }

    /**
     * Inc void.
     *
     * @param counter the counter
     */
    public static void inc(Counter counter) {
        if (enable == true && counter != null) {
            counter.inc();
        }
    }

    /**
     * Dec void.
     *
     * @param counter the counter
     */
    public static void dec(Counter counter) {
        if (enable == true && counter != null) {
            counter.dec();
        }
    }

    /**
     * Gets histogram.
     *
     * @param name the name
     * @return the histogram
     */
// ---------------- Histogram -----------------------
    public static Histogram getHistogram(String name) {
        if (enable == false) {
            return null;
        }

        return metrics.histogram(name);
    }

    /**
     * Update histogram.
     *
     * @param histogram the histogram
     * @param value the value
     */
    public static void updateHistogram(Histogram histogram, long value) {
        if (enable == true && histogram != null) {
            histogram.update(value);
        }
    }
}
