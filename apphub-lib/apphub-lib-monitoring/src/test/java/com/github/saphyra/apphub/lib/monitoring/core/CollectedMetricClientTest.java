package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequestWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.EXCHANGE_MONITORING_METRIC;
import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_MONITORING_METRIC;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class CollectedMetricClientTest {
    private static final String SERVICE = "service";

    @Mock
    private RabbitTemplate rabbitTemplate;

    private CollectedMetricClient underTest;

    @Mock
    private PutMetricsRequest putMetricsRequest;

    @BeforeEach
    void setUp() {
        underTest = new CollectedMetricClient(SERVICE, rabbitTemplate);
    }

    @Test
    public void sendMetrics() {
        underTest.send(List.of(putMetricsRequest));

        then(rabbitTemplate).should().convertAndSend(EXCHANGE_MONITORING_METRIC, MESSAGE_MONITORING_METRIC, new PutMetricsRequestWrapper(SERVICE, List.of(putMetricsRequest)));
    }
}