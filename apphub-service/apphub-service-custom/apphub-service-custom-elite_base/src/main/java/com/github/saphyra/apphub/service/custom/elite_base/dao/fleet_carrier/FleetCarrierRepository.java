package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface FleetCarrierRepository extends CrudRepository<FleetCarrierEntity, String> {
    Optional<FleetCarrierEntity> findByCarrierId(String carrierId);

    Optional<FleetCarrierEntity> findByMarketId(Long marketId);

    List<FleetCarrierEntity> getByCarrierIdOrMarketId(String carrierId, Long marketId);

    @Query("UPDATE FleetCarrierEntity e SET e.marketId=null WHERE e.marketId = :marketId AND e.id != :id")
    @Modifying
    @Transactional
    void clearMarketId(@Param("id") String id, @Param("marketId") Long marketId);
}
