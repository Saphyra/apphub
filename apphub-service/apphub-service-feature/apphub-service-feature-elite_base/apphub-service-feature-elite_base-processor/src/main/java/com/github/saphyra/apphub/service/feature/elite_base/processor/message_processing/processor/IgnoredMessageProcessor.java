package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
class IgnoredMessageProcessor implements MessageProcessor {
    private static final List<String> IGNORED_SCHEMAS = List.of(
        SchemaRefs.SCAN_ORGANIC
    );

    @Override
    public boolean canProcess(EdMessage message) {
        return IGNORED_SCHEMAS.contains(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        log.debug("Ignoring message {}", message);
    }
}
