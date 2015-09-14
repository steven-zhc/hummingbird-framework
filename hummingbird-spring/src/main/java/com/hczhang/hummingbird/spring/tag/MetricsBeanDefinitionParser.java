package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.metrics.MetricsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * Created by steven on 12/29/14.
 */
public class MetricsBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static Logger logger = LoggerFactory.getLogger(MetricsBeanDefinitionParser.class);

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        String value = element.getAttribute("enable");

        boolean enable = Boolean.parseBoolean(value);

        MetricsManager.enable = enable;
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected Class getBeanClass(Element element) {
        return Object.class;
    }
}
