package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityLocation;
import com.github.saphyra.apphub.service.elite_base.dao.fleet_carrier.FleetCarrierDockingAccess;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.FleetCarrierSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StationSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.commodity.CommodityMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaver stationSaver;
    private final CommoditySaver commoditySaver;
    private final FleetCarrierSaver fleetCarrierSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.COMMODITY.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        CommodityMessage commodityMessage = objectMapperWrapper.readValue(message.getMessage(), CommodityMessage.class);

        StarSystem starSystem = starSystemSaver.save(
            commodityMessage.getTimestamp(),
            commodityMessage.getSystemName()
        );

        StationType stationType = StationType.parse(commodityMessage.getStationType());
        CommodityLocation commodityLocation = stationType == StationType.FLEET_CARRIER ? CommodityLocation.FLEET_CARRIER : CommodityLocation.STATION;

        UUID externalReference = switch (commodityLocation) {
            case FLEET_CARRIER -> fleetCarrierSaver.save(
                commodityMessage.getTimestamp(),
                starSystem.getId(),
                commodityMessage.getStationName(),
                null,
                FleetCarrierDockingAccess.parse(commodityMessage.getCarrierDockingAccess()),
                commodityMessage.getMarketId()
            ).getId();
            case STATION -> stationSaver.save(
                commodityMessage.getTimestamp(),
                starSystem.getId(),
                commodityMessage.getStationName(),
                stationType,
                commodityMessage.getMarketId(),
                commodityMessage.getEconomies()
            ).getId();
            default -> throw new IllegalStateException("Unhandled " + CommodityLocation.class.getSimpleName() + ": " + commodityLocation);
        };

        commoditySaver.saveAll(commodityMessage.getTimestamp(), commodityLocation, externalReference, commodityMessage.getMarketId(), commodityMessage.getCommodities());
    }
}
