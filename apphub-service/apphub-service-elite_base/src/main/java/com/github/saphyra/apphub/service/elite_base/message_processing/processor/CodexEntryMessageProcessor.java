package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.codex_entry.CodexEntryMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CodexEntryMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.CODEX_ENTRY.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        CodexEntryMessage codexEntryMessage = objectMapperWrapper.readValue(message.getMessage(), CodexEntryMessage.class);

        //TODO implement
    }
}
