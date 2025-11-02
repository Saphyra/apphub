package com.github.saphyra.apphub.service.platform.scheduler;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.concurrency.DefaultExecutorServiceBeanConfig;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.config.health.EnableHealthCheck;
import com.github.saphyra.apphub.lib.monitoring.EnableMemoryMonitoring;
import com.github.saphyra.apphub.lib.request_validation.locale.EnableLocaleMandatoryRequestValidation;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@EnableHealthCheck
@EnableLocaleMandatoryRequestValidation
@EnableScheduling
@EnableMemoryMonitoring
@Import(DefaultExecutorServiceBeanConfig.class)
public class SchedulerBeanConfiguration {
    @Bean
    ObjectMapperWrapper objectMapperWrapper() {
        return new ObjectMapperWrapper(new ObjectMapper());
    }

    @Bean
    ScheduledExecutorServiceBean scheduledExecutorServiceBean(ExecutorServiceBeanFactory executorServiceBeanFactory) {
        return executorServiceBeanFactory.createScheduled(1);
    }
}
