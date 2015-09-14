package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.CloudRuntimeException;
import com.hczhang.hummingbird.cloud.SimpleEventSourceCloud;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.repository.AggregateRepository;
import com.hczhang.hummingbird.repository.annotation.AggRepository;
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
public class AggregateRepositoryAware extends PackageLifecycleAware {

    private static Logger logger = LoggerFactory.getLogger(AggregateRepositoryAware.class);

    protected SimpleEventSourceCloud cloud;

    public AggregateRepositoryAware(String basePackage) {
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

        logger.info("===> Scan @AggRepository annotation in class path [{}].", getBasePackage());

        Set<Class<?>> classes = AnnotationUtils.findClasses(getBasePackage(), AggRepository.class);
        Set<String> repos = new HashSet<String>();

        for (Class c : classes) {
            try {
                AggRepository ds = (AggRepository) c.getAnnotation(AggRepository.class);

                cloud.addRepository(this.newInstance(ds, c), ds.value());

                repos.add(c.getSimpleName());

            } catch (Exception e) {
                logger.warn("Cannot register Event Handler.", e);
            }
        }
        logger.info("Registered AggregateRepository: [{}]", StringUtils.join(repos.iterator(), ","));

    }

    public AggregateRepository newInstance(AggRepository ds, Class cls) throws Exception {
        return (AggregateRepository) cls.newInstance();
    }

}
