package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_discovery_scan.FssDiscoveryScanMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FssDiscoveryScanMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_DISCOVERY_SCAN.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssDiscoveryScanMessage fssDiscoveryScanMessage = objectMapperWrapper.readValue(message.getMessage(), FssDiscoveryScanMessage.class);

        starSystemSaver.save(
            fssDiscoveryScanMessage.getTimestamp(),
            fssDiscoveryScanMessage.getStarId(),
            fssDiscoveryScanMessage.getStarName(),
            fssDiscoveryScanMessage.getStarPosition()
        );
    }
}
