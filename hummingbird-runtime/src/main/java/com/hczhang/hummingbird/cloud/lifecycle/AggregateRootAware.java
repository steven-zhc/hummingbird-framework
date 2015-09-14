package com.hczhang.hummingbird.cloud.lifecycle;

import com.hczhang.hummingbird.cloud.Cloud;
import com.hczhang.hummingbird.cloud.CloudRuntimeException;
import com.hczhang.hummingbird.cloud.SimpleEventSourceCloud;
import com.hczhang.hummingbird.model.annotation.AggregateID;
import com.hczhang.hummingbird.model.annotation.AggregateRoot;
import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import com.hczhang.hummingbird.util.HBAssert;
import com.hczhang.hummingbird.util.AnnotationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by steven on 10/3/14.
 */
public class AggregateRootAware extends PackageLifecycleAware {

    private static Logger logger = LoggerFactory.getLogger(AggregateRootAware.class);

    protected SimpleEventSourceCloud cloud;

    public AggregateRootAware(String basePackage) {
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
        HBAssert.notNull(ctx, ModelRuntimeException.class, "Cloud Countext must not be null.");

        logger.info("===> Scan @AggregateRoot annotation in class path [{}].", getBasePackage());

        Set<Class<?>> classes = AnnotationUtils.findClasses(getBasePackage(), AggregateRoot.class);
        Set<String> rlt = new HashSet<String>();

        for (Class c : classes) {

            cloud.registerDewPrototype(c);

            try {
                Field aid = null;

                // Get Aggregate ID.
                Field[] fields = c.getDeclaredFields();
                for (Field f : fields) {
                    AggregateID ann = f.getAnnotation(AggregateID.class);
                    if (ann != null) {
                        aid = f;
                        break;
                    }
                }

                cloud.registerField(c, aid);

            } catch (Exception e) {
                logger.warn("Cannot register Aggregate ID field.", e);
            }

            rlt.add(c.getSimpleName());
        }
        logger.info("Found Annotated Aggregates: [{}]", StringUtils.join(rlt.iterator(), ","));
    }

}
