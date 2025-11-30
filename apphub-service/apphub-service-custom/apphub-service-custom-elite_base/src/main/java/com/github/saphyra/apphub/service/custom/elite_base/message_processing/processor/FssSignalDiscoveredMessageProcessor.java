package com.github.saphyra.apphub.service.custom.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.custom.elite_base.message_handling.dao.EdMessage;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.saver.StarSystemSaver;
import com.github.saphyra.apphub.service.custom.elite_base.message_processing.structure.fss_signal_discovered.FssSignalDiscoveredMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
class FssSignalDiscoveredMessageProcessor implements MessageProcessor {
    private final ObjectMapper objectMapper;
    private final StarSystemSaver starSystemSaver;

    @Override
    public boolean canProcess(EdMessage message) {
        return SchemaRefs.FSS_SIGNAL_DISCOVERED.equals(message.getSchemaRef());
    }

    @Override
    public void processMessage(EdMessage message) {
        FssSignalDiscoveredMessage fssSignalDiscoveredMessage = objectMapper.readValue(message.getMessage(), FssSignalDiscoveredMessage.class);

        starSystemSaver.save(
            fssSignalDiscoveredMessage.getTimestamp(),
            fssSignalDiscoveredMessage.getStarId(),
            fssSignalDiscoveredMessage.getStarName(),
            fssSignalDiscoveredMessage.getStarPosition()
        );
    }
}
