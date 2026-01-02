package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.nav_beacon_scan.NavBeaconScanMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class NavBeaconScanMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.NAV_BEACON_SCAN.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        NavBeaconScanMessage navBeaconScanMessage = objectMapper.readValue(message.getMessage(), NavBeaconScanMessage.class);

        starSystemSaver.save(
            navBeaconScanMessage.getTimestamp(),
            navBeaconScanMessage.getStarId(),
            navBeaconScanMessage.getStarName(),
            navBeaconScanMessage.getStarPosition()
        );
    }
}
