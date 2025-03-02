package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_service;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StationServiceRepository extends CrudRepository<StationServiceEntity, StationServiceEntity> {
    List<StationServiceEntity> getByStationId(String stationId);
}
