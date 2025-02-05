package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station.station_service;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface StationServiceRepository extends CrudRepository<StationServiceEntity, StationServiceEntity> {
    List<StationServiceEntity> getByStationId(String stationId);
}
