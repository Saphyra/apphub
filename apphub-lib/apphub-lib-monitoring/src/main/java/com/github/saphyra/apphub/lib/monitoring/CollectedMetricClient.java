package com.github.saphyra.apphub.lib.monitoring;

import com.github.saphyra.apphub.api.platform.monitoring.model.PutMetricsRequest;

import java.util.List;

public interface CollectedMetricClient {
    void send(List<PutMetricsRequest> requests);
}
