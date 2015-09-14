package com.hczhang.hummingbird.spring.cloud.lifecycle;

import com.hczhang.hummingbird.annotation.Source;
import com.hczhang.hummingbird.cloud.lifecycle.DomainServiceAware;
import com.hczhang.hummingbird.model.annotation.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by steven on 10/3/14.
 */
public class SpringDomainServiceAware extends DomainServiceAware {

    private static Logger logger = LoggerFactory.getLogger(SpringDomainServiceAware.class);

    private ApplicationContext context;

    public SpringDomainServiceAware(String basePackage, ApplicationContext springContext) {
        super(basePackage);

        this.context = context;
    }

    @Override
    public Object newInstance(DomainService ds, Class cls) throws Exception {
        if (ds.value() == Source.SPRING) {
            return context.getBean(cls);
        } else {
            return super.newInstance(ds, cls);
        }
    }
}
