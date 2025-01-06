package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_economy;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface StationEconomyRepository extends CrudRepository<StationEconomyEntity, String> {
    List<StationEconomyEntity> getByStationId(String stationId);
}
