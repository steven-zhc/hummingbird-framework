package com.hczhang.hummingbird.spring.tag;

import org.springframework.beans.factory.config.BeanDefinition;
import org.w3c.dom.Element;

/**
 * Created by steven on 8/11/15.
 */
public interface FilterManagerTagParser {

    BeanDefinition parseFilterManagerElement(Element filterElement);

    BeanDefinition parseItemElement(Element item);

}
