package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.CloudRuntimeException;
import com.hczhang.hummingbird.cloud.SimpleEventSourceCloud;
import com.hczhang.hummingbird.model.annotation.DomainService;
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
public class DomainServiceAware extends PackageLifecycleAware {

    private static Logger logger = LoggerFactory.getLogger(DomainServiceAware.class);

    protected SimpleEventSourceCloud cloud;

    public DomainServiceAware(String basePackage) {
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

        logger.info("===> Scan @DomainService annotation in class path [{}].", getBasePackage());

        Set<Class<?>> classes = AnnotationUtils.findClasses(getBasePackage(), DomainService.class);
        Set<String> services = new HashSet<String>();

        for (Class c : classes) {
            try {
                DomainService ds = (DomainService) c.getAnnotation(DomainService.class);

                cloud.addCrystal(this.newInstance(ds, c));

            } catch (Exception e) {
                logger.warn("Cannot register DomainService.", e);
            }
        }
        logger.info("Registered DomainServices: [{}]", StringUtils.join(services.iterator(), ","));

    }

    public Object newInstance(DomainService ds, Class cls) throws Exception {
        return cls.newInstance();
    }
}
