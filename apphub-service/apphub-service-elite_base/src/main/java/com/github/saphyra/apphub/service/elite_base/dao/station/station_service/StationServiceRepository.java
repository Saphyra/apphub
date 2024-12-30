package com.github.saphyra.apphub.service.elite_base.dao.station.station_service;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

//TODO unit test
interface StationServiceRepository extends CrudRepository<StationServiceEntity, String> {
    List<StationServiceEntity> getByStationId(String stationId);
}
