package com.github.saphyra.apphub.service.platform.message_sender.connection;

import com.github.saphyra.apphub.api.platform.message_sender.model.MessageGroup;
import com.github.saphyra.apphub.api.platform.message_sender.model.WebSocketMessage;

public interface WebSocketHandler {
    MessageGroup getGroup();

    void sendEvent(WebSocketMessage message);

    void sendPingRequest();

    void cleanUp();
}
