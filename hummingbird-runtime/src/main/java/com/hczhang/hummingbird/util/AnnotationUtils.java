package com.hczhang.hummingbird.util;

import com.hczhang.hummingbird.model.exception.ModelRuntimeException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by steven on 10/6/14.
 */
public class AnnotationUtils {

    private static Logger logger = LoggerFactory.getLogger(AnnotationUtils.class);

    public static Set<Class<?>> findClasses(String basePackage, Class<? extends Annotation> annotationType) {

        HBAssert.notEmpty(basePackage, ModelRuntimeException.class, "basePackage must not be null");
        HBAssert.notNull(annotationType, ModelRuntimeException.class, "annotationType must not be null");

        String[] pkgs = basePackage.split(",");

        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationType));

        Set<Class<?>> result = new HashSet<Class<?>>();
        for (String p : pkgs) {
            for (BeanDefinition bd : scanner.findCandidateComponents(StringUtils.trim(p))) {

                String name =  bd.getBeanClassName();

                try {
                    result.add(Class.forName(name));
                } catch (ClassNotFoundException e) {
                    logger.warn("Cannot get class [{}]", name, e);
                }
            }
        }

        return result;
    }
}
