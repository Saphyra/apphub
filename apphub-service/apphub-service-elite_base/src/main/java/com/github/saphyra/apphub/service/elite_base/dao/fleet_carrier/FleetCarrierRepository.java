package com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
interface FleetCarrierRepository extends CrudRepository<FleetCarrierEntity, String> {
    Optional<FleetCarrierEntity> findByCarrierId(String carrierId);

    Optional<FleetCarrierEntity> findByMarketId(Long marketId);
}
