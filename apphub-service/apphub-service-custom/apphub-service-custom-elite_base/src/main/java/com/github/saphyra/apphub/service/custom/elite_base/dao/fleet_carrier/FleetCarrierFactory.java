package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.lib.common_util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FleetCarrierFactory {
    private final IdGenerator idGenerator;

    public FleetCarrier create(String carrierId, LocalDateTime timestamp, String carrierName, UUID starSystemId, FleetCarrierDockingAccess dockingAccess, Long marketId) {
        return FleetCarrier.builder()
            .id(idGenerator.randomUuid())
            .carrierId(carrierId)
            .lastUpdate(timestamp)
            .carrierName(carrierName)
            .starSystemId(starSystemId)
            .dockingAccess(dockingAccess)
            .marketId(marketId)
            .build();
    }
}
