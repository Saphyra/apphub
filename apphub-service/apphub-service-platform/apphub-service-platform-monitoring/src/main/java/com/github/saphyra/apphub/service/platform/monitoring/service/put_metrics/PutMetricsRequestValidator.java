package com.github.saphyra.apphub.service.platform.monitoring.service.put_metrics;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;
import com.github.saphyra.apphub.lib.common_util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class PutMetricsRequestValidator {
    public void validate(List<PutMetricsRequest> request) {
        request.forEach(this::validate);
    }

    public void validate(PutMetricsRequest request) {
        ValidationUtil.notBlank(request.getService(), "service");
        ValidationUtil.notNull(request.getFeature(), "feature");
        ValidationUtil.notNull(request.getFunctionality(), "functionality");
        ValidationUtil.notBlank(request.getFunctionality().getName(), "functionality.name");
        ValidationUtil.notEmpty(request.getProperties(), "metrics");
    }
}
