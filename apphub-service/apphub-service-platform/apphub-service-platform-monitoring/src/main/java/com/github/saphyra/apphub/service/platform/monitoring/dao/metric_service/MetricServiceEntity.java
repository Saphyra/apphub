package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.TABLE_METRIC_SERVICE;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@Entity
@Table(schema = SCHEMA, name = TABLE_METRIC_SERVICE)
@IdClass(MetricServiceEntity.class)
class MetricServiceEntity {
    @Id
    private String metricId;
    @Id
    private String service;
}
