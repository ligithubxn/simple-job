package com.simple.parser;

import com.simple.zk.ZKConfig;
import com.simple.zk.ZKRegisterCenter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by liws on 2017/11/23.
 */
public class ZKBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ZKRegisterCenter.class);
        builder.addConstructorArgValue(parse(element));
        builder.setInitMethodName("init");
        return builder.getBeanDefinition();
    }

    private AbstractBeanDefinition parse(Element element) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ZKConfig.class);
        builder.addPropertyValue("serverList", element.getAttribute("server-lists"));
        builder.addPropertyValue("namespace", element.getAttribute("namespace"));
        return builder.getBeanDefinition();
    }
}
