package com.github.saphyra.apphub.service.feature.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.converter.ConverterBase;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FleetCarrierConverter extends ConverterBase<FleetCarrierEntity, FleetCarrier> {
    private final UuidConverter uuidConverter;

    @Override
    protected FleetCarrierEntity processDomainConversion(FleetCarrier domain) {
        return FleetCarrierEntity.builder()
            .id(uuidConverter.convertDomain(domain.getId()))
            .carrierId(domain.getCarrierId())
            .carrierName(domain.getCarrierName())
            .starSystemId(uuidConverter.convertDomain(domain.getStarSystemId()))
            .dockingAccess(domain.getDockingAccess())
            .marketId(domain.getMarketId())
            .build();
    }

    @Override
    protected FleetCarrier processEntityConversion(FleetCarrierEntity entity) {
        return FleetCarrier.builder()
            .id(uuidConverter.convertEntity(entity.getId()))
            .carrierId(entity.getCarrierId())
            .carrierName(entity.getCarrierName())
            .starSystemId(uuidConverter.convertEntity(entity.getStarSystemId()))
            .dockingAccess(entity.getDockingAccess())
            .marketId(entity.getMarketId())
            .build();
    }
}
