package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_property;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MetricPropertyRepository extends CrudRepository<MetricPropertyEntity, MetricPropertyId> {
    @Modifying
    @Query("DELETE FROM MetricPropertyEntity e WHERE e.id.metricId NOT IN :metricIds")
    void deleteByMetricIdsNotIn(@Param("metricIds") List<String> metricIds);
}
