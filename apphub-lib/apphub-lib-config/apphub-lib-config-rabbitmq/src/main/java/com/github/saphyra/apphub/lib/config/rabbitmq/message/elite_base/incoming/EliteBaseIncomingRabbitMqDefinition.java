package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.incoming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_INCOMING;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_INCOMING;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_ELITE_BASE_INCOMING;

@Configuration
@Profile("!test")
@Slf4j
class EliteBaseIncomingRabbitMqDefinition {
    @Bean
    TopicExchange eliteBaseIncomingExchange() {
        log.info("Creating exchange {}", EXCHANGE_ELITE_BASE_INCOMING);
        return new TopicExchange(EXCHANGE_ELITE_BASE_INCOMING, true, false);
    }

    @Bean
    Queue eliteBaseIncomingQueue() {
        String queue = QUEUE_ELITE_BASE_INCOMING;
        log.info("Creating queue {}", queue);
        return QueueBuilder.durable(queue)
            .build();
    }

    @Bean
    Binding eliteBaseIncomingBinding(Queue eliteBaseIncomingQueue, TopicExchange eliteBaseIncomingExchange) {
        log.info("Creating binding for queue {} and exchange {}", eliteBaseIncomingQueue.getName(), eliteBaseIncomingExchange.getName());
        return BindingBuilder.bind(eliteBaseIncomingQueue)
            .to(eliteBaseIncomingExchange)
            .with(MESSAGE_ELITE_BASE_INCOMING);
    }
}
