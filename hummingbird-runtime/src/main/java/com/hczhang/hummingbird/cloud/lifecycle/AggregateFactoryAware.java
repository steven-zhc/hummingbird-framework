package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.CloudRuntimeException;
import com.hczhang.hummingbird.cloud.SimpleEventSourceCloud;
import com.hczhang.hummingbird.model.AggregateFactory;
import com.hczhang.hummingbird.model.annotation.Factory;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.util.HBAssert;
import com.hczhang.hummingbird.util.AnnotationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by steven on 10/3/14.
 */
public class AggregateFactoryAware extends PackageLifecycleAware {

    private static Logger logger = LoggerFactory.getLogger(AggregateFactoryAware.class);

    protected SimpleEventSourceCloud cloud;

    public AggregateFactoryAware(String basePackage) {
        super(basePackage);
    }


    @Override
    public void preInit(Cloud ctx) {

        if (!(SimpleEventSourceCloud.class.isAssignableFrom(ctx.getClass()))) {
            logger.error("The cloud doesn't extend from AbstractEventSourceCloud");
            throw new CloudRuntimeException("Cloud initial error");
        }

        cloud = (SimpleEventSourceCloud) ctx;
    }

    @Override
    public void postInit(Cloud ctx) {
        HBAssert.notNull(ctx, ModelRuntimeException.class, "Cloud Context must not be null.");

        logger.info("===> Scan @Factory annotation in class path [{}].", getBasePackage());

        Set<Class<?>> classes = AnnotationUtils.findClasses(getBasePackage(), Factory.class);
        Set<String> factories = new HashSet<String>();

        for (Class c : classes) {
            try {
                Factory ds = (Factory) c.getAnnotation(Factory.class);

                cloud.addRadiator(newInstance(ds, c), ds.value());
                factories.add(c.getSimpleName());

                break;

            } catch (Exception e) {
                logger.warn("Cannot register Event Handler.", e);
            }
        }
        logger.info("Registered AggregateFactory: [{}]", StringUtils.join(factories.iterator(), ","));

    }

    public AggregateFactory newInstance(Factory ds, Class cls) throws Exception {
        return (AggregateFactory) cls.newInstance();
    }
}
