package com.ibg.receipt;

import com.ibg.cloud.config.listener.CloudConfigListener;
import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.shiro.CorsFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.servlet.FilterRegistration;

@Slf4j
@EnableDiscoveryClient
@EnableJpaAuditing
@SpringBootApplication
//@ComponentScan(basePackages = { "com.ibg.receipt"})
public class Application extends SpringBootServletInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners(new CloudConfigListener());
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        log.info("receipt API启动成功");
    }

    /*@Bean
    public FilterRegistrationBean getFilterRegistrationBean(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new CorsFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }*/

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ContextContainer.setAc(event.getApplicationContext());
    }


}
