package com.github.saphyra.apphub.service.etc.admin_panel.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminPanelBeanConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }

    @Bean
    ScheduledExecutorServiceBean scheduledExecutorServiceBean(ExecutorServiceBeanFactory executorServiceBeanFactory) {
        return executorServiceBeanFactory.createScheduled(1);
    }
}
