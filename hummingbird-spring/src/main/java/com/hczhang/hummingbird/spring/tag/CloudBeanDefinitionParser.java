package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.cloud.lifecycle.PackageLifecycleAware;
import com.hczhang.hummingbird.fog.TwoLevelFogManager;
import com.hczhang.hummingbird.spring.cloud.SpringEventSourceCloud;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by steven on 12/29/14.
 */
public class CloudBeanDefinitionParser extends AbstractBeanDefinitionParser {

    private static Logger logger = LoggerFactory.getLogger(CloudBeanDefinitionParser.class);

    private String packageName;

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

        return parseCloudElement(element, parserContext);
    }

    private AbstractBeanDefinition parseCloudElement(Element element, ParserContext ctx) {

        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringEventSourceCloud.class);
        factory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        factory.setLazyInit(false);
        factory.setInitMethodName("launch");

        packageName = element.getAttribute("package");
        factory.addPropertyValue("basePackage", packageName);

        // event router
        String value = element.getAttribute("eventrouter");
        if (StringUtils.isNotBlank(value)) {
            factory.addPropertyReference("eventRouter", value);
        }

        // event store
        value = element.getAttribute("eventstore");
        if (StringUtils.isNotBlank(value)) {
            factory.addPropertyReference("eventSourceRepository", value);
        }

        // parse fogs. it has default value. it could be null.
        Element fogsElement = DomUtils.getChildElementByTagName(element, "fogs");
        parseFogsElement(fogsElement, factory, ctx);

        // parse logs
        Element logsElement = DomUtils.getChildElementByTagName(element, "logs");
        parseLogsElement(logsElement, factory, ctx);

        // parse lifecyle
        Element lifecycleElement = DomUtils.getChildElementByTagName(element, "lifecycle");
        parseLifecycleElement(lifecycleElement, factory);

        return factory.getBeanDefinition();
    }

    private void parseFogsElement(Element fogsElement, BeanDefinitionBuilder factory, ParserContext ctx) {
        if (fogsElement == null) {
            return;
        }
        Element e1 = DomUtils.getChildElementByTagName(fogsElement, "l1");
        Element e2 = DomUtils.getChildElementByTagName(fogsElement, "l2");

        BeanDefinitionBuilder manager = BeanDefinitionBuilder.rootBeanDefinition(TwoLevelFogManager.class);

        if (e2 != null) {
            manager.addPropertyReference("level2", e2.getAttribute("ref"));
        }

        if (e1 != null) {
            manager.addPropertyReference("level1", e1.getAttribute("ref"));

            factory.addPropertyValue("fogManager", manager.getBeanDefinition());
        }
    }

    private void parseLogsElement(Element logsElement, BeanDefinitionBuilder factory, ParserContext ctx) {
        if (logsElement == null) {
            return;
        }

        List<Element> childElements = DomUtils.getChildElementsByTagName(logsElement, "log");

        ManagedSet<BeanDefinition> logs = new ManagedSet<BeanDefinition>(childElements.size());

        for (Element element : childElements) {

            BeanDefinition bd = ctx.getRegistry().getBeanDefinition(element.getAttribute("ref"));

            if (bd != null) {
                logs.add(bd);
            }
        }

        if (logs.size() != 0) {
            factory.addPropertyValue("eventLogs", logs);
        }
    }

    private void parseLifecycleElement(Element lifecycleElement, BeanDefinitionBuilder factory) {
        if (lifecycleElement == null) {
            return;
        }

        List<Element> childElements = DomUtils.getChildElementsByTagName(lifecycleElement, "item");

        ManagedSet<BeanDefinition> items = new ManagedSet<BeanDefinition>(childElements.size());

        for (Element element : childElements) {

            BeanDefinition bd = parseItemElement(element);

            if (bd != null) {
                items.add(bd);
            }

        }

        factory.addPropertyValue("lifecycleListeners", items);
    }

    private BeanDefinition parseItemElement(Element element) {

        String classType = element.getAttribute("class");

        BeanDefinitionBuilder factory = null;
        try {
            Class listenerType = Class.forName(classType);

            factory = BeanDefinitionBuilder.rootBeanDefinition(listenerType);

            if (PackageLifecycleAware.class.isAssignableFrom(listenerType)) {
                factory.addPropertyValue("basePackage", packageName);
            }
            factory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
            factory.setLazyInit(false);

            return factory.getBeanDefinition();

        } catch (ClassNotFoundException e) {
            logger.error("Cannot load class.", e);
        }

        return null;
    }

}

