package com.myway.crawler.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 
 * 
 * @author zhangy
 * @version 2018年10月19日
 */
@Configuration
public class PoolConfiguration {

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("crawler-pool");
        scheduler.setPoolSize(10);
        return scheduler;
    }
}
