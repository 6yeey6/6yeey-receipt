package com.ibg.receipt.api.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

@Configuration
public class LogConfig {
    @Bean
    public FilterRegistrationBean defaultMDCInsertingServletFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MDCInsertingServletFilter());
        registration.addUrlPatterns("/*");
        registration.setName("defaultMDCInsertingServletFilter");
        return registration;
    }
}
