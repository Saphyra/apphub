package com.github.saphyra.apphub.service.platform.monitoring.controller;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequestWrapper;
import com.github.saphyra.apphub.lib.concurrency.ExecutionResult;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics.PutMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static com.github.saphyra.apphub.lib.config.rabbitmq.RabbitMqConstants.MESSAGE_LISTENER_MONITORING_METRIC;

@Component(MESSAGE_LISTENER_MONITORING_METRIC)
@RequiredArgsConstructor
@Slf4j
class MonitoringMetricMessageListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ExecutorServiceBean executorServiceBean;
    private final PutMetricsService putMetricsService;
    private final ErrorReporterService errorReporterService;

    @Override
    public void onMessage(Message message) {
        PutMetricsRequestWrapper wrapper = objectMapper.readValue(message.getBody(), PutMetricsRequestWrapper.class);
        log.info("Received {} metrics from service {}", wrapper.getRequests().size(), wrapper.getService());

        List<ExecutionResult<Void>> futures = executorServiceBean.consumeCollectionWithWait(wrapper.getRequests(), request -> putMetricsService.putMetrics(wrapper.getService(), request), 4);

        for (ExecutionResult<Void> future : futures) {
            if (!future.isSuccess()) {
                errorReporterService.report("Failed to put metrics", future.getException());
            }
        }
    }
}
