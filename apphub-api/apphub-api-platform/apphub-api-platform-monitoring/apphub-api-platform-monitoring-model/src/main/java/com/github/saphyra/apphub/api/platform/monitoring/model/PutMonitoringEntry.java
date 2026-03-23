package com.github.saphyra.apphub.api.platform.monitoring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PutMonitoringEntry {
    private String service;
    private Feature feature;
    private Functionality getName;
    private Map<String, Double> properties = new HashMap<>();
}
