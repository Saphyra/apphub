package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface FleetCarrierRepository extends CrudRepository<FleetCarrierEntity, String> {
    Optional<FleetCarrierEntity> findByCarrierId(String carrierId);

    Optional<FleetCarrierEntity> findByMarketId(Long marketId);
}
