package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import com.github.saphyra.apphub.api.platform.monitoring.model.MetricDataType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//TODO unit test
interface MetricDataRepository extends CrudRepository<MetricDataEntity, String> {
    @Query("DELETE FROM MetricDataEntity e WHERE e.type='HOUR' AND e.timestamp < :expiration")
    @Modifying
    void deleteByExpirationBefore(@Param("expiration") LocalDateTime expiration);

    @Query("SELECT e FROM MetricDataEntity e WHERE type = :type AND timestamp BETWEEN :expirationStart AND :expirationEnd")
    List<MetricDataEntity> getByTypeBetween(@Param("type") MetricDataType type, @Param("expirationStart") LocalDateTime expirationStart, @Param("expirationEnd") LocalDateTime expirationEnd);

    @Query("SELECT e FROM MetricDataEntity e WHERE e.type = :type AND e.metricId IN (:metricIds) AND (:service IS NULL OR e.service = :service) AND e.timestamp >= :timestamp")
    List<MetricDataEntity> getByTypeAndMetricIdInAndServiceAfter(
        @Param("type") MetricDataType type,
        @Param("metricIds") List<String> metricIds,
        @Param("service") String service,
        @Param("timestamp") LocalDateTime timestamp
    );

    Optional<MetricDataEntity> findFirstByTypeOrderByTimestampAsc(MetricDataType type);

    @Query("SELECT e.service FROM MetricDataEntity e GROUP BY e.service")
    List<String> getServices();

    @Query("SELECT e.metricId from MetricDataEntity e GROUP BY e.metricId")
    List<String> getMetricIds();
}
