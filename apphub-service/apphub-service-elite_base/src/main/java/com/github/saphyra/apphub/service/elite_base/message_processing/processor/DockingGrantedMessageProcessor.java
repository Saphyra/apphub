package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.docking_granted.DockingGrantedMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.util.StationSaverUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class DockingGrantedMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StationSaverUtil stationSaverUtil;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.DOCKING_GRANTED.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        DockingGrantedMessage dockingGrantedMessage = objectMapperWrapper.readValue(message.getMessage(), DockingGrantedMessage.class);

        stationSaverUtil.saveStationOrFleetCarrier(
            dockingGrantedMessage.getTimestamp(),
            dockingGrantedMessage.getStationType(),
            dockingGrantedMessage.getMarketId(),
            dockingGrantedMessage.getStationName()
        );
    }
}
