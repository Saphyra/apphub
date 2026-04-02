package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class MetricPropertyId implements Serializable {
    private String metricId;
    private String property;
}
