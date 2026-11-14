package com.github.saphyra.apphub.api.platform.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PutMetricsRequestWrapper {
    private String service;
    private List<PutMetricsRequest> requests;
}
