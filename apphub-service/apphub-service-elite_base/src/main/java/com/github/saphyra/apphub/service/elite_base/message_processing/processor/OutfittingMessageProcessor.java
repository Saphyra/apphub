package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.LoadoutType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.outfitting.OutfittingMessage;
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
class OutfittingMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.OUTFITTING.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        OutfittingMessage outfittingMessage = objectMapperWrapper.readValue(message.getMessage(), OutfittingMessage.class);

        StarSystem starSystem = starSystemSaver.save(outfittingMessage.getTimestamp(), outfittingMessage.getSystemName());

        StationSaveResult saveResult = stationSaverUtil.saveStationOrFleetCarrier(
            outfittingMessage.getTimestamp(),
            starSystem.getId(),
            outfittingMessage.getMarketId(),
            outfittingMessage.getStationName()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        loadoutSaver.save(
            outfittingMessage.getTimestamp(),
            LoadoutType.OUTFITTING,
            saveResult.getCommodityLocation(),
            saveResult.getExternalReference(),
            outfittingMessage.getMarketId(),
            CollectionUtils.toList(outfittingMessage.getModules())
        );
    }
}
