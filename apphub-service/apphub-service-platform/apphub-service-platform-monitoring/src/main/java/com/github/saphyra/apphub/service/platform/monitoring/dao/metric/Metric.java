package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
public class Metric {
    private final UUID metricId;
    private final Feature feature;
    private final String functionality;
}
