package com.github.saphyra.apphub.service.custom.elite_base.dao.station;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

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

    public List<Station> findAllById(List<UUID> stationIds) {
        List<StationEntity> entities = StreamSupport.stream(repository.findAllById(uuidConverter.convertDomain(stationIds)).spliterator(), false)
            .toList();
        return converter.convertEntity(entities);
    }
}
