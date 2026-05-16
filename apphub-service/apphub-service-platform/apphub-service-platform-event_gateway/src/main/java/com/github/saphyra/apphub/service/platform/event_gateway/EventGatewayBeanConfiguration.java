package com.github.saphyra.apphub.service.platform.event_gateway;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.SleepService;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.event.processor.EventProcessorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableJpaRepositories
@EntityScan
@ComponentScan(basePackages = "com.github.saphyra.util", basePackageClasses = ExecutorServiceBeanFactory.class)
@EnableAutoConfiguration(exclude = EventProcessorAutoConfiguration.class)
class EventGatewayBeanConfiguration {
    @Bean
    @ConditionalOnMissingBean(ExecutorServiceBean.class)
    ExecutorServiceBean executorServiceBean(ExecutorServiceBeanFactory executorServiceBeanFactory) {
        return executorServiceBeanFactory.create();
    }

    @Bean
    @ConditionalOnMissingBean(SleepService.class)
    SleepService sleepService() {
        return new SleepService();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
