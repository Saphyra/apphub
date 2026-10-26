package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.api.platform.monitoring.model.Feature;
import com.github.saphyra.apphub.lib.monitoring.instrument.MonitoringInstruments;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.feature.elite_base.processor.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.ObjectType;
import com.github.saphyra.apphub.service.feature.elite_base.processor.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.saver.LoadoutSaver;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.outfitting.OutfittingV3Message;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.outfitting.OutfittingV3Module;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class OutfittingV3MessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final MonitoringInstruments monitoringInstruments;
    private final StarSystemSaver starSystemSaver;
    private final StationSaverUtil stationSaverUtil;
    private final LoadoutSaver loadoutSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.OUTFITTING_V3.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        OutfittingV3Message outfittingMessage = objectMapper.readValue(message.getMessage(), OutfittingV3Message.class);

        StarSystem starSystem = monitoringInstruments.wrap(
            () -> starSystemSaver.save(outfittingMessage.getTimestamp(), outfittingMessage.getSystemName()),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_SYSTEM.name()
        );

        StationSaveResult saveResult = monitoringInstruments.wrap(
            () -> stationSaverUtil.saveStationOrFleetCarrier(
                outfittingMessage.getTimestamp(),
                starSystem.getId(),
                outfittingMessage.getMarketId(),
                outfittingMessage.getStationName()
            ),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_STATION.name()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        monitoringInstruments.wrap(
            () -> loadoutSaver.save(
                outfittingMessage.getTimestamp(),
                ObjectType.EQUIPMENT,
                saveResult.getLocationType(),
                saveResult.getExternalReference(),
                outfittingMessage.getMarketId(),
                Arrays.stream(outfittingMessage.getModules()).map(OutfittingV3Module::getName).toList(),
                starSystem.getId()
            ),
            Feature.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_OUTFITTING_MESSAGE_SAVE_LOADOUT.name()
        );
    }
}
