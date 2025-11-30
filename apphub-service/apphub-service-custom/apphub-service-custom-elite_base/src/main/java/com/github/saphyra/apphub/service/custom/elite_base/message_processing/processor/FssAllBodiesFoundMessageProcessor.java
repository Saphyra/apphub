package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_all_bodies_found.FssAllBodiesFoundMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class FssAllBodiesFoundMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_ALL_BODIES_FOUND.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssAllBodiesFoundMessage fssAllBodiesFoundMessage = objectMapper.readValue(message.getMessage(), FssAllBodiesFoundMessage.class);

        starSystemSaver.save(
            fssAllBodiesFoundMessage.getTimestamp(),
            fssAllBodiesFoundMessage.getStarId(),
            fssAllBodiesFoundMessage.getStarName(),
            fssAllBodiesFoundMessage.getStarPosition()
        );
    }
}
