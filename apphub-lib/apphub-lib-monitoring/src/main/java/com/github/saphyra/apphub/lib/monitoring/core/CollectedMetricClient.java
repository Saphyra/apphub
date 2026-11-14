package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_MONITORING_METRIC;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_MONITORING_METRIC;

@Component
@Slf4j
class CollectedMetricClient {
    private final String service;
    private final RabbitTemplate rabbitTemplate;

    public CollectedMetricClient(@Value("${spring.application.name}") String service, RabbitTemplate rabbitTemplate) {
        this.service = service;
        this.rabbitTemplate = rabbitTemplate;
    }

    void send(List<PutMetricsRequest> requests) {
        PutMetricsRequestWrapper wrapper = new PutMetricsRequestWrapper(service, requests);
        log.debug("Sending {} metrics", requests.size());

        rabbitTemplate.convertAndSend(EXCHANGE_MONITORING_METRIC, MESSAGE_MONITORING_METRIC, wrapper);

        log.debug("Sent {} metrics", requests.size());
    }
}
