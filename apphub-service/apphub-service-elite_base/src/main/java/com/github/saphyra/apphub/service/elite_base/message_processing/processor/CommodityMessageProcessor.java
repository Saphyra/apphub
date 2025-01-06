package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.commodity.CommodityMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CommodityMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final CommoditySaver commoditySaver;
    private final StationSaverUtil stationSaverUtil;

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

        StationSaveResult saveResult = stationSaverUtil.saveStationOrFleetCarrier(
            commodityMessage.getTimestamp(),
            starSystem.getId(),
            null,
            commodityMessage.getStationType(),
            commodityMessage.getMarketId(),
            commodityMessage.getStationName(),
            null,
            null,
            null,
            null,
            commodityMessage.getCarrierDockingAccess(),
            null
        );

        if (isNull(saveResult.getExternalReference())) {
            return;
        }

        commoditySaver.saveAll(
            commodityMessage.getTimestamp(),
            CommodityType.COMMODITY,
            saveResult.getCommodityLocation(),
            saveResult.getExternalReference(),
            commodityMessage.getMarketId(),
            commodityMessage.getCommodities()
        );
    }
}
