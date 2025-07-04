package com.github.saphyra.apphub.service.custom.elite_base.dao.station.station_economy;

import com.github.saphyra.apphub.lib.common_util.dao.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StationEconomyDao extends AbstractDao<StationEconomyEntity, StationEconomy, StationEconomyEntityId, StationEconomyRepository> {
    private final UuidConverter uuidConverter;

     StationEconomyDao(StationEconomyConverter converter, StationEconomyRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
         this.uuidConverter = uuidConverter;
     }

    public List<StationEconomy> getByStationId(UUID stationId) {
         return converter.convertEntity(repository.getByIdStationId(uuidConverter.convertDomain(stationId)));
    }
}
