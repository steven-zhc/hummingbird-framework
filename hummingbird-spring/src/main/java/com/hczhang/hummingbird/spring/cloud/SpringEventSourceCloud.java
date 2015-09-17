package com.hczhang.hummingbird.spring.cloud;

import com.hczhang.hummingbird.cloud.AnnotatedEventSourceCloud;
import com.hczhang.hummingbird.cloud.SimpleEventSourceCloud;
import com.hczhang.hummingbird.cloud.lifecycle.AggregateRootAware;
import com.hczhang.hummingbird.spring.cloud.lifecycle.SpringAggregateFactoryAware;
import com.hczhang.hummingbird.spring.cloud.lifecycle.SpringAggregateRepositoryAware;
import com.hczhang.hummingbird.spring.cloud.lifecycle.SpringDomainServiceAware;
import com.hczhang.hummingbird.spring.cloud.lifecycle.SpringEventHandlerAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * A special cloud implementation for Spring framework.
 * It inherits from {@code SimpleEventSourceCloud}. So please refer to {@link SimpleEventSourceCloud} to find
 * more details about the default information.<br>
 * Additional, we add some Aware Classes to cloud, which mean they could scan packages
 * and find out the following class in your class path and maintain them in spring application context .
 * <ul>
 *     <li>{@link SpringAggregateFactoryAware} - looking for {@code Factory}</li>
 *     <li>{@link SpringAggregateRepositoryAware} - looking for {@code AggregateRepository}</li>
 *     <li>{@link SpringDomainServiceAware} - looking for {@code DomainService}</li>
 *     <li>{@link SpringEventHandlerAware} - looking for {@code EventHandler}</li>
 *     <li>{@link AggregateRootAware} - looking for {@code AggregateRoot}</li>
 * </ul>
 *
 * Note: don't forget to setup QXSyncEventLog if you have query database.
 * <pre>
 *     this.addEventLog(new QXSyncEventLog());
 * </pre>
 *
 * Created by steven on 7/17/15.
 */
public class SpringEventSourceCloud extends SimpleEventSourceCloud implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedEventSourceCloud.class);

    protected String basePackage;

    protected ApplicationContext springContext;

    @Override
    public void config() {
        this.addLifecycleListener(new SpringAggregateFactoryAware(basePackage, springContext));
        this.addLifecycleListener(new SpringAggregateRepositoryAware(basePackage, springContext));
        this.addLifecycleListener(new SpringDomainServiceAware(basePackage, springContext));
        this.addLifecycleListener(new SpringEventHandlerAware(basePackage, springContext));

        this.addLifecycleListener(new AggregateRootAware(basePackage));

        super.config();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.springContext = applicationContext;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
