package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.structure.scan_bary_centre.ScanBaryCentreMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class ScanBaryCentreMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.SCAN_BARY_CENTRE.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        ScanBaryCentreMessage scanBaryCentreMessage = objectMapper.readValue(message.getMessage(), ScanBaryCentreMessage.class);

        starSystemSaver.save(
            scanBaryCentreMessage.getTimestamp(),
            scanBaryCentreMessage.getStarId(),
            scanBaryCentreMessage.getStarName(),
            scanBaryCentreMessage.getStarPosition()
        );
    }
}
