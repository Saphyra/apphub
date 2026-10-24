package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.backfill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_ELITE_BASE_BACKFILL;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.PLACEHOLDER_SERVICE;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_ELITE_BASE_BACKFILL;

@Configuration
@ConditionalOnProperty(prefix = "elite-base", name = "message.backfill.consumer.enabled", havingValue = "true")
@Profile("!test")
@Import(EliteBaseBackfillRabbitMqDefinition.class)
@Slf4j
public class EliteBaseBackfillRabbitMqConsumerConfig {
    @Bean
    public SimpleMessageListenerContainer eliteBaseBackfillListenerContainer(
        ConnectionFactory connectionFactory,
        @Qualifier(MESSAGE_LISTENER_ELITE_BASE_BACKFILL) MessageListener messageListener,
        @Value("${spring.application.name}") String serviceName
    ) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        String queueName = QUEUE_ELITE_BASE_BACKFILL.replace(PLACEHOLDER_SERVICE, serviceName);
        log.info("Creating listener container for queue {}", queueName);

        container.setQueueNames(queueName);
        container.setMessageListener(messageListener);
        container.setConcurrentConsumers(10);

        return container;
    }

    @Bean
    Queue eliteBaseBackfillQueue(@Value("${spring.application.name}") String serviceName) {
        String queue = QUEUE_ELITE_BASE_BACKFILL.replace(PLACEHOLDER_SERVICE, serviceName);
        log.info("Creating queue {}", queue);
        return QueueBuilder.nonDurable(queue)
            .autoDelete()
            .exclusive()
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
