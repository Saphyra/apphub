package com.github.saphyra.apphub.service.elite_base.message_processing.dao.station;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class StationDao extends AbstractDao<StationEntity, Station, String, StationRepository> {
    private final UuidConverter uuidConverter;

    StationDao(StationConverter converter, StationRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<Station> findByStarSystemIdAndStationName(UUID starSystemId, String stationName) {
        return converter.convertEntity(repository.findByStarSystemIdAndStationName(uuidConverter.convertDomain(starSystemId), stationName));
    }

    public Optional<Station> findByMarketId(Long marketId) {
        return converter.convertEntity(repository.findByMarketId(marketId));
    }
}
