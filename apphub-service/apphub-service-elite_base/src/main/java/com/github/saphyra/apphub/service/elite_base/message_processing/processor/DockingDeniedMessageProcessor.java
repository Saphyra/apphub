package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.StationType;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StationSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.docking_denied.DockingDeniedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class DockingDeniedMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StationSaver stationSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.DOCKING_DENIED.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        DockingDeniedMessage dockingDeniedMessage = objectMapperWrapper.readValue(message.getMessage(), DockingDeniedMessage.class);

        stationSaver.save(dockingDeniedMessage.getTimestamp(), dockingDeniedMessage.getMarketId(), StationType.parse(dockingDeniedMessage.getStationType()));
    }
}
