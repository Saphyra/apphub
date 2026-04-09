package com.github.saphyra.apphub.lib.monitoring.instrument;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.lib.monitoring.core.MetricRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Slf4j
@Component
public class MonitoringInstruments {
    private final MetricMapper metricMapper;
    private final MetricRegistry metricRegistry;

    public <T> T wrap(Supplier<T> task, Feature feature, String functionality) {
        StopWatch stopWatch = StopWatch.createStarted();
        boolean success = true;
        T result = null;
        try {
            result = task.get();

            return result;
        } catch (Exception e) {
            success = false;
            throw e;
        } finally {
            stopWatch.stop();
            List<MetricPropertyModel> properties = metricMapper.map(feature, functionality, result, success, stopWatch.getTime(TimeUnit.MILLISECONDS));
            metricRegistry.reportMetric(feature, functionality, properties);
        }
    }

    public void wrap(Runnable task, Feature feature, String functionality) {
        wrap(
            () -> {
                task.run();
                return null;
            },
            feature,
            functionality
        );
    }
}
