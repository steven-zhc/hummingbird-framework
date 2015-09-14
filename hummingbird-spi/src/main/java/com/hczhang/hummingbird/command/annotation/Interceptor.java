package com.hczhang.hummingbird.command.annotation;


import com.hczhang.hummingbird.annotation.Source;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by steven on 12/29/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Interceptor {
    /**
     * Value class.
     *
     * @return the class
     */
    Class value();

    /**
     * Priority int.
     *
     * @return the int
     */
    int priority() default 0;

    /**
     * Source interceptor source.
     *
     * @return the interceptor source
     */
    Source source() default Source.SPRING;
}
