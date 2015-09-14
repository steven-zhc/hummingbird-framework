package com.hczhang.hummingbird.cloud;

import com.hczhang.hummingbird.cloud.lifecycle.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cloud implement EventSource. Moreover it could understand some annotations are supplied by Hummingbird framework
 * <pre>
 *    AnnotatedEventSourceCloud cloud = new AnnotatedEventSourceCloud();
 *    cloud.scan("PACKAGE NAME");
 *    cloud.launch();
 * </pre>
 * Created by steven on 10/3/14.
 */
public class AnnotatedEventSourceCloud extends SimpleEventSourceCloud {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedEventSourceCloud.class);

    public void scanPackage(String basePackage) {
        if (StringUtils.isNotBlank(basePackage)) {
            this.addLifecycleListener(new AggregateFactoryAware(basePackage));
            this.addLifecycleListener(new AggregateRepositoryAware(basePackage));
            this.addLifecycleListener(new AggregateRootAware(basePackage));
            this.addLifecycleListener(new DomainServiceAware(basePackage));
            this.addLifecycleListener(new EventHandlerAware(basePackage));
        } else {
            logger.warn("Please setup base package.");
        }
    }


}
