package com.github.saphyra.apphub.service.feature.elite_base.processor.message_processing.processor;

import com.github.saphyra.apphub.api.feature.elite_base.message.EdMessage;

public interface MessageProcessor {
    boolean canProcess(EdMessage message);

    void processMessage(EdMessage message);
}
