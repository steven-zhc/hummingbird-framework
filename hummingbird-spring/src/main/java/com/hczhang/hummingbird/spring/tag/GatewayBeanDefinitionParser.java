package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.gateway.AnnotatedGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by steven on 12/29/14.
 */
public class GatewayBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static Logger logger = LoggerFactory.getLogger(GatewayBeanDefinitionParser.class);

    @Override
    protected Class getBeanClass(Element element) {
        return AnnotatedGateway.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("basePackage", element.getAttribute("package"));
        builder.addPropertyReference("cloud", element.getAttribute("cloud"));

        builder.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        builder.setLazyInit(false);

        builder.setInitMethodName("initial");
    }
}
