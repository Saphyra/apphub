package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.TABLE_METRIC_DATA;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@Entity
@Table(schema = SCHEMA, name = TABLE_METRIC_DATA)
class MetricDataEntity {
    @Id
    private String metricDataId;
    private String metricId;
    private String service;
    @Enumerated(EnumType.STRING)
    private com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType type;
    private LocalDateTime timestamp;
    private String properties;
}
