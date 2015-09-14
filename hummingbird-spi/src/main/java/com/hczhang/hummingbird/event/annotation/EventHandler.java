package com.hczhang.hummingbird.event.annotation;


import com.hczhang.hummingbird.annotation.Source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steven on 10/3/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EventHandler {
    /**
     * Value handler source.
     *
     * @return the handler source
     */
// TODO: add new parameters, such as event type. added new annotation. onmessage
    Source value() default Source.SPRING;
}
