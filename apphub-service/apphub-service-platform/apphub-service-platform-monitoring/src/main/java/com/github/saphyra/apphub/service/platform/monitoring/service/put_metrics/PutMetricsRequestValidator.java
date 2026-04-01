package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricPropertyModel;
import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PutMetricsRequestValidator {
    public void validate(List<PutMetricsRequest> request) {
        request.forEach(this::validate);
    }

    public void validate(PutMetricsRequest request) {
        ValidationUtil.notNull(request.getFeature(), "feature");
        ValidationUtil.notBlank(request.getFunctionality(), "functionality");
        ValidationUtil.notEmpty(request.getProperties(), "properties");
        request.getProperties()
            .forEach(this::validate);
    }

    private void validate(MetricPropertyModel propertyModel) {
        ValidationUtil.notBlank(propertyModel.getKey(), "property.key");
        ValidationUtil.notNull(propertyModel.getValue(), "property.value");
        ValidationUtil.notNull(propertyModel.getAggregationStrategy(), "property.aggregationStrategy");
    }
}
