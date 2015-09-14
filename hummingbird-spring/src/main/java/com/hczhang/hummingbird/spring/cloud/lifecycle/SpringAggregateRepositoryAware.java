package com.hczhang.hummingbird.spring.cloud.lifecycle;

import com.hczhang.hummingbird.annotation.Source;
import com.hczhang.hummingbird.cloud.lifecycle.AggregateRepositoryAware;
import com.hczhang.hummingbird.repository.AggregateRepository;
import com.hczhang.hummingbird.repository.annotation.AggRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by steven on 10/3/14.
 */
public class SpringAggregateRepositoryAware extends AggregateRepositoryAware {

    private static Logger logger = LoggerFactory.getLogger(SpringAggregateRepositoryAware.class);

    private ApplicationContext context;

    public SpringAggregateRepositoryAware(String basePackage, ApplicationContext springContext) {
        super(basePackage);

        this.context = context;
    }

    @Override
    public AggregateRepository newInstance(AggRepository ds, Class cls) throws Exception {

        if (ds.source() == Source.SPRING) {
            return (AggregateRepository) context.getBean(cls);
        } else {
            return super.newInstance(ds, cls);
        }

    }

}
