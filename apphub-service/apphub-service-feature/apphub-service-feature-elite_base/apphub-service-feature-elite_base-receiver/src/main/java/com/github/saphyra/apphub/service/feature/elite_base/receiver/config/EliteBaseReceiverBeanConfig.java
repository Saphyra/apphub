package com.github.saphyra.apphub.service.feature.elite_base.receiver.config;

import com.github.saphyra.apphub.lib.common_util.ApplicationContextProxy;
import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ScheduledExecutorServiceBean;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.incoming.EliteBaseIncomingRabbitMqPublisherConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EliteBaseIncomingRabbitMqPublisherConfig.class)
class EliteBaseReceiverBeanConfig {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }

    @Bean
    ApplicationContextProxy applicationContextProxy(ConfigurableApplicationContext applicationContext) {
        return new ApplicationContextProxy(applicationContext);
    }

    @Bean
    ScheduledExecutorServiceBean scheduledExecutorServiceBean(
        ExecutorServiceBeanFactory factory,
        @Value("${elite-base.executor.scheduled.threadCount}") int threadCount
    ) {
        return factory.createScheduled(threadCount);
    }
}
