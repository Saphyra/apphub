package com.github.saphyra.apphub.service.custom.elite_base.dao.fleet_carrier;

import com.github.saphyra.apphub.service.custom.elite_base.dao.ItemLocationData;
import com.github.saphyra.apphub.service.custom.elite_base.dao.StationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Data
@Builder
public class FleetCarrier implements ItemLocationData {
    private final UUID id;
    private LocalDateTime lastUpdate;
    private String carrierId;
    private String carrierName;
    private UUID starSystemId;
    private FleetCarrierDockingAccess dockingAccess;
    private Long marketId;

    @Override
    public StationType getType() {
        return StationType.FLEET_CARRIER;
    }

    @Override
    public String getName() {
        return Stream.of(carrierId, carrierName)
            .filter(Objects::nonNull)
            .collect(Collectors.joining(" - "));
    }

    @Override
    public UUID getBodyId() {
        return null;
    }
}
