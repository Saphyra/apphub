package com.github.saphyra.apphub.lib.concurrency;

import com.github.saphyra.apphub.lib.common_util.SleepService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class DefaultExecutorServiceBeanConfig {
    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }

    @Bean
    ExecutorServiceBean executorServiceBean(ExecutorServiceBeanFactory factory) {
        return factory.create();
    }
}
