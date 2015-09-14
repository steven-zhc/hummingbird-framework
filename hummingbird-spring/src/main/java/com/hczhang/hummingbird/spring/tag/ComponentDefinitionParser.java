package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.filter.SimpleFilterManager;
import com.hczhang.hummingbird.spring.ExtensionBinding;
import com.hczhang.hummingbird.spring.ExtensionManager;
import com.hczhang.hummingbird.spring.ExtensionType;
import com.hczhang.hummingbird.spring.SpringExtensionError;
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
 * Created by steven on 2/9/15.
 */
public class ComponentDefinitionParser extends AbstractBeanDefinitionParser implements FilterManagerTagParser, ExtensionTypeParser {

    private static Logger logger = LoggerFactory.getLogger(ComponentDefinitionParser.class);

    private ExtensionManager mgr = ExtensionManager.sharedInstance();

    private String componentName;
    private ExtensionType extensionType;

    public ComponentDefinitionParser(String componentName, ExtensionType extensionType) {
        this.componentName = componentName;
        this.extensionType = extensionType;
    }

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

        String type = element.getAttribute("type");

        ExtensionBinding binding = findBinding(type);

        BeanDefinitionBuilder componentFactory = getExtensionBuilder(binding);
        if (componentFactory == null) {
            return null;
        }

        componentFactory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        componentFactory.setLazyInit(false);

        binding.moreConfig(componentFactory, element, parserContext);


        Element filterElement = DomUtils.getChildElementByTagName(element, "filter");
        if (filterElement != null) {
            BeanDefinition bd = parseFilterManagerElement(filterElement);
            componentFactory.addPropertyValue("filterManager", bd);
        }

        return componentFactory.getBeanDefinition();
    }



    @Override
    public BeanDefinition parseFilterManagerElement(Element filterElement) {

        BeanDefinitionBuilder filterManagerFactory = BeanDefinitionBuilder.rootBeanDefinition(SimpleFilterManager.class);
        filterManagerFactory.setAutowireMode(RootBeanDefinition.AUTOWIRE_BY_TYPE);
        filterManagerFactory.setLazyInit(false);

        filterManagerFactory.addPropertyValue("type", getComponentName());

        List<Element> itemElements = DomUtils.getChildElementsByTagName(filterElement, "item");
        ManagedSet<BeanDefinition> items = new ManagedSet<BeanDefinition>(itemElements.size());

        for (Element element : itemElements) {
            BeanDefinition bd = parseItemElement(element);

            if (bd != null) {
                items.add(bd);
            }
        }
        filterManagerFactory.addPropertyValue("filters", items);

        return filterManagerFactory.getBeanDefinition();
    }

    @Override
    public BeanDefinition parseItemElement(Element item) {

        String classType = item.getAttribute("class");

        BeanDefinitionBuilder factory = null;
        try {
            Class filterType = Class.forName(classType);

            factory = BeanDefinitionBuilder.rootBeanDefinition(filterType);

            return factory.getBeanDefinition();

        } catch (ClassNotFoundException e) {
            logger.error("Cannot load class.", e);
        }

        return null;
    }

    public ExtensionBinding findBinding(String type) {
        for (ExtensionBinding eb : mgr.getBindings()) {
            if (eb.getExtensionType() == this.extensionType && eb.lookingFor(type)) {
                return eb;
            }
        }

        throw new SpringExtensionError("Cannot find extension binding for type [{}]", type);
    }

    @Override
    public BeanDefinitionBuilder getExtensionBuilder(ExtensionBinding eb) {

        Class extensionClass = eb.getImplementClass();
        if (extensionClass == null) {
            throw new SpringExtensionError("Extension class<{}> has null value.", eb.getClass().getSimpleName());
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(extensionClass);

        return builder;
    }

    public String getComponentName() {
        return componentName;
    }
}
