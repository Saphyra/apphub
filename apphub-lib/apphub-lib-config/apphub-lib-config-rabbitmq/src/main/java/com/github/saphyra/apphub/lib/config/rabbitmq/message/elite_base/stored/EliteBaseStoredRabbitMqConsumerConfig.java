package com.github.saphyra.apphub.lib.config.rabbitmq.message.elite_base.stored;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_ELITE_BASE_STORED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_ELITE_BASE_STORED;

@Configuration
@ConditionalOnProperty(prefix = "elite-base", name = "message.stored.consumer.enabled", havingValue = "true")
@Profile("!test")
@Import(EliteBaseStoredRabbitMqDefinition.class)
@Slf4j
public class EliteBaseStoredRabbitMqConsumerConfig {
    @Bean
    public SimpleMessageListenerContainer eliteBaseStoredListenerContainer(
        ConnectionFactory connectionFactory,
        @Qualifier(MESSAGE_LISTENER_ELITE_BASE_STORED) MessageListener messageListener
    ) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        String queueName = QUEUE_ELITE_BASE_STORED;
        log.info("Creating listener container for queue {}", queueName);

        container.setQueueNames(queueName);
        container.setMessageListener(messageListener);
        container.setConcurrentConsumers(10);

        return container;
    }
}
