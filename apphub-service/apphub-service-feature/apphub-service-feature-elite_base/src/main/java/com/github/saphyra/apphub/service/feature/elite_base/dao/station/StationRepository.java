package com.github.saphyra.apphub.service.feature.elite_base.dao.station;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface StationRepository extends CrudRepository<StationEntity, String> {
    Optional<StationEntity> findByStarSystemIdAndStationName(String starSystemId, String stationName);

    Optional<StationEntity> findByMarketId(Long marketId);

    //TODO unit test
    //TODO index
    List<StationEntity> getByStarSystemId(String starSystemId);
}
