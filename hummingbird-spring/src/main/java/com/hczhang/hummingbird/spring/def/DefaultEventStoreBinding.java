package com.hczhang.hummingbird.spring.def;

import com.hczhang.hummingbird.repository.CassandraEventSourceRepository;
import com.hczhang.hummingbird.spring.ExtensionBinding;
import com.hczhang.hummingbird.spring.ExtensionType;
import com.hczhang.hummingbird.spring.tag.CassandraBeanDefinitionParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by steven on 8/12/15.
 */
public class DefaultEventStoreBinding implements ExtensionBinding {
    @Override
    public ExtensionType getExtensionType() {
        return ExtensionType.EVENT_STORE;
    }

    @Override
    public boolean lookingFor(String type) {
        if (StringUtils.equals("cassandra", type)) {
            return true;
        }
        return false;
    }

    @Override
    public Class<?> getImplementClass() {
        return CassandraEventSourceRepository.class;
    }

    @Override
    public void moreConfig(BeanDefinitionBuilder componentFactory, Element element, ParserContext parserContext) {

        componentFactory.addPropertyReference("template", CassandraBeanDefinitionParser.HIMMINGBIRD_CASSANDRA);
        componentFactory.addPropertyValue("hybird", Boolean.valueOf(element.getAttribute("hybird")));
    }
}
