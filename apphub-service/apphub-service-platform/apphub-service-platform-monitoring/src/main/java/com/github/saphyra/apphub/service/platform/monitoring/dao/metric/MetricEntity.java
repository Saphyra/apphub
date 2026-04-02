package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
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

import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.TABLE_METRIC;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@Entity
@Table(schema = SCHEMA, name = TABLE_METRIC)
class MetricEntity {
    @Id
    private String metricId;
    @Enumerated(EnumType.STRING)
    private Feature feature;
    private String functionality;
}
