package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.dao.body.BodyType;
import com.github.saphyra.apphub.service.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.elite_base.message_processing.saver.StarSystemSaver;
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
    private final StarSystemSaver starSystemSaver;
    private final BodySaver bodySaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.CODEX_ENTRY.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        CodexEntryMessage codexEntryMessage = objectMapperWrapper.readValue(message.getMessage(), CodexEntryMessage.class);

        StarSystem starSystem = starSystemSaver.save(
            codexEntryMessage.getTimestamp(),
            codexEntryMessage.getStarId(),
            codexEntryMessage.getStarName(),
            codexEntryMessage.getStarPosition()
        );

        bodySaver.save(
            codexEntryMessage.getTimestamp(),
            starSystem.getId(),
            BodyType.WORLD,
            codexEntryMessage.getBodyId(),
            codexEntryMessage.getName()
        );
    }
}
