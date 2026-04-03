package com.github.saphyra.apphub.service.platform.monitoring.config;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MonitoringBeanConfig {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
