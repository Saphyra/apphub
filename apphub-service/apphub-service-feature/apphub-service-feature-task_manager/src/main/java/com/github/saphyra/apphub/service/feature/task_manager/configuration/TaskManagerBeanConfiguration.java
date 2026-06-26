package com.github.saphyra.apphub.service.feature.task_manager.configuration;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class TaskManagerBeanConfiguration {
    @Bean
    IdGenerator idGenerator() {
        return new IdGenerator();
    }
}
