package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.nav_beacon_scan.NavBeaconScanMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class NavBeaconScanMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.NAV_BEACON_SCAN.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        NavBeaconScanMessage navBeaconScanMessage = objectMapperWrapper.readValue(message.getMessage(), NavBeaconScanMessage.class);

        starSystemSaver.save(
            navBeaconScanMessage.getTimestamp(),
            navBeaconScanMessage.getStarId(),
            navBeaconScanMessage.getStarName(),
            navBeaconScanMessage.getStarPosition()
        );
    }
}
