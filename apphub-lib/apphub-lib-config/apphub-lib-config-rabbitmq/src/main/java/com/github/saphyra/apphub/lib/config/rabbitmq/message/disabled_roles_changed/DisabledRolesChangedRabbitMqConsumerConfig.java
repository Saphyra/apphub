package com.github.saphyra.apphub.lib.config.rabbitmq.message.disabled_roles_changed;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_DISABLED_ROLES_CHANGED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_DISABLED_ROLES_CHANGED;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.PLACEHOLDER_SERVICE;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_DISABLED_ROLES_CHANGED;

@Configuration
@Import(DisabledRolesChangedRabbitMqDefinition.class)
@Profile("!test")
@Slf4j
public class DisabledRolesChangedRabbitMqConsumerConfig {
    @Bean
    public SimpleMessageListenerContainer disabledRolesChangedListenerContainer(
        ConnectionFactory connectionFactory,
        @Qualifier(MESSAGE_LISTENER_DISABLED_ROLES_CHANGED) MessageListener messageListener,
        @Value("${spring.application.name}") String serviceName
    ) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);

        String queueName = QUEUE_DISABLED_ROLES_CHANGED.replace(PLACEHOLDER_SERVICE, serviceName);
        log.info("Creating listener container for queue {}", queueName);

        container.setQueueNames(queueName);
        container.setMessageListener(messageListener);
        container.setConcurrentConsumers(1);

        return container;
    }

    @Bean
    Queue disabledRolesChangedQueue(@Value("${spring.application.name}") String serviceName) {
        String queue = QUEUE_DISABLED_ROLES_CHANGED.replace(PLACEHOLDER_SERVICE, serviceName);
        log.info("Creating queue {}", queue);
        return QueueBuilder.nonDurable(queue)
            .autoDelete()
            .exclusive()
            .build();
    }

    @Bean
    Binding disabledRolesChangedBinding(Queue disabledRolesChangedQueue, TopicExchange disabledRolesChangedExchange) {
        log.info("Creating binding for queue {} and exchange {}", disabledRolesChangedQueue.getName(), disabledRolesChangedExchange.getName());
        return BindingBuilder.bind(disabledRolesChangedQueue)
            .to(disabledRolesChangedExchange)
            .with(MESSAGE_DISABLED_ROLES_CHANGED);
    }
}
