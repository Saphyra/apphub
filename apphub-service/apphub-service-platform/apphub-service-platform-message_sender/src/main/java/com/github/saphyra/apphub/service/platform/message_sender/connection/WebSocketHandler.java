package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;
import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;

import java.util.List;
import java.util.UUID;

public interface WebSocketHandler {
    MessageGroup getGroup();

    List<UUID> sendEvent(WebSocketMessage message);
}
