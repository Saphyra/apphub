package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.backfill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_ELITE_BASE_BACKFILL;

@Configuration
@Profile("!test")
@Slf4j
class EliteBaseBackfillRabbitMqDefinition {
    @Bean
    TopicExchange eliteBaseBackfillExchange() {
        log.info("Creating exchange {}", EXCHANGE_ELITE_BASE_BACKFILL);
        return new TopicExchange(EXCHANGE_ELITE_BASE_BACKFILL, true, false);
    }

    @Bean
    Queue eliteBaseBackfillQueue() {
        String queue = QUEUE_ELITE_BASE_BACKFILL;
        log.info("Creating queue {}", queue);
        return QueueBuilder.durable(queue)
            .build();
    }

    @Bean
    Binding eliteBaseBackfillBinding(Queue eliteBaseBackfillQueue, TopicExchange eliteBaseBackfillExchange) {
        log.info("Creating binding for queue {} and exchange {}", eliteBaseBackfillQueue.getName(), eliteBaseBackfillExchange.getName());
        return BindingBuilder.bind(eliteBaseBackfillQueue)
            .to(eliteBaseBackfillExchange)
            .with(MESSAGE_ELITE_BASE_BACKFILL);
    }
}
