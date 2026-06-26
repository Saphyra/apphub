package com.github.saphyra.apphub.service.feature.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.dao.item.ItemType;
import com.github.saphyra.apphub.service.feature.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.structure.shipyard.outfitting.ShipyardMessage;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.feature.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class ShipyardMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;
    private final MonitoringInstruments monitoringInstruments;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.SHIPYARD.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ShipyardMessage shipyardMessage = objectMapper.readValue(message.getMessage(), ShipyardMessage.class);

        StarSystem starSystem = monitoringInstruments.wrap(
            () -> starSystemSaver.save(shipyardMessage.getTimestamp(), shipyardMessage.getSystemName()),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_SYSTEM.name()
        );

        StationSaveResult saveResult = monitoringInstruments.wrap(
            () -> stationSaverUtil.saveStationOrFleetCarrier(
                shipyardMessage.getTimestamp(),
                starSystem.getId(),
                shipyardMessage.getMarketId(),
                shipyardMessage.getStationName()
            ),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_STATION.name()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        monitoringInstruments.wrap(
            () -> loadoutSaver.save(
                shipyardMessage.getTimestamp(),
                ItemType.SPACESHIP,
                saveResult.getLocationType(),
                saveResult.getExternalReference(),
                shipyardMessage.getMarketId(),
                CollectionUtils.toList(shipyardMessage.getShips()),
                starSystem.getId()
            ),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_LOADOUT.name()
        );
    }
}
