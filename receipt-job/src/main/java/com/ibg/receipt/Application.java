package com.ibg.receipt;

import com.ibg.cloud.config.listener.CloudConfigListener;
import com.ibg.receipt.context.ContextContainer;
import com.ibg.receipt.job.service.SysJobService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableRetry
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
//@ComponentScan(basePackages = "com.ibg.receipt")
public class Application extends SpringBootServletInitializer implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private Scheduler scheduler;

    @Override
    public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        application.listeners(new CloudConfigListener());
        return application.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //系统会存在两个容器，一个是root application context ,另一个就是我们自己的 projectName-servlet  context（作为root application context的子容器）
    //该方法会被执行两次，root application context 没有parent
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ContextContainer.setAc(event.getApplicationContext());
        //windows和mac 不启动定时任务
        //本地调试吧下面的注解去掉
        if (!System.getProperty("os.name").toUpperCase().startsWith("W")
                && !System.getProperty("os.name").toUpperCase().startsWith("M")) {
            event.getApplicationContext().getBean(SysJobService.class);
            try {
                scheduler.start();
            } catch (SchedulerException e) {
                throw new RuntimeException("quartz启动失败", e);
            }
        }

    }
}
