package com.simple.namespace;

import com.simple.parser.JobBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Created by liws on 2017/11/23.
 */
public class JobNameSpaceHandler extends NamespaceHandlerSupport {
    public void init() {
        this.registerBeanDefinitionParser("job", new JobBeanDefinitionParser());
    }
}
