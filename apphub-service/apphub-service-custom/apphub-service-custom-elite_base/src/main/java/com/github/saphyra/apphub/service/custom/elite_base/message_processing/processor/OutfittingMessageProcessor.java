package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.loadout.LoadoutType;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.outfitting.OutfittingMessage;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class OutfittingMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;
    private final PerformanceReporter performanceReporter;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.OUTFITTING.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        OutfittingMessage outfittingMessage = objectMapperWrapper.readValue(message.getMessage(), OutfittingMessage.class);

        StarSystem starSystem = performanceReporter.wrap(
            () -> starSystemSaver.save(outfittingMessage.getTimestamp(), outfittingMessage.getSystemName()),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_SYSTEM.name()
        );

        StationSaveResult saveResult = performanceReporter.wrap(
            () -> stationSaverUtil.saveStationOrFleetCarrier(
                outfittingMessage.getTimestamp(),
                starSystem.getId(),
                outfittingMessage.getMarketId(),
                outfittingMessage.getStationName()
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_STATION.name()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        performanceReporter.wrap(
            () -> loadoutSaver.save(
                outfittingMessage.getTimestamp(),
                LoadoutType.OUTFITTING,
                saveResult.getCommodityLocation(),
                saveResult.getExternalReference(),
                outfittingMessage.getMarketId(),
                CollectionUtils.toList(outfittingMessage.getModules())
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_LOADOUT.name()
        );
    }
}
