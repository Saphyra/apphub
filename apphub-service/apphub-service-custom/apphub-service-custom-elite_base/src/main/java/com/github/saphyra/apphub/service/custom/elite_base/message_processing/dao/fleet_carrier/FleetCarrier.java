package com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.fleet_carrier;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class FleetCarrier {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private final String carrierId;
    private String carrierName;
    private UUID starSystemId;
    private FleetCarrierDockingAccess dockingAccess;
    private Long marketId;
}
