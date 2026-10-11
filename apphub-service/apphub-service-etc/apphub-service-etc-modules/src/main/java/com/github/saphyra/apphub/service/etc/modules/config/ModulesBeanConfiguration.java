package com.github.saphyra.apphub.service.etc.modules.config;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.config.rabbitmq.message.disabled_roles_changed.DisabledRolesChangedRabbitMqConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DisabledRolesChangedRabbitMqConsumerConfig.class)
class ModulesBeanConfiguration {
    @Bean
    UuidConverter uuidConverter() {
        return new UuidConverter();
    }
}
