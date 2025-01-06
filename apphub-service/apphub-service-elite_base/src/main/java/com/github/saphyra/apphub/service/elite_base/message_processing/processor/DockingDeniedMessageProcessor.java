package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.docking_denied.DockingDeniedMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DockingDeniedMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StationSaverUtil stationSaverUtil;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.DOCKING_DENIED.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        DockingDeniedMessage dockingDeniedMessage = objectMapperWrapper.readValue(message.getMessage(), DockingDeniedMessage.class);

        stationSaverUtil.saveStationOrFleetCarrier(
            dockingDeniedMessage.getTimestamp(),
            null,
            null,
            dockingDeniedMessage.getStationType(),
            dockingDeniedMessage.getMarketId(),
            dockingDeniedMessage.getStationName(),
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
