package com.hczhang.hummingbird.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by steven on 7/17/15.
 */
public interface ExtensionBinding {
    ExtensionType getExtensionType();

    boolean lookingFor(String type);

    Class<?> getImplementClass();

    void moreConfig(BeanDefinitionBuilder componentFactory, Element element, ParserContext parserContext);
}
