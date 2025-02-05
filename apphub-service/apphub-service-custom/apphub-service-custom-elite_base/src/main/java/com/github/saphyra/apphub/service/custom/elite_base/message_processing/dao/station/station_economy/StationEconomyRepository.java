package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.station.station_economy;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StationEconomyRepository extends CrudRepository<StationEconomyEntity, StationEconomyEntityId> {
    List<StationEconomyEntity> getByIdStationId(String stationId);
}
