package com.ibg.receipt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@SuppressWarnings("all")
public class NacosHealthCheckConfig implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, CommandLineRunner {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (registry.containsBeanDefinition("nacosConfigHealthIndicator")) {
            registry.removeBeanDefinition("nacosConfigHealthIndicator");
            log.info("remove the nacos HealthIndicator success");
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    private ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            Class<?> clazz = Class.forName("com.alibaba.cloud.nacos.endpoint.NacosConfigHealthIndicator");
            Object bean = applicationContext.getBean(clazz);
            if (Objects.isNull(bean)) {
                log.info("verify remove NacosConfigHealthIndicator success");
            }
        } catch (Exception e) {
            log.warn("", e);
        }
    }
}
