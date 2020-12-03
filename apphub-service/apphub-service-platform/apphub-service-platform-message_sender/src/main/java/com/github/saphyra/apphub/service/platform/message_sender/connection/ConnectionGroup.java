package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;

import java.util.UUID;

public interface ConnectionGroup {
    MessageGroup getGroup();

    void sendEvent(UUID id, Object message);
}
