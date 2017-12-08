package com.simple.namespace;

import com.simple.parser.ZKBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by liws on 2017/11/23.
 */
public class RegNameSpaceHandler extends NamespaceHandlerSupport {

    public void init() {
        this.registerBeanDefinitionParser("zk", new ZKBeanDefinitionParser());
    }
}
