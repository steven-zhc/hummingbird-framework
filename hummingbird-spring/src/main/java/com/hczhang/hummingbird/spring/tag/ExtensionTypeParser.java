package com.hczhang.hummingbird.spring.tag;

import com.hczhang.hummingbird.spring.ExtensionBinding;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Created by steven on 8/11/15.
 */
public interface ExtensionTypeParser {
    BeanDefinitionBuilder getExtensionBuilder(ExtensionBinding type);
}
