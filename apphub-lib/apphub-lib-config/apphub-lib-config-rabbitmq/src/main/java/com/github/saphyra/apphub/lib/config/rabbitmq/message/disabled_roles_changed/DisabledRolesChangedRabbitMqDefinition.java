package com.github.saphyra.apphub.lib.config.rabbitmq.message.disabled_roles_changed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_DISABLED_ROLES_CHANGED;

@Configuration
@Profile("!test")
@Slf4j
class DisabledRolesChangedRabbitMqDefinition {
    @Bean
    TopicExchange disabledRolesChangedExchange() {
        log.info("Creating exchange {}", EXCHANGE_DISABLED_ROLES_CHANGED);
        return new TopicExchange(EXCHANGE_DISABLED_ROLES_CHANGED, false, false);
    }
}
