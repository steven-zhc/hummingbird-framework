package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.spring.ExtensionManager;
import com.hczhang.hummingbird.spring.ExtensionType;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by steven on 12/29/14.
 */
public class CoreNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {

        ExtensionManager.sharedInstance();

        registerBeanDefinitionParser("cloud", new CloudBeanDefinitionParser());
        registerBeanDefinitionParser("gateway", new GatewayBeanDefinitionParser());
        registerBeanDefinitionParser("eventlog", new ComponentDefinitionParser("EventLog", ExtensionType.EVENT_LOG));
        registerBeanDefinitionParser("eventstore", new ComponentDefinitionParser("EventStore", ExtensionType.EVENT_STORE));
        registerBeanDefinitionParser("eventrouter", new ComponentDefinitionParser("EventRouter", ExtensionType.EVENT_ROUTER));
        registerBeanDefinitionParser("fog", new ComponentDefinitionParser("Fog", ExtensionType.FOG));
        registerBeanDefinitionParser("cassandra", new CassandraBeanDefinitionParser());
        registerBeanDefinitionParser("metrics", new MetricsBeanDefinitionParser());

        // delete temporally cause cannot parse el expres ${}
        // registerBeanDefinitionParser("redis", new RedisFactoryBeanDefinitionParser());
    }


}
