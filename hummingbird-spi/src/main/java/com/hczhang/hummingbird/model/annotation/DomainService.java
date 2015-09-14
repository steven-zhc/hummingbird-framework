package com.hczhang.hummingbird.model.annotation;


import com.hczhang.hummingbird.annotation.Source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steven on 12/15/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DomainService {
    /**
     * Value domain service source.
     *
     * @return the domain service source
     */
    Source value() default Source.SPRING;
}
