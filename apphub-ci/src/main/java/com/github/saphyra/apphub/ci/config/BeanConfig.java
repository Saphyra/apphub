package com.github.saphyra.apphub.ci.config;

import com.github.saphyra.apphub.ci.utils.concurrent.ExecutorServiceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
class BeanConfig {
    @Bean
    ExecutorServiceBean executorServiceBean() {
        return new ExecutorServiceBean(Executors.newCachedThreadPool());
    }
}
