package com.github.saphyra.apphub.service.elite_base.message_processing.processor;

import com.github.saphyra.apphub.service.elite_base.message_handling.dao.EdMessage;

public interface MessageProcessor{
    boolean canProcess(EdMessage message);

    void processMessage(EdMessage message);
}
