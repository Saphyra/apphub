package com.github.saphyra.apphub.service.custom.elite_base.config;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.DateTimeConverter;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.geometry.n_dimension.NDimensionDistanceCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EliteBaseBeanConfig {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    DateTimeConverter dateTimeConverter() {
        return new DateTimeConverter();
    }

    @Bean
    ScheduledExecutorServiceBean scheduledExecutorServiceBean(
        ExecutorServiceBeanFactory factory,
        @Value("${elite-base.executor.scheduled.threadCount}") int threadCount
    ) {
        return factory.createScheduled(threadCount);
    }

    @Bean
    ApplicationContextProxy applicationContextProxy(ConfigurableApplicationContext applicationContext) {
        return new ApplicationContextProxy(applicationContext);
    }

    @Bean
    NDimensionDistanceCalculator nDimensionDistanceCalculator() {
        return new NDimensionDistanceCalculator();
    }
}
