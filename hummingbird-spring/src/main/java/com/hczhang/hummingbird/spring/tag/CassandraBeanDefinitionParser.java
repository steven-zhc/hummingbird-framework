package com.hczhang.hummingbird.spring.tag;

import com.hczhang.ostrich.CassandraSessionFactory;
import com.hczhang.ostrich.CassandraTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by steven on 12/29/14.
 */
public class CassandraBeanDefinitionParser extends AbstractBeanDefinitionParser {

    public static final String HIMMINGBIRD_CASSANDRA = "HIMMINGBIRD-CASSANDRA";

    private static Logger logger = LoggerFactory.getLogger(CassandraBeanDefinitionParser.class);

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

        // Define session factory class definition
        BeanDefinitionBuilder sessionFactory = BeanDefinitionBuilder.rootBeanDefinition(CassandraSessionFactory.class);
        sessionFactory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        sessionFactory.setLazyInit(false);

        sessionFactory.addConstructorArgValue(element.getAttribute("points"));
        sessionFactory.addConstructorArgValue(element.getAttribute("keyspace"));
        sessionFactory.addConstructorArgValue(element.getAttribute("username"));
        sessionFactory.addConstructorArgValue(element.getAttribute("password"));

        // Define template class definition
        BeanDefinitionBuilder templateFactory = BeanDefinitionBuilder.rootBeanDefinition(CassandraTemplate.class);
        templateFactory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        templateFactory.setLazyInit(false);

        templateFactory.addConstructorArgValue(sessionFactory.getBeanDefinition());

        return templateFactory.getBeanDefinition();
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        return HIMMINGBIRD_CASSANDRA;
    }
}
