package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.scan_bary_centre.ScanBaryCentreMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class ScanBaryCentreMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.SCAN_BARY_CENTRE.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ScanBaryCentreMessage scanBaryCentreMessage = objectMapperWrapper.readValue(message.getMessage(), ScanBaryCentreMessage.class);

        starSystemSaver.save(
            scanBaryCentreMessage.getTimestamp(),
            scanBaryCentreMessage.getStarId(),
            scanBaryCentreMessage.getStarName(),
            scanBaryCentreMessage.getStarPosition()
        );
    }
}
