package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.elite_base.message_processing.structure.fss_all_bodies_found.FssAllBodiesFoundMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class FssAllBodiesFoundMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_ALL_BODIES_FOUND.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssAllBodiesFoundMessage fssAllBodiesFoundMessage = objectMapperWrapper.readValue(message.getMessage(), FssAllBodiesFoundMessage.class);

        //TODO implement
    }
}
