package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_service;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MetricServiceRepository extends CrudRepository<MetricServiceEntity, MetricServiceEntity> {
    @Modifying
    @Query("DELETE FROM MetricServiceEntity e WHERE e.service NOT IN :services")
    void deleteByServiceNotIn(@Param("services") List<String> services);

    @Modifying
    @Query("DELETE FROM MetricServiceEntity e WHERE e.metricId NOT IN :metricIds")
    void deleteByMetricIdsNotIn(@Param("metricIds") List<String> metricIds);
}
