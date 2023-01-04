package com.github.saphyra.apphub.api.platform.message_sender.server;

import com.github.saphyra.apphub.lib.config.common.Endpoints;
import org.springframework.web.bind.annotation.PostMapping;

public interface MessageSenderEventController {
    @PostMapping(Endpoints.EVENT_MESSAGE_SENDER_PING_REQUEST)
    void sendPingRequests();

    @PostMapping(Endpoints.EVENT_MESSAGE_SENDER_CONNECTION_CLEANUP)
    void connectionCleanup();
}
