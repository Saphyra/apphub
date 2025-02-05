package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface StationRepository extends CrudRepository<StationEntity, String> {
    Optional<StationEntity> findByStarSystemIdAndStationName(String starSystemId, String stationName);

    Optional<StationEntity> findByMarketId(Long marketId);
}
