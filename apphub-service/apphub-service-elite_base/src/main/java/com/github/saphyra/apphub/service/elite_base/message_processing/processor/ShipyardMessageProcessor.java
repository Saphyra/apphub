package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.loadout.LoadoutType;
import com.github.saphyra.apphub.service.elite_base.message_processing.dao.star_system.StarSystem;
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
class ShipyardMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;
    private final PerformanceReporter performanceReporter;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.SHIPYARD.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ShipyardMessage shipyardMessage = objectMapperWrapper.readValue(message.getMessage(), ShipyardMessage.class);

        StarSystem starSystem = performanceReporter.wrap(
            () -> starSystemSaver.save(shipyardMessage.getTimestamp(), shipyardMessage.getSystemName()),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_SYSTEM.name()
        );

        StationSaveResult saveResult = performanceReporter.wrap(
            () -> stationSaverUtil.saveStationOrFleetCarrier(
                shipyardMessage.getTimestamp(),
                starSystem.getId(),
                shipyardMessage.getMarketId(),
                shipyardMessage.getStationName()
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_STATION.name()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        performanceReporter.wrap(
            () -> loadoutSaver.save(
                shipyardMessage.getTimestamp(),
                LoadoutType.SHIPYARD,
                saveResult.getCommodityLocation(),
                saveResult.getExternalReference(),
                shipyardMessage.getMarketId(),
                CollectionUtils.toList(shipyardMessage.getShips())
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_SHIPYARD_MESSAGE_SAVE_LOADOUT.name()
        );
    }
}
