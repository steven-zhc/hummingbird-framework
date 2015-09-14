package com.hczhang.hummingbird.repository.annotation;



import com.hczhang.hummingbird.annotation.Source;
import com.hczhang.hummingbird.model.AggregateRoot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steven on 1/5/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface AggRepository {
    /**
     * Value class.
     *
     * @return the class
     */
    Class<? extends AggregateRoot> value();


    /**
     * Source source.
     *
     * @return the source
     */
    Source source() default Source.SPRING;
}
