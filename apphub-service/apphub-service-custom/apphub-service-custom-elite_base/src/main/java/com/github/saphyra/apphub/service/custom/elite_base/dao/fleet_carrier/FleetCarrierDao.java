package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.ExceptionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.Objects.isNull;

@Component
public class FleetCarrierDao extends AbstractDao<FleetCarrierEntity, FleetCarrier, String, FleetCarrierRepository> {
    private final UuidConverter uuidConverter;

    FleetCarrierDao(FleetCarrierConverter converter, FleetCarrierRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
        this.uuidConverter = uuidConverter;
    }

    public Optional<FleetCarrier> findByCarrierId(String carrierId) {
        return converter.convertEntity(repository.findByCarrierId(carrierId));
    }

    public Optional<FleetCarrier> findByMarketId(Long marketId) {
        return converter.convertEntity(repository.findByMarketId(marketId));
    }

    public List<FleetCarrier> findAllById(List<UUID> fleetCarrierIds) {
        List<FleetCarrierEntity> entities = StreamSupport.stream(repository.findAllById(uuidConverter.convertDomain(fleetCarrierIds)).spliterator(), false)
            .toList();
        return converter.convertEntity(entities);
    }

    public Optional<FleetCarrier> findByCarrierIdOrMarketId(String carrierId, Long marketId) {
        if (isNull(carrierId)) {
            return findByMarketId(marketId);
        }

        if (isNull(marketId)) {
            return findByCarrierId(carrierId);
        }

        List<FleetCarrier> result = converter.convertEntity(repository.getByCarrierIdOrMarketId(carrierId, marketId));

        if (result.size() > 1) {
            throw ExceptionFactory.notLoggedException(HttpStatus.INTERNAL_SERVER_ERROR, "Multiple FleetCarriers found for carrierId %s and marketId %s: %s".formatted(carrierId, marketId, result));
        }

        return result.stream()
            .findAny();
    }
}
