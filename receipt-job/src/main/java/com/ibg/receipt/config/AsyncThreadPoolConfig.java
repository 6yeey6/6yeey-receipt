package com.ibg.receipt.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author yushijun
 * @date 2019/6/17
 * @description 定义@Async 注解使用的线程池
 * @description1  @Async使用注意事项：
 * 1.it must be applied to public methods only 注解方法必须 public
 * 2.self-invocation – calling the async method from within the same class – won’t work 类中的内部调用不生效,和@Transaction 内部不生效原理一致
 * 3. @Async 使用线程池bean名称就会使用该线程池执行，否则使用默认线程池
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncThreadPoolConfig {
    @Bean(name = "asyncMongoThreadPool")
    public Executor threadPoolTaskExecutor() {
        log.info("===========asyncMongoThreadPool-InitialAsync============");
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();
        // 当前线程数
        threadPool.setCorePoolSize(3);
        // 最大线程数
        threadPool.setMaxPoolSize(6);
        // 线程池所使用的缓冲队列
        threadPool.setQueueCapacity(1000);
        // 当线程空闲时间达到keepAliveTime时，线程会退出，直到线程数量=corePoolSize
        threadPool.setKeepAliveSeconds(300);
        // 设置策略-超出队列使用主线程
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置核心线程超时回收策略
        threadPool.setAllowCoreThreadTimeOut(true);
        // 线程名称前缀
        threadPool.setThreadNamePrefix("asyncMongoThreadPool");
        // 初始化
        threadPool.initialize();
        log.info("===========asyncMongoThreadPool-InitialAsyncThreadPool End============");
        return threadPool;
    }


}
