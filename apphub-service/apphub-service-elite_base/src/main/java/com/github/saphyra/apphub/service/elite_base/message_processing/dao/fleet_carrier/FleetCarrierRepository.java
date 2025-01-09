package com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface FleetCarrierRepository extends CrudRepository<FleetCarrierEntity, String> {
    Optional<FleetCarrierEntity> findByCarrierId(String carrierId);

    Optional<FleetCarrierEntity> findByMarketId(Long marketId);
}
