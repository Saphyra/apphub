package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.api.admin_panel.model.model.performance_reporting.PerformanceReportingTopic;
import com.github.saphyra.apphub.lib.performance_reporting.PerformanceReporter;
import com.github.saphyra.apphub.service.custom.elite_base.common.MessageProcessingDelayedException;
import com.github.saphyra.apphub.service.custom.elite_base.common.PerformanceReportingKey;
import com.github.saphyra.apphub.service.custom.elite_base.dao.commodity.CommodityType;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.CommoditySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.commodity.CommodityMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaveResult;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@Slf4j
class CommodityMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;
    private final CommoditySaver commoditySaver;
    private final StationSaverUtil stationSaverUtil;
    private final PerformanceReporter performanceReporter;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.COMMODITY.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        CommodityMessage commodityMessage = objectMapper.readValue(message.getMessage(), CommodityMessage.class);

        StarSystem starSystem = performanceReporter.wrap(
            () -> starSystemSaver.save(
                commodityMessage.getTimestamp(),
                commodityMessage.getSystemName()
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_COMMODITY_MESSAGE_SAVE_STAR_SYSTEM.name()
        );

        StationSaveResult saveResult = performanceReporter.wrap(
            () -> stationSaverUtil.saveStationOrFleetCarrier(
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
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_COMMODITY_MESSAGE_SAVE_STATION.name()
        );

        if (isNull(saveResult.getExternalReference())) {
            throw new MessageProcessingDelayedException("ExternalReference is null.");
        }

        performanceReporter.wrap(
            () -> commoditySaver.saveAll(
                commodityMessage.getTimestamp(),
                CommodityType.COMMODITY,
                saveResult.getCommodityLocation(),
                saveResult.getExternalReference(),
                commodityMessage.getMarketId(),
                commodityMessage.getCommodities()
            ),
            PerformanceReportingTopic.ELITE_BASE_MESSAGE_PROCESSING,
            PerformanceReportingKey.PROCESS_COMMODITY_MESSAGE_SAVE_COMMODITIES.name()
        );
    }
}
