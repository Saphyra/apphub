package com.github.saphyra.apphub.lib.monitoring.core;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class MetricRegistry {
    private final Map<LocalDateTime, List<PutMetricsRequest>> registry = new ConcurrentHashMap<>();
    private final DateTimeUtil dateTimeUtil;

    public void reportMetric(Feature feature, String functionality, List<MetricPropertyModel> properties) {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        PutMetricsRequest request = PutMetricsRequest.builder()
            .feature(feature)
            .functionality(functionality)
            .timestamp(timestamp)
            .properties(properties)
            .build();

        List<PutMetricsRequest> bucket = registry.computeIfAbsent(timestamp, _ -> new Vector<>());
        bucket.add(request);
    }

    public Collection<List<PutMetricsRequest>> getMetricsToSend() {
        LocalDateTime timestamp = dateTimeUtil.getCurrentDateTime()
            .withNano(0);

        Map<LocalDateTime, List<PutMetricsRequest>> result = registry.entrySet()
            .stream()
            .filter(entry -> entry.getKey().isBefore(timestamp)) //Send metrics created before the current second
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        result.forEach((t, _) -> registry.remove(t));

        return result.values();
    }
}
