package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import com.github.saphyra.apphub.api.platform.monitoring.model.AggregationStrategy;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.SCHEMA;
import static com.github.saphyra.apphub.service.platform.monitoring.dao.DatabaseConstants.TABLE_METRIC_PROPERTY;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Data
@Entity
@Table(schema = SCHEMA, name = TABLE_METRIC_PROPERTY)
class MetricPropertyEntity {
    @EmbeddedId
    private MetricPropertyId id;
    @Enumerated(EnumType.STRING)
    private AggregationStrategy aggregationStrategy;
}
