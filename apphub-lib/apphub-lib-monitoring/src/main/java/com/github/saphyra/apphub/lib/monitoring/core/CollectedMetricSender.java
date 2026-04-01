package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class CollectedMetricSender {
    private final CollectedMetricClient collectedMetricClient;
    private final MetricRegistry metricRegistry;
    private final ExecutorServiceBean executorServiceBean;

    public void sendCollectedMetrics() {
        Collection<List<PutMetricsRequest>> metrics = metricRegistry.getMetricsToSend();

        executorServiceBean.processCollectionWithWait(
            metrics,
            requests -> {
                collectedMetricClient.send(requests);
                return null;
            }
        );
    }
}
