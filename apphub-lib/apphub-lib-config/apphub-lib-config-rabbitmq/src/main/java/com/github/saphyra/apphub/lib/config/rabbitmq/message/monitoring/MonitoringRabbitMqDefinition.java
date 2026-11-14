package com.github.saphyra.apphub.lib.config.rabbitmq.message.monitoring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_MONITORING_METRIC;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_MONITORING_METRIC;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.QUEUE_MONITORING_METRIC;

@Configuration
@Profile("!test")
@Slf4j
class MonitoringRabbitMqDefinition {
    @Bean
    TopicExchange monitoringMetricExchange() {
        log.info("Creating exchange {}", EXCHANGE_MONITORING_METRIC);
        return new TopicExchange(EXCHANGE_MONITORING_METRIC, true, false);
    }

    @Bean
    Queue monitoringMetricQueue() {
        String queue = QUEUE_MONITORING_METRIC;
        log.info("Creating queue {}", queue);
        return QueueBuilder.durable(queue)
            .build();
    }

    @Bean
    Binding monitoringMetricBinding(Queue monitoringMetricQueue, TopicExchange monitoringMetricExchange) {
        log.info("Creating binding for queue {} and exchange {}", monitoringMetricQueue.getName(), monitoringMetricExchange.getName());
        return BindingBuilder.bind(monitoringMetricQueue)
            .to(monitoringMetricExchange)
            .with(MESSAGE_MONITORING_METRIC);
    }
}
