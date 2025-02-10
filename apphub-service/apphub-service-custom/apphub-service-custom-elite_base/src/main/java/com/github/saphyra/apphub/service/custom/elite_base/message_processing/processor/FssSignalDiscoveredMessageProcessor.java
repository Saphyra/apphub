package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_signal_discovered.FssSignalDiscoveredMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
class FssSignalDiscoveredMessageProcessor implements MessageProcessor {
    private final ObjectMapperWrapper objectMapperWrapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_SIGNAL_DISCOVERED.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssSignalDiscoveredMessage fssSignalDiscoveredMessage = objectMapperWrapper.readValue(message.getMessage(), FssSignalDiscoveredMessage.class);

        starSystemSaver.save(
            fssSignalDiscoveredMessage.getTimestamp(),
            fssSignalDiscoveredMessage.getStarId(),
            fssSignalDiscoveredMessage.getStarName(),
            fssSignalDiscoveredMessage.getStarPosition()
        );
    }
}
