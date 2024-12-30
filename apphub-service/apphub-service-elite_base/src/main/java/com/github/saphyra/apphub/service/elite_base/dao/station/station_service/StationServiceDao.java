package com.github.saphyra.apphub.service.elite_base.dao.station.station_service;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
//TODO unit test
public class StationServiceDao extends AbstractDao<StationServiceEntity, StationService, String, StationServiceRepository> {
    private final UuidConverter uuidConverter;

    StationServiceDao(StationServiceConverter converter, StationServiceRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public List<StationService> getByStationId(UUID stationId) {
        return converter.convertEntity(repository.getByStationId(uuidConverter.convertDomain(stationId)));
    }
}
