package com.hczhang.hummingbird.cloud.lifecycle;

import com.codahale.metrics.JmxReporter;
import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.metrics.MetricsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by steven on 10/3/14.
 */
public class JmxMetricsAware extends LifecycleAwareAdapter {

    private static Logger logger = LoggerFactory.getLogger(JmxMetricsAware.class);

    @Override
    public void preStart(Cloud ctx) {
        if (MetricsManager.enable) {
            logger.info("===> Start JMX Metrics Report ...");
            final JmxReporter reporter = JmxReporter.forRegistry(MetricsManager.metrics).build();
            reporter.start();
        }
    }
}
