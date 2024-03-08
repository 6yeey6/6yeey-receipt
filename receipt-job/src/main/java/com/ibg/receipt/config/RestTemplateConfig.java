package com.ibg.receipt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {


    @Bean(name = "httpClientFactory")
    public SimpleClientHttpRequestFactory httpClientFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);
        return factory;
    }

    @Bean(name = "restTemplate")
    public RestTemplate restTemplate() {
        RestTemplate template = new RestTemplate(httpClientFactory());
        return template;
    }
}
