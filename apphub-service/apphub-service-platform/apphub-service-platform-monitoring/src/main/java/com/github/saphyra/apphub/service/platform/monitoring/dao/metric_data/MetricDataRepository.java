package com.github.saphyra.apphub.service.platform.monitoring.dao.metric_data;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

//TODO unit test
interface MetricDataRepository extends CrudRepository<MetricDataEntity, String> {
    @Query("DELETE FROM MetricDataEntity e WHERE e.type='HOUR' AND e.timestamp < :expiration")
    @Modifying
    void deleteByExpirationBefore(@Param("expiration") LocalDateTime expiration);

    @Query("SELECT e FROM MetricDataEntity e WHERE type = :type AND timestamp BETWEEN :expirationStart AND :expirationEnd")
    List<MetricDataEntity> getByTypeBetween(@Param("type") MetricDataType type, @Param("expirationStart") LocalDateTime expirationStart, @Param("expirationEnd") LocalDateTime expirationEnd);
}
