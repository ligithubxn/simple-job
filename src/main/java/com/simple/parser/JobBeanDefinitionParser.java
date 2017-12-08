package com.simple.parser;

import com.simple.scheduler.JobConfig;
import com.simple.scheduler.JobScheduler;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * Created by liws on 2017/11/24.
 */
public class JobBeanDefinitionParser extends AbstractBeanDefinitionParser {

    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JobScheduler.class);
        builder.setInitMethodName("init");

        BeanDefinition bd = BeanDefinitionBuilder.rootBeanDefinition(element.getAttribute("class")).getBeanDefinition();
        builder.addConstructorArgValue(bd);
        builder.addPropertyValue("config", getJobConfig(element));
        return builder.getBeanDefinition();
    }

    private AbstractBeanDefinition getJobConfig(Element element) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(JobConfig.class);
        builder.addPropertyValue("jobName", element.getAttribute("id"));
//        builder.addPropertyValue("jobClass", element.getAttribute("class"));
        builder.addPropertyValue("cron", element.getAttribute("cron"));
        builder.addPropertyReference("center", element.getAttribute("registry-center"));
        return builder.getBeanDefinition();
    }
}
