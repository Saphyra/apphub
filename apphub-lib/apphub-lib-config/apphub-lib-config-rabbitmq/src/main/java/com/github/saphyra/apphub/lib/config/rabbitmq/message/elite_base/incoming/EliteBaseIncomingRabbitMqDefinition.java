package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.incoming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_INCOMING;

@Configuration
@Profile("!test")
@Slf4j
class EliteBaseIncomingRabbitMqDefinition {
    @Bean
    TopicExchange eliteBaseIncomingExchange() {
        log.info("Creating exchange {}", EXCHANGE_ELITE_BASE_INCOMING);
        return new TopicExchange(EXCHANGE_ELITE_BASE_INCOMING, false, false);
    }
}
