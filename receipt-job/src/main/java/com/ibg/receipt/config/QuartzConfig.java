package com.ibg.receipt.config;

import javax.sql.DataSource;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.ibg.receipt.job.quartz.SpringManagedJobFactory;

import lombok.RequiredArgsConstructor;


@Configuration
public class QuartzConfig {

	@Autowired
	private ApplicationContext context;
	@Autowired
    private Tracer tracer;

	@Bean(name = "scheduler")
	public SchedulerFactoryBean schedulerFactory(DataSource dataSource, PlatformTransactionManager transactionManager) {
		SchedulerFactoryBean bean = new SchedulerFactoryBean();
		// 用于quartz集群,QuartzScheduler 启动时更新己存在的Job
		bean.setOverwriteExistingJobs(true);
		bean.setDataSource(dataSource);
		bean.setConfigLocation(new ClassPathResource("quartz.properties"));
		bean.setWaitForJobsToCompleteOnShutdown(true);
		bean.setAutoStartup(false);
		bean.setTransactionManager(transactionManager);
		SpringManagedJobFactory jobFactory = new SpringManagedJobFactory();
		jobFactory.setApplicationContext(context);
		bean.setJobFactory(jobFactory);
		bean.setGlobalJobListeners(new TracingJobListener(tracer));
		return bean;
	}

	@Bean
	public ServletListenerRegistrationBean<IntrospectorCleanupListener> introspectorCleanupListener() {
		ServletListenerRegistrationBean<IntrospectorCleanupListener> bean = new ServletListenerRegistrationBean<>();
		bean.setListener(new IntrospectorCleanupListener());
		return bean;
	}

}

@RequiredArgsConstructor
class TracingJobListener implements JobListener {

    private final Tracer tracer;
    
    @Override
    public String getName() {
        return "quartzJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        tracer.createSpan(context.getJobDetail().getKey().getName());
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        Span span = tracer.getCurrentSpan();
        tracer.close(span);
        
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        Span span = tracer.getCurrentSpan();
        tracer.close(span);
    }
}
