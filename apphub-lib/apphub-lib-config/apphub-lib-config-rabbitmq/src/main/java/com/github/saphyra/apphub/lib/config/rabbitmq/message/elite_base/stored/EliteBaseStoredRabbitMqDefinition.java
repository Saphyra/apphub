package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.stored;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_STORED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_STORED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_ELITE_BASE_STORED;

@Configuration
@Profile("!test")
@Slf4j
class EliteBaseStoredRabbitMqDefinition {
    @Bean
    TopicExchange eliteBaseStoredExchange() {
        log.info("Creating exchange {}", EXCHANGE_ELITE_BASE_STORED);
        return new TopicExchange(EXCHANGE_ELITE_BASE_STORED, true, false);
    }

    @Bean
    Queue eliteBaseStoredQueue() {
        String queue = QUEUE_ELITE_BASE_STORED;
        log.info("Creating queue {}", queue);
        return QueueBuilder.durable(queue)
            .build();
    }

    @Bean
    Binding eliteBaseStoredBinding(Queue eliteBaseStoredQueue, TopicExchange eliteBaseStoredExchange) {
        log.info("Creating binding for queue {} and exchange {}", eliteBaseStoredQueue.getName(), eliteBaseStoredExchange.getName());
        return BindingBuilder.bind(eliteBaseStoredQueue)
            .to(eliteBaseStoredExchange)
            .with(MESSAGE_ELITE_BASE_STORED);
    }
}
