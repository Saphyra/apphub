package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.dao.star_system.StarSystem;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_body_signals.FssBodySignalsMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.BodySaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FssBodySignalsMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;
    private final BodySaver bodySaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_BODY_SIGNALS.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssBodySignalsMessage fssBodySignalsMessage = objectMapperWrapper.readValue(message.getMessage(), FssBodySignalsMessage.class);

        StarSystem starSystem = starSystemSaver.save(
            fssBodySignalsMessage.getTimestamp(),
            fssBodySignalsMessage.getStarId(),
            fssBodySignalsMessage.getStarName(),
            fssBodySignalsMessage.getStarPosition()
        );

        bodySaver.save(
            fssBodySignalsMessage.getTimestamp(),
            starSystem.getId(),
            null,
            fssBodySignalsMessage.getBodyId(),
            fssBodySignalsMessage.getBodyName()
        );
    }
}
