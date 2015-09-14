package com.hczhang.hummingbird.spring.cloud.lifecycle;

import com.hczhang.hummingbird.annotation.Source;
import com.hczhang.hummingbird.cloud.lifecycle.EventHandlerAware;
import com.hczhang.hummingbird.event.Handler;
import com.hczhang.hummingbird.event.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by steven on 10/3/14.
 */
public class SpringEventHandlerAware extends EventHandlerAware {

    private static Logger logger = LoggerFactory.getLogger(SpringEventHandlerAware.class);

    private ApplicationContext context;

    public SpringEventHandlerAware(String basePackage, ApplicationContext springContext) {
        super(basePackage);

        this.context = context;
    }

    @Override
    public Handler newInstance(EventHandler ds, Class cls) throws Exception {

        if (ds.value() == Source.SPRING) {
            return (Handler) context.getBean(cls);
        } else {
            return super.newInstance(ds, cls);
        }
    }
}
