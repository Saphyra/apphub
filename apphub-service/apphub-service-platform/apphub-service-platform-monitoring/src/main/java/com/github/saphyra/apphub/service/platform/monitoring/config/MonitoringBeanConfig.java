package com.github.saphyra.apphub.service.platform.monitoring.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.monitoring.MonitoringRabbitMqConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(MonitoringRabbitMqConsumerConfig.class)
class MonitoringBeanConfig {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
