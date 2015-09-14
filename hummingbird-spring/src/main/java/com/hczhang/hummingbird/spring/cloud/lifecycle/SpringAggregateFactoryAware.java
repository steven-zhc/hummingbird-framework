package com.hczhang.hummingbird.spring.cloud.lifecycle;

import com.hczhang.hummingbird.annotation.Source;
import com.hczhang.hummingbird.cloud.lifecycle.AggregateFactoryAware;
import com.hczhang.hummingbird.model.AggregateFactory;
import com.hczhang.hummingbird.model.annotation.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by steven on 10/3/14.
 */
public class SpringAggregateFactoryAware extends AggregateFactoryAware {

    private static Logger logger = LoggerFactory.getLogger(SpringAggregateFactoryAware.class);

    private ApplicationContext context;

    public SpringAggregateFactoryAware(String basePackage, ApplicationContext context) {
        super(basePackage);

        this.context = context;
    }

    @Override
    public AggregateFactory newInstance(Factory ds, Class cls) throws Exception {

        if (ds.source() == Source.SPRING) {
            return (AggregateFactory) context.getBean(cls);
        } else {
            return super.newInstance(ds, cls);
        }
    }
}
