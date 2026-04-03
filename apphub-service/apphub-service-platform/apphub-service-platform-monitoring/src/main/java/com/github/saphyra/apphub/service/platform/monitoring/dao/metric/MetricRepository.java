package com.github.saphyra.apphub.service.platform.monitoring.dao.metric;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MetricRepository extends CrudRepository<MetricEntity, String> {
    @Modifying
    @Query("DELETE FROM MetricEntity e WHERE e.metricId NOT IN :metricIds")
    void deleteByMetricIdNotIn(@Param("metricIds") List<String> metricIds);
}
