package com.github.saphyra.apphub.service.elite_base.message_processing.util;

import com.github.saphyra.apphub.service.elite_base.dao.Allegiance;
import com.github.saphyra.apphub.service.elite_base.dao.EconomyEnum;
import com.github.saphyra.apphub.service.elite_base.dao.FactionStateEnum;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.FleetCarrierSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StationSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.Economy;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.journal.ControllingFaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
public class StationSaverUtil {
    private final CommodityLocationFetcher commodityLocationFetcher;
    private final FleetCarrierSaver fleetCarrierSaver;
    private final StationSaver stationSaver;

    public StationSaveResult saveStationOrFleetCarrier(
        LocalDateTime timestamp,
        UUID starSystemId,
        UUID bodyId,
        String stationTypeStr,
        Long marketId,
        String stationName,
        Allegiance allegiance,
        EconomyEnum economy,
        String[] stationServices,
        Economy[] economies,
        String dockingAccess,
        ControllingFaction controllingFaction
    ) {
        StationType stationType = StationType.parse(stationTypeStr);

        CommodityLocation commodityLocation;
        if (StationType.FLEET_CARRIER == stationType) {
            commodityLocation = CommodityLocation.FLEET_CARRIER;
        } else if (EconomyEnum.CARRIER == economy) {
            commodityLocation = CommodityLocation.FLEET_CARRIER;
        } else if (nonNull(economies) && Arrays.stream(economies).map(e -> EconomyEnum.parse(e.getName())).anyMatch(r -> r == EconomyEnum.CARRIER)) {
            commodityLocation = CommodityLocation.FLEET_CARRIER;
        } else {
            commodityLocation = Optional.ofNullable(stationType)
                .map(st -> st == StationType.FLEET_CARRIER ? CommodityLocation.FLEET_CARRIER : CommodityLocation.STATION)
                .orElseGet(() -> commodityLocationFetcher.fetchCommodityLocationByMarketId(marketId));
        }

        UUID externalReference = switch (commodityLocation) {
            case FLEET_CARRIER -> fleetCarrierSaver.save(
                timestamp,
                starSystemId,
                stationName,
                null,
                FleetCarrierDockingAccess.parse(dockingAccess),
                marketId
            ).getId();
            case STATION -> stationSaver.save(
                timestamp,
                starSystemId,
                bodyId,
                stationName,
                stationType,
                marketId,
                allegiance,
                economy,
                stationServices,
                economies,
                Optional.ofNullable(controllingFaction).map(ControllingFaction::getFactionName).orElse(null),
                Optional.ofNullable(controllingFaction).map(ControllingFaction::getEconomicState).map(FactionStateEnum::parse).orElse(null)
            ).getId();
            case UNKNOWN -> null;
            default -> throw new IllegalStateException("Unhandled " + CommodityLocation.class.getSimpleName() + ": " + commodityLocation);
        };

        if (isNull(externalReference)) {
            log.warn("Could not determine stationType for marketId {}", marketId);
        }

        return StationSaveResult.builder()
            .externalReference(externalReference)
            .commodityLocation(commodityLocation)
            .build();
    }
}
