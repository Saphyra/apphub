package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Component
public class FleetCarrierDao extends AbstractDao<FleetCarrierEntity, FleetCarrier, String, FleetCarrierRepository> {
    private final UuidConverter uuidConverter;

    FleetCarrierDao(FleetCarrierConverter converter, FleetCarrierRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<FleetCarrier> findByMarketId(Long marketId) {
        return converter.convertEntity(repository.findByMarketId(marketId));
    }

    public List<FleetCarrier> findAllById(List<UUID> fleetCarrierIds) {
        List<FleetCarrierEntity> entities = StreamSupport.stream(repository.findAllById(uuidConverter.convertDomain(fleetCarrierIds)).spliterator(), false)
            .toList();
        return converter.convertEntity(entities);
    }
}
