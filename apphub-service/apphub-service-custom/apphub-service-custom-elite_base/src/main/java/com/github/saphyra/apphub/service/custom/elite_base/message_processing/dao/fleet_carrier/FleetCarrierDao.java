package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FleetCarrierDao extends AbstractDao<FleetCarrierEntity, FleetCarrier, String, FleetCarrierRepository> {
    FleetCarrierDao(FleetCarrierConverter converter, FleetCarrierRepository repository) {
        super(converter, repository);
    }

    public Optional<FleetCarrier> findByCarrierId(String carrierId) {
        return converter.convertEntity(repository.findByCarrierId(carrierId));
    }

    public Optional<FleetCarrier> findByMarketId(Long marketId) {
        return converter.convertEntity(repository.findByMarketId(marketId));
    }
}
