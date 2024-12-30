package com.github.saphyra.apphub.service.elite_base.dao.station;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

//TODO unit test
interface StationRepository extends CrudRepository<StationEntity, String> {
    Optional<StationEntity> findByStarSystemIdAndStationName(String starSystemId, String stationName);
}
