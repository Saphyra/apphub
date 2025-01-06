package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.elite_base.dao.loadout.LoadoutType;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.shipyard.outfitting.ShipyardMessage;
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
class ShipyardMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.SHIPYARD.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ShipyardMessage shipyardMessage = objectMapperWrapper.readValue(message.getMessage(), ShipyardMessage.class);

        StarSystem starSystem = starSystemSaver.save(shipyardMessage.getTimestamp(), shipyardMessage.getSystemName());

        StationSaveResult saveResult = stationSaverUtil.saveStationOrFleetCarrier(
            shipyardMessage.getTimestamp(),
            starSystem.getId(),
            shipyardMessage.getMarketId(),
            shipyardMessage.getStationName()
        );

        if (isNull(saveResult.getExternalReference())) {
            return;
        }

        loadoutSaver.save(
            shipyardMessage.getTimestamp(),
            LoadoutType.SHIPYARD,
            saveResult.getCommodityLocation(),
            saveResult.getExternalReference(),
            shipyardMessage.getMarketId(),
            CollectionUtils.toList(shipyardMessage.getShips())
        );
    }
}
