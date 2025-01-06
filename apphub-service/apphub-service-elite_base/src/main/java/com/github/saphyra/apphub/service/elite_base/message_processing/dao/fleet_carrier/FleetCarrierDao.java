package com.github.saphyra.apphub.service.elite_base.message_processing.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.AbstractDao;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
//TODO unit test
public class FleetCarrierDao extends AbstractDao<FleetCarrierEntity, FleetCarrier, String, FleetCarrierRepository> {

    FleetCarrierDao(FleetCarrierConverter converter, FleetCarrierRepository repository, UuidConverter uuidConverter) {
        super(converter, repository);
    }

    public Optional<FleetCarrier> findByCarrierId(String carrierId) {
        return converter.convertEntity(repository.findByCarrierId(carrierId));
    }

    public Optional<FleetCarrier> findByMarketId(Long marketId) {
        return converter.convertEntity(repository.findByMarketId(marketId));
    }
}
